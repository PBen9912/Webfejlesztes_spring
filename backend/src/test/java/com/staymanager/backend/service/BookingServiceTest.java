package com.staymanager.backend.service;

import com.staymanager.backend.dto.BookingCreateRequest;
import com.staymanager.backend.dto.BookingResponse;
import com.staymanager.backend.entity.Accommodation;
import com.staymanager.backend.entity.Booking;
import com.staymanager.backend.entity.BookingStatus;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccommodationService accommodationService;

    @InjectMocks
    private BookingService bookingService;

    private BookingCreateRequest request;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        request = new BookingCreateRequest();
        request.setAccommodationId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuestCount(2);
        request.setGuestName("Test Guest");
        request.setGuestEmail("test@example.com");

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setCapacity(4);
        accommodation.setPricePerNight(new BigDecimal("25000"));
    }

    static Stream<Arguments> invalidBookingDates() {

        LocalDate today = LocalDate.now();

        return Stream.of(
                Arguments.of(null, today.plusDays(3)),
                Arguments.of(today, null),
                Arguments.of(today.minusDays(1), today.plusDays(3)),
                Arguments.of(today.plusDays(3), today.plusDays(1)),
                Arguments.of(today, today)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBookingDates")
    void createBookingShouldThrowWhenDatesAreInvalid(LocalDate checkIn, LocalDate checkOut) {

        request.setAccommodationId(1L);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        accommodation.setId(1L);

        when(accommodationService.getAccommodationById(request.getAccommodationId())).thenReturn(accommodation);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(request, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid booking dates", exception.getReason());

        verifyNoInteractions(bookingRepository);

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0, 5})
    void createBookingShouldThrowWhenGuestCountIsInvalid(Integer guestCount) {

        request.setGuestCount(guestCount);

        when(accommodationService.getAccommodationById(request.getAccommodationId())).thenReturn(accommodation);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(request, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid guest count", exception.getReason());

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBookingShouldThrowWhenBookingOverlaps() {

        when(accommodationService.getAccommodationById(request.getAccommodationId())).thenReturn(accommodation);

        when(bookingRepository.existsOverlappingBooking(accommodation.getId(), request.getCheckIn(), request.getCheckOut(), BookingStatus.CONFIRMED)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(request, null));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("A szállás a kiválasztott időszakban már foglalt.", exception.getReason());

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void createBookingShouldCreateBookingSuccessfully(boolean authenticated) {

        User user = null;

        if (authenticated) {
            user = new User();
            user.setId(1L);
        }

        request.setGuestName("  Test Guest  ");
        request.setGuestEmail("  test@example.com  ");

        when(accommodationService.getAccommodationById(request.getAccommodationId())).thenReturn(accommodation);

        when(bookingRepository.existsOverlappingBooking(accommodation.getId(), request.getCheckIn(), request.getCheckOut(), BookingStatus.CONFIRMED)).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation ->
                {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(10L);
                    return booking;
                });

        BookingResponse result = bookingService.createBooking(request, user);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();

        assertEquals(accommodation, savedBooking.getAccommodation());
        assertEquals(user, savedBooking.getUser());
        assertEquals("Test Guest", savedBooking.getGuestName());
        assertEquals("test@example.com", savedBooking.getGuestEmail());
        assertEquals(request.getCheckIn(), savedBooking.getCheckIn());
        assertEquals(request.getCheckOut(), savedBooking.getCheckOut());
        assertEquals(request.getGuestCount(), savedBooking.getGuestCount());
        assertEquals(new BigDecimal("50000"), savedBooking.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());

        assertEquals(10L, result.getId());
        assertEquals(accommodation.getId(), result.getAccommodationId());
        assertEquals("Test Guest", result.getGuestName());
        assertEquals("test@example.com", result.getGuestEmail());
        assertEquals(request.getCheckIn(), result.getCheckIn());
        assertEquals(request.getCheckOut(), result.getCheckOut());
        assertEquals(request.getGuestCount(), result.getGuestCount());
        assertEquals(new BigDecimal("50000"), result.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());

        if (authenticated) {
            assertEquals(user.getId(), result.getUserId());
        } else {
            assertNull(result.getUserId());
        }
    }

    private Booking createExistingBooking(User user, BookingStatus status) {

        Booking booking = new Booking();

        booking.setId(10L);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setGuestCount(request.getGuestCount());
        booking.setTotalPrice(new BigDecimal("50000"));
        booking.setStatus(status);

        return booking;
    }

    @Test
    void cancelBookingShouldThrowWhenBookingNotFound() {

        Long bookingId = 10L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookingService.cancelBooking(bookingId, userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Booking not found", exception.getReason());
    }

    @Test
    void cancelBookingShouldThrowWhenBookingHasNoUser() {

        Booking booking = createExistingBooking(null, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> bookingService.cancelBooking(booking.getId(), 1L));

        assertEquals("You cannot cancel this booking", exception.getMessage());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void cancelBookingShouldThrowWhenUserIsNotOwner() {

        User owner = new User();
        owner.setId(1L);

        Booking booking = createExistingBooking(owner, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> bookingService.cancelBooking(booking.getId(), 2L));

        assertEquals("You cannot cancel this booking", exception.getMessage());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void cancelBookingShouldThrowWhenBookingAlreadyCancelled() {

        User user = new User();
        user.setId(1L);

        Booking booking = createExistingBooking(user, BookingStatus.CANCELLED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> bookingService.cancelBooking(booking.getId(), user.getId()));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Booking is already cancelled", exception.getReason());
    }

    @Test
    void cancelBookingShouldCancelBookingSuccessfully() {

        User user = new User();
        user.setId(1L);

        Booking booking = createExistingBooking(user, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponse result = bookingService.cancelBooking(booking.getId(), user.getId());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        assertEquals(booking.getId(), result.getId());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void getMyBookingsShouldReturnEmptyListWhenNoBookingsExist() {

        Long userId = 1L;

        when(bookingRepository.findByUser_Id(userId)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getMyBookings(userId);

        assertTrue(result.isEmpty());

        verify(bookingRepository).findByUser_Id(userId);
    }


    @Test
    void getMyBookingsShouldReturnExistingBookings() {

        User user = new User();
        user.setId(1L);

        Booking firstBooking = createExistingBooking(user, BookingStatus.CONFIRMED);

        Booking secondBooking = createExistingBooking(user, BookingStatus.CANCELLED);

        secondBooking.setId(11L);

        when(bookingRepository.findByUser_Id(user.getId())).thenReturn(List.of(firstBooking, secondBooking));

        List<BookingResponse> result = bookingService.getMyBookings(user.getId());

        assertEquals(2, result.size());

        assertEquals(firstBooking.getId(), result.get(0).getId());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(BookingStatus.CONFIRMED, result.get(0).getStatus());

        assertEquals(secondBooking.getId(), result.get(1).getId());
        assertEquals(user.getId(), result.get(1).getUserId());
        assertEquals(BookingStatus.CANCELLED, result.get(1).getStatus());

        verify(bookingRepository).findByUser_Id(user.getId());
    }

}