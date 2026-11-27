package com.staymanager.backend.controller;

import com.staymanager.backend.dto.AccommodationCreateRequest;
import com.staymanager.backend.dto.AccommodationResponse;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.service.AccommodationService;
import com.staymanager.backend.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Provides REST endpoints for searching and managing accommodations. */
@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
  private final AccommodationService accommodationService;
  private final UserService userService;

  /**
   * Returns all accommodations or filters them by city.
   *
   * @param city the optional city filter
   * @return the list of matching accommodations
   */
  @GetMapping
  public ResponseEntity<List<AccommodationResponse>> getAllAccommodations(
      @RequestParam(required = false) String city) {
    if (city == null || city.isBlank()) {
      List<AccommodationResponse> allAccommodations =
          accommodationService.getAllAccommodationResponses();
      return ResponseEntity.ok(allAccommodations);
    }

    List<AccommodationResponse> allAccommodations =
        accommodationService.getAccommodationResponsesByCity(city.trim());
    return ResponseEntity.ok(allAccommodations);
  }

  /**
   * Retrieves an accommodation by its identifier.
   *
   * @param id the accommodation identifier
   * @return the requested accommodation
   */
  @GetMapping("/{id}")
  public ResponseEntity<AccommodationResponse> getAccommodationById(@PathVariable Long id) {
    AccommodationResponse accommodationResponse =
        accommodationService.getAccommodationResponseById(id);
    return ResponseEntity.ok(accommodationResponse);
  }

  /**
   * Creates a new accommodation for the authenticated host.
   *
   * @param request the accommodation details
   * @param jwt the authenticated host's JWT
   * @return the created accommodation with HTTP status 201
   */
  @PostMapping
  @PreAuthorize("hasRole('HOST')")
  public ResponseEntity<AccommodationResponse> createAccommodation(
      @Valid @RequestBody AccommodationCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
    String subject = jwt.getSubject();
    Long userId = Long.parseLong(subject);
    User user = userService.findUserById(userId);
    AccommodationResponse accommodationResponse =
        accommodationService.createAccommodation(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(accommodationResponse);
  }

  /**
   * Updates an existing accommodation as an authenticated host.
   *
   * @param id the accommodation identifier
   * @param request the updated accommodation details
   * @param jwt the authenticated host's JWT
   * @return the updated accommodation
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('HOST')")
  public ResponseEntity<AccommodationResponse> updateAccommodation(
      @PathVariable Long id,
      @Valid @RequestBody AccommodationCreateRequest request,
      @AuthenticationPrincipal Jwt jwt) {

    Long userId = Long.parseLong(jwt.getSubject());

    User user = userService.findUserById(userId);

    AccommodationResponse accommodationResponse =
        accommodationService.updateAccommodation(id, request, user);

    return ResponseEntity.ok(accommodationResponse);
  }

  /**
   * Deletes an accommodation as an administrator.
   *
   * @param id the accommodation identifier
   * @return an empty response with HTTP status 204
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {

    accommodationService.deleteAccommodation(id);

    return ResponseEntity.noContent().build();
  }
}
