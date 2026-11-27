package com.staymanager.backend.exception;

import com.staymanager.backend.dto.ErrorResponse;
import java.sql.SQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Handles application exceptions and converts them into REST API error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles registration attempts with an already registered email address.
   *
   * @param ex the email registration exception
   * @return an error response with HTTP status 409
   */
  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyRegistered(
      EmailAlreadyRegisteredException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
  }

  /**
   * Handles invalid arguments provided to the application.
   *
   * @param ex the illegal argument exception
   * @return an error response with HTTP status 400
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  /**
   * Handles request validation failures and reports the first field error.
   *
   * @param ex the request validation exception
   * @return an error response with HTTP status 400
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("Validation failed");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
  }

  /**
   * Handles failed authentication attempts caused by invalid credentials.
   *
   * @param ex the invalid credentials exception
   * @return an error response with HTTP status 401
   */
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
      InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
  }

  /**
   * Converts a response status exception into a standardized error response.
   *
   * @param ex the response status exception
   * @return an error response with the exception's HTTP status
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    String message = ex.getReason() != null ? ex.getReason() : "Request failed";
    return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(message));
  }

  /**
   * Handles overlapping bookings rejected by the database exclusion constraint.
   *
   * @param ex the data integrity violation exception
   * @return an error response with HTTP status 409 for overlapping bookings
   * @throws DataIntegrityViolationException if the violation is unrelated to booking overlap
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    Throwable cause = ex;
    while (cause != null) {
      if (cause instanceof SQLException sqlException) {
        if ("23P01".equals(sqlException.getSQLState())
            && sqlException.getMessage() != null
            && sqlException.getMessage().contains("bookings_no_overlap")) {
          return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(new ErrorResponse("A szállás a kiválasztott időszakban már foglalt."));
        }
      }
      cause = cause.getCause();
    }

    throw ex;
  }
}
