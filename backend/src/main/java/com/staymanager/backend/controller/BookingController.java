package com.staymanager.backend.controller;

import com.staymanager.backend.dto.BookingCreateRequest;
import com.staymanager.backend.dto.BookingResponse;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.service.BookingService;
import com.staymanager.backend.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Provides REST endpoints for creating, retrieving and cancelling bookings. */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;
  private final UserService userService;

  /**
   * Creates a booking for an authenticated user or an anonymous guest.
   *
   * @param request the booking details
   * @param jwt the authenticated user's JWT, or null for a guest
   * @return the created booking with HTTP status 201
   */
  @PostMapping
  public ResponseEntity<BookingResponse> createBooking(
      @Valid @RequestBody BookingCreateRequest request, @AuthenticationPrincipal Jwt jwt) {

    User user = null;

    if (jwt != null) {
      Long userId = Long.valueOf(jwt.getSubject());
      user = userService.findUserById(userId);
    }

    BookingResponse response = bookingService.createBooking(request, user);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Retrieves all bookings belonging to the authenticated user.
   *
   * @param jwt the authenticated user's JWT
   * @return the user's bookings
   */
  @GetMapping("/my")
  public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {

    Long userId = Long.valueOf(jwt.getSubject());

    return ResponseEntity.ok(bookingService.getMyBookings(userId));
  }

  /**
   * Requests cancellation of a booking belonging to the authenticated user.
   *
   * @param id the booking identifier
   * @param jwt the authenticated user's JWT
   * @return the cancelled booking
   */
  @PatchMapping("/{id}/cancel")
  public ResponseEntity<BookingResponse> cancelBooking(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    Long userId = Long.valueOf(jwt.getSubject());
    return ResponseEntity.ok(bookingService.cancelBooking(id, userId));
  }
}
