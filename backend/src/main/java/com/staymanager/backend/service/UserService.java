package com.staymanager.backend.service;

import com.staymanager.backend.dto.LoginRequest;
import com.staymanager.backend.dto.RegisterRequest;
import com.staymanager.backend.dto.UserResponse;
import com.staymanager.backend.entity.Role;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.exception.EmailAlreadyRegisteredException;
import com.staymanager.backend.exception.InvalidCredentialsException;
import com.staymanager.backend.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Handles user registration, authentication and retrieval. */
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user with a normalized email address, hashed password and assigned role.
   *
   * @param request the user registration details
   * @return the registered user response
   * @throws EmailAlreadyRegisteredException if the email address is already registered
   * @throws ResponseStatusException if the password exceeds the BCrypt limit of 72 bytes
   */
  public UserResponse register(RegisterRequest request) {
    User user = new User();
    String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyRegisteredException("Email already registered");
    }
    user.setEmail(email);
    if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password exceeds 72 bytes");
    }
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    user.setPassword(hashedPassword);
    user.setName(request.getName());
    if (request.isHostRegistration()) {
      user.setRole(Role.HOST);
    } else {
      user.setRole(Role.USER);
    }
    return toResponse(userRepository.save(user));
  }

  private UserResponse toResponse(User user) {
    UserResponse response = new UserResponse();
    response.setEmail(user.getEmail());
    response.setName(user.getName());
    response.setRole(user.getRole());
    response.setId(user.getId());
    return response;
  }

  /**
   * Authenticates a user by validating the email address and password.
   *
   * @param request the user's login credentials
   * @return the authenticated user entity
   * @throws InvalidCredentialsException if the email address or password is invalid
   */
  public User login(LoginRequest request) {
    String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    return user;
  }

  /**
   * Retrieves a registered user by identifier.
   *
   * @param id the user identifier
   * @return the requested user entity
   * @throws ResponseStatusException if the user is not found
   */
  public User findUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }
}
