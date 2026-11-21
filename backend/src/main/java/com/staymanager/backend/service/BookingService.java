package com.staymanager.backend.service;

import com.staymanager.backend.dto.BookingCreateRequest;
import com.staymanager.backend.dto.BookingResponse;
import com.staymanager.backend.entity.Accommodation;
import com.staymanager.backend.entity.Booking;
import com.staymanager.backend.entity.BookingStatus;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AccommodationService accommodationService;

    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request, User user) {

        Accommodation accommodation = accommodationService.getAccommodationById(request.getAccommodationId());

        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        if (checkIn == null || checkOut == null || checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid booking dates");
        }

        if (request.getGuestCount() == null || request.getGuestCount() < 1 || request.getGuestCount() > accommodation.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid guest count");
        }

        boolean overlapping = bookingRepository.existsOverlappingBooking(accommodation.getId(), checkIn, checkOut, BookingStatus.CONFIRMED);

        if (overlapping) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A szállás a kiválasztott időszakban már foglalt.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        BigDecimal totalPrice = accommodation.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setGuestName(request.getGuestName().trim());
        booking.setGuestEmail(request.getGuestEmail().trim());
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuestCount(request.getGuestCount());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        return toResponse(savedBooking);
    }

    private BookingResponse toResponse(Booking booking) {

        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setAccommodationId(booking.getAccommodation().getId());
        response.setUserId(booking.getUser() != null ? booking.getUser().getId() : null);
        response.setGuestName(booking.getGuestName());
        response.setGuestEmail(booking.getGuestEmail());
        response.setCheckIn(booking.getCheckIn());
        response.setCheckOut(booking.getCheckOut());
        response.setGuestCount(booking.getGuestCount());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());

        return response;
    }
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUser_Id(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getUser() == null || !booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You cannot cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return toResponse(booking);
    }
}