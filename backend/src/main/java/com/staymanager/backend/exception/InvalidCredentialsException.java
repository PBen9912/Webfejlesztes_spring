package com.staymanager.backend.exception;

/** Indicates that the provided login credentials are invalid. */
public class InvalidCredentialsException extends RuntimeException {

  /**
   * Creates an exception for invalid login credentials.
   *
   * @param message the error message
   */
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
