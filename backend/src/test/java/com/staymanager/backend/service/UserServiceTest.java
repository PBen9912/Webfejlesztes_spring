package com.staymanager.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.staymanager.backend.dto.LoginRequest;
import com.staymanager.backend.dto.RegisterRequest;
import com.staymanager.backend.dto.UserResponse;
import com.staymanager.backend.entity.Role;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.exception.EmailAlreadyRegisteredException;
import com.staymanager.backend.exception.InvalidCredentialsException;
import com.staymanager.backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void loginShouldThrowWhenUserNotFound() {

    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("testPassword");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
  }

  @Test
  void loginShouldThrowWhenPasswordIsInvalid() {

    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("wrongPassword");

    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
  }

  @Test
  void loginShouldReturnUserWhenCredentialsAreValid() {
    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("testPassword");

    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

    User result = userService.login(request);

    assertSame(user, result);
  }

  @Test
  void loginShouldNormalizeEmail() {

    LoginRequest request = new LoginRequest();
    request.setEmail("  TEST@Example.COM  ");
    request.setPassword("testPassword");

    User user = new User();
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

    User result = userService.login(request);

    assertSame(user, result);

    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void registerShouldThrowWhenEmailAlreadyExists() {

    RegisterRequest request = new RegisterRequest();
    request.setName("Test User");
    request.setEmail("test@example.com");
    request.setPassword("testPassword");

    User user = new User();
    user.setEmail("test@example.com");

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    assertThrows(EmailAlreadyRegisteredException.class, () -> userService.register(request));

    verifyNoInteractions(passwordEncoder);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void registerShouldThrowWhenPasswordExceeds72Bytes() {
    RegisterRequest request = new RegisterRequest();
    request.setName("Test User");
    request.setEmail("test@example.com");
    request.setPassword("á".repeat(37));

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> userService.register(request));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    verifyNoInteractions(passwordEncoder);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void registerShouldCreateHostUser() {

    RegisterRequest request = new RegisterRequest();
    request.setName("Test User");
    request.setEmail("  TEST@Example.COM  ");
    request.setPassword("testPassword");
    request.setHostRegistration(true);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    final UserResponse result = userService.register(request);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    User savedUser = captor.getValue();

    assertEquals("test@example.com", savedUser.getEmail());
    assertEquals(request.getName(), savedUser.getName());
    assertEquals("encodedPassword", savedUser.getPassword());
    assertEquals(Role.HOST, savedUser.getRole());
    assertEquals(Role.HOST, result.getRole());
    assertNotNull(result);
  }

  @Test
  void registerShouldCreateUser() {

    RegisterRequest request = new RegisterRequest();
    request.setName("Test User");
    request.setEmail("  TEST@Example.COM  ");
    request.setPassword("testPassword");
    request.setHostRegistration(false);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    final UserResponse result = userService.register(request);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    User savedUser = captor.getValue();

    assertEquals("test@example.com", savedUser.getEmail());
    assertEquals(request.getName(), savedUser.getName());
    assertEquals("encodedPassword", savedUser.getPassword());
    assertEquals(Role.USER, savedUser.getRole());
    assertEquals(Role.USER, result.getRole());
    assertNotNull(result);
  }

  @Test
  void findUserByIdShouldReturnUserWhenExists() {

    Long id = 1L;

    User user = new User();
    user.setId(id);

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    User foundUser = userService.findUserById(id);

    assertSame(user, foundUser);
  }

  @Test
  void findUserByIdShouldThrowWhenUserNotFound() {

    Long id = 1L;

    when(userRepository.findById(id)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> userService.findUserById(id));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
  }
}
