package com.ifortex.internship.auth_service.exception;

public class AuthServiceException extends RuntimeException {
  public AuthServiceException(String message) {
    super(message);
  }

  public AuthServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}