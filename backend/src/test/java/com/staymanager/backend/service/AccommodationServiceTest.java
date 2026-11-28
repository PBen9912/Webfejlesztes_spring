package com.staymanager.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.staymanager.backend.dto.AccommodationCreateRequest;
import com.staymanager.backend.dto.AccommodationResponse;
import com.staymanager.backend.entity.Accommodation;
import com.staymanager.backend.entity.Role;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.repository.AccommodationRepository;
import com.staymanager.backend.repository.BookingRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

  @Mock private AccommodationRepository accommodationRepository;

  @Mock private BookingRepository bookingRepository;

  @InjectMocks private AccommodationService accommodationService;

  private User owner;
  private Accommodation accommodation;

  @BeforeEach
  void setUp() {
    owner = new User();
    owner.setId(1L);
    owner.setRole(Role.HOST);

    accommodation = new Accommodation();
    accommodation.setId(10L);
    accommodation.setName("Test Hotel");
    accommodation.setDescription("Test description");
    accommodation.setCity("Debrecen");
    accommodation.setAddress("Test Street 1");
    accommodation.setCapacity(4);
    accommodation.setPricePerNight(new BigDecimal("25000.00"));
    accommodation.setOwner(owner);
  }

  @Test
  void getAllAccommodationsReturnsAllAccommodations() {
    when(accommodationRepository.findAll()).thenReturn(List.of(accommodation));

    List<Accommodation> result = accommodationService.getAllAccommodations();

    assertEquals(List.of(accommodation), result);
    verify(accommodationRepository).findAll();
  }

  @Test
  void getAccommodationByIdReturnsAccommodationWhenFound() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));

    Accommodation result = accommodationService.getAccommodationById(10L);

    assertSame(accommodation, result);
    verify(accommodationRepository).findById(10L);
  }

  @Test
  void getAccommodationByIdThrowsExceptionWhenNotFound() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> accommodationService.getAccommodationById(10L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Accommodation not found", exception.getReason());
  }

  @Test
  void getAccommodationsByCityReturnsMatchingAccommodations() {
    when(accommodationRepository.findByCityIgnoreCase("debrecen"))
        .thenReturn(List.of(accommodation));

    List<Accommodation> result = accommodationService.getAccommodationsByCity("debrecen");

    assertEquals(List.of(accommodation), result);
    verify(accommodationRepository).findByCityIgnoreCase("debrecen");
  }

  @Test
  void createAccommodationSavesAndReturnsAccommodation() {
    final AccommodationCreateRequest request = createRequest();

    when(accommodationRepository.save(any(Accommodation.class)))
        .thenAnswer(invocation -> {
          Accommodation saved = invocation.getArgument(0);
          saved.setId(20L);
          return saved;
        });

    final AccommodationResponse result = accommodationService.createAccommodation(request, owner);

    ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
    verify(accommodationRepository).save(captor.capture());

    final Accommodation saved = captor.getValue();

    assertSame(owner, saved.getOwner());
    assertEquals(20L, saved.getId());
    assertRequestMapped(request, saved);
    assertResponseMapped(saved, result);
  }

  @Test
  void updateAccommodationUpdatesAccommodationWhenUserIsOwner() {
    final AccommodationCreateRequest request = createRequest();

    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
    when(accommodationRepository.save(accommodation)).thenReturn(accommodation);

    final AccommodationResponse result =
        accommodationService.updateAccommodation(10L, request, owner);

    assertEquals(10L, accommodation.getId());
    assertSame(owner, accommodation.getOwner());
    assertRequestMapped(request, accommodation);
    assertResponseMapped(accommodation, result);

    verify(accommodationRepository).save(accommodation);
  }

  @Test
  void updateAccommodationThrowsExceptionWhenNotFound() {
    final AccommodationCreateRequest request = createRequest();

    when(accommodationRepository.findById(10L)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> accommodationService.updateAccommodation(10L, request, owner));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Accommodation not found", exception.getReason());

    verify(accommodationRepository, never()).save(any(Accommodation.class));
  }

  @Test
  void updateAccommodationThrowsExceptionWhenUserIsNotOwner() {
    final AccommodationCreateRequest request = createRequest();

    final User otherUser = new User();
    otherUser.setId(2L);
    otherUser.setRole(Role.HOST);

    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));

    AccessDeniedException exception =
        assertThrows(
            AccessDeniedException.class,
            () -> accommodationService.updateAccommodation(10L, request, otherUser));

    assertEquals("You are not the owner of this accommodation", exception.getMessage());
    assertEquals("Test Hotel", accommodation.getName());

    verify(accommodationRepository, never()).save(any(Accommodation.class));
  }

  @Test
  void deleteAccommodationDeletesAccommodationWithoutBookings() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
    when(bookingRepository.existsByAccommodationId(10L)).thenReturn(false);

    accommodationService.deleteAccommodation(10L);

    verify(bookingRepository).existsByAccommodationId(10L);
    verify(accommodationRepository).delete(accommodation);
  }

  @Test
  void deleteAccommodationThrowsExceptionWhenNotFound() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> accommodationService.deleteAccommodation(10L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Accommodation not found", exception.getReason());

    verifyNoInteractions(bookingRepository);
    verify(accommodationRepository, never()).delete(any(Accommodation.class));
  }

  @Test
  void deleteAccommodationThrowsExceptionWhenBookingsExist() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
    when(bookingRepository.existsByAccommodationId(10L)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> accommodationService.deleteAccommodation(10L));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Accommodation cannot be deleted because it has bookings", exception.getReason());

    verify(accommodationRepository, never()).delete(any(Accommodation.class));
  }

  @Test
  void getAccommodationResponseByIdReturnsMappedResponse() {
    when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));

    AccommodationResponse result = accommodationService.getAccommodationResponseById(10L);

    assertResponseMapped(accommodation, result);
  }

  @Test
  void getAllAccommodationResponsesReturnsMappedResponses() {
    when(accommodationRepository.findAll()).thenReturn(List.of(accommodation));

    List<AccommodationResponse> result = accommodationService.getAllAccommodationResponses();

    assertEquals(1, result.size());
    assertResponseMapped(accommodation, result.getFirst());
  }

  @Test
  void getAccommodationResponsesByCityReturnsMatchingResponses() {
    when(accommodationRepository.findByCityIgnoreCase("Debrecen"))
        .thenReturn(List.of(accommodation));

    List<AccommodationResponse> result =
        accommodationService.getAccommodationResponsesByCity("Debrecen");

    assertEquals(1, result.size());
    assertResponseMapped(accommodation, result.getFirst());

    verify(accommodationRepository).findByCityIgnoreCase("Debrecen");
  }

  private AccommodationCreateRequest createRequest() {
    AccommodationCreateRequest request = new AccommodationCreateRequest();
    request.setName("New Hotel");
    request.setDescription("New description");
    request.setCity("Nyiregyhaza");
    request.setAddress("New Street 2");
    request.setCapacity(6);
    request.setPricePerNight(new BigDecimal("35000.00"));
    return request;
  }

  private void assertRequestMapped(AccommodationCreateRequest request, Accommodation entity) {
    assertEquals(request.getName(), entity.getName());
    assertEquals(request.getDescription(), entity.getDescription());
    assertEquals(request.getCity(), entity.getCity());
    assertEquals(request.getAddress(), entity.getAddress());
    assertEquals(request.getCapacity(), entity.getCapacity());
    assertEquals(request.getPricePerNight(), entity.getPricePerNight());
  }

  private void assertResponseMapped(Accommodation entity, AccommodationResponse response) {
    assertEquals(entity.getId(), response.getId());
    assertEquals(entity.getName(), response.getName());
    assertEquals(entity.getDescription(), response.getDescription());
    assertEquals(entity.getCity(), response.getCity());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getCapacity(), response.getCapacity());
    assertEquals(entity.getPricePerNight(), response.getPricePerNight());
    assertEquals(entity.getOwner().getId(), response.getOwnerId());
  }
}
