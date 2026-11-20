package com.staymanager.backend.controller;

import com.staymanager.backend.dto.AccommodationCreateRequest;
import com.staymanager.backend.dto.AccommodationResponse;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.service.AccommodationService;
import com.staymanager.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<AccommodationResponse>> getAllAccommodations(@RequestParam(required = false) String city){
        if(city == null || city.isBlank()) {
            List<AccommodationResponse> allAccommodations = accommodationService.getAllAccommodationResponses();
            return ResponseEntity.ok(allAccommodations);
        }

        List<AccommodationResponse> allAccommodations = accommodationService.getAccommodationResponsesByCity(city.trim());
        return ResponseEntity.ok(allAccommodations);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> getAccommodationById(@PathVariable Long id){
        AccommodationResponse accommodationResponse = accommodationService.getAccommodationResponseById(id);
        return ResponseEntity.ok(accommodationResponse);
    }
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<AccommodationResponse> createAccommodation(@Valid @RequestBody AccommodationCreateRequest request,@AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        Long userId = Long.parseLong(subject);
        User user = userService.findUserById(userId);
        AccommodationResponse accommodationResponse = accommodationService.createAccommodation(request,user);
        return ResponseEntity.status(HttpStatus.CREATED).body(accommodationResponse);
    }
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {

        accommodationService.deleteAccommodation(id);

        return ResponseEntity.noContent().build();
    }
}
