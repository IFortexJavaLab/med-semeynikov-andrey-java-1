package com.ifortex.internship.auth_service.exception.custom;

import com.ifortex.internship.auth_service.exception.AuthServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpiredException extends AuthServiceException {
  public RefreshTokenExpiredException(String message) {
    super(message);
  }
}
