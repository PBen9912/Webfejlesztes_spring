package com.staymanager.backend.controller;

import com.staymanager.backend.dto.BookingCreateRequest;
import com.staymanager.backend.dto.BookingResponse;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.service.BookingService;
import com.staymanager.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request, @AuthenticationPrincipal Jwt jwt) {

        User user = null;

        if (jwt != null) {
            Long userId = Long.valueOf(jwt.getSubject());
            user = userService.findUserById(userId);
        }

        BookingResponse response = bookingService.createBooking(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(bookingService.getMyBookings(userId));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(
                bookingService.cancelBooking(id, userId)
        );
    }
}