package com.staymanager.backend.controller;

import com.staymanager.backend.dto.LoginRequest;
import com.staymanager.backend.dto.LoginResponse;
import com.staymanager.backend.dto.RegisterRequest;
import com.staymanager.backend.dto.UserResponse;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.service.JwtService;
import com.staymanager.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Provides REST endpoints for user registration and authentication. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;
  private final JwtService jwtService;

  /**
   * Registers a new user account.
   *
   * @param request the user registration details
   * @return the registered user with HTTP status 201
   */
  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserResponse response = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Authenticates a user and generates a JWT.
   *
   * @param request the user's login credentials
   * @return the login response containing the generated JWT
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

    User user = userService.login(request);

    String token = jwtService.generateToken(user);

    LoginResponse loginResponse = new LoginResponse(token);

    return ResponseEntity.ok(loginResponse);
  }
}
