package com.staymanager.backend.service;

import com.staymanager.backend.dto.AccommodationCreateRequest;
import com.staymanager.backend.dto.AccommodationResponse;
import com.staymanager.backend.entity.Accommodation;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.repository.AccommodationRepository;
import com.staymanager.backend.repository.BookingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Handles accommodation management, retrieval and ownership validation. */
@Service
@RequiredArgsConstructor
public class AccommodationService {
  private final AccommodationRepository accommodationRepository;
  private final BookingRepository bookingRepository;

  public List<Accommodation> getAllAccommodations() {
    return accommodationRepository.findAll();
  }

  /**
   * Retrieves an accommodation by its identifier.
   *
   * @param id the accommodation identifier
   * @return the requested accommodation entity
   * @throws ResponseStatusException if the accommodation is not found
   */
  public Accommodation getAccommodationById(Long id) {
    return accommodationRepository
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation not found"));
  }

  /**
   * Retrieves accommodations matching the specified city.
   *
   * @param city the city name
   * @return the list of matching accommodation entities
   */
  public List<Accommodation> getAccommodationsByCity(String city) {
    return accommodationRepository.findByCityIgnoreCase(city);
  }

  /**
   * Creates and saves a new accommodation belonging to the specified owner.
   *
   * @param request the accommodation details
   * @param owner the accommodation owner
   * @return the created accommodation response
   */
  public AccommodationResponse createAccommodation(AccommodationCreateRequest request, User owner) {
    Accommodation accommodation = new Accommodation();
    accommodation.setAddress(request.getAddress());
    accommodation.setCity(request.getCity());
    accommodation.setName(request.getName());
    accommodation.setDescription(request.getDescription());
    accommodation.setPricePerNight(request.getPricePerNight());
    accommodation.setCapacity(request.getCapacity());
    accommodation.setOwner(owner);
    return toResponse(accommodationRepository.save(accommodation));
  }

  /**
   * Updates an accommodation after verifying ownership.
   *
   * @param id the accommodation identifier
   * @param request the updated accommodation details
   * @param user the user requesting the update
   * @return the updated accommodation response
   * @throws ResponseStatusException if the accommodation is not found
   * @throws AccessDeniedException if the user does not own the accommodation
   */
  public AccommodationResponse updateAccommodation(
      Long id, AccommodationCreateRequest request, User user) {

    Accommodation accommodation = getAccommodationById(id);

    if (!accommodation.getOwner().getId().equals(user.getId())) {
      throw new AccessDeniedException("You are not the owner of this accommodation");
    }

    accommodation.setCity(request.getCity());
    accommodation.setName(request.getName());
    accommodation.setAddress(request.getAddress());
    accommodation.setDescription(request.getDescription());
    accommodation.setPricePerNight(request.getPricePerNight());
    accommodation.setCapacity(request.getCapacity());

    return toResponse(accommodationRepository.save(accommodation));
  }

  /**
   * Deletes an accommodation if it has no associated bookings.
   *
   * @param id the accommodation identifier
   * @throws ResponseStatusException if the accommodation is not found or has bookings
   */
  public void deleteAccommodation(Long id) {
    Accommodation accommodation = getAccommodationById(id);
    if (bookingRepository.existsByAccommodationId(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Accommodation cannot be deleted because it has bookings");
    }
    accommodationRepository.delete(accommodation);
  }

  private AccommodationResponse toResponse(Accommodation accommodation) {
    AccommodationResponse response = new AccommodationResponse();
    response.setId(accommodation.getId());
    response.setName(accommodation.getName());
    response.setDescription(accommodation.getDescription());
    response.setCity(accommodation.getCity());
    response.setCapacity(accommodation.getCapacity());
    response.setPricePerNight(accommodation.getPricePerNight());
    response.setAddress(accommodation.getAddress());
    response.setOwnerId(accommodation.getOwner().getId());
    return response;
  }

  /**
   * Retrieves an accommodation and converts it into a response DTO.
   *
   * @param id the accommodation identifier
   * @return the requested accommodation response
   * @throws ResponseStatusException if the accommodation is not found
   */
  public AccommodationResponse getAccommodationResponseById(Long id) {
    return toResponse(getAccommodationById(id));
  }

  public List<AccommodationResponse> getAllAccommodationResponses() {
    return getAllAccommodations().stream().map(accommodation -> toResponse(accommodation)).toList();
  }

  /**
   * Retrieves accommodation response DTOs matching the specified city.
   *
   * @param city the city name
   * @return the list of matching accommodation responses
   */
  public List<AccommodationResponse> getAccommodationResponsesByCity(String city) {
    return getAccommodationsByCity(city).stream()
        .map(accommodation -> toResponse(accommodation))
        .toList();
  }
}
