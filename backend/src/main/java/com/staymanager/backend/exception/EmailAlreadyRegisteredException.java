package com.staymanager.backend.exception;

/** Indicates that an email address is already registered. */
public class EmailAlreadyRegisteredException extends RuntimeException {

  /**
   * Creates an exception for an already registered email address.
   *
   * @param message the error message
   */
  public EmailAlreadyRegisteredException(String message) {
    super(message);
  }
}
