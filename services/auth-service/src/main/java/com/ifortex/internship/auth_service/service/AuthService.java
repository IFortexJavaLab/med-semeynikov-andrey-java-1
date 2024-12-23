package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.dto.request.LoginRequest;
import com.ifortex.internship.auth_service.dto.response.AuthResponse;
import com.ifortex.internship.auth_service.exception.custom.UserNotAuthenticatedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Service interface for handling user login and authentication.
 *
 * <p>Provides methods to authenticate users, generate authentication tokens, and prepare cookies
 * for secure storage of access and refresh tokens.
 */
public interface AuthService {

  /**
   * Authenticates a user based on their login credentials.
   *
   * <p>This method performs authentication using the provided {@link LoginRequest}, generates an
   * access token and refresh token for the authenticated user, and returns a {@link AuthResponse}
   * containing the tokens and user information.
   *
   * @param loginRequest the LoginRequest containing the user's email and password
   * @return a AuthResponse containing the generated tokens and user details
   * @throws BadCredentialsException if the provided credentials are invalid
   */
  AuthResponse authenticateUser(LoginRequest loginRequest);

  /**
   * Logs out the currently authenticated user by invalidating all their refresh tokens and clearing
   * authentication cookies.
   *
   * <p>This method retrieves the currently authenticated user's details from the {@link
   * SecurityContextHolder}. If the user is authenticated, their refresh tokens are deleted from the
   * database, and cookies for access and refresh tokens are cleared. If the user is not
   * authenticated (anonymous), a {@link UserNotAuthenticatedException} is thrown.
   *
   * @return a AuthResponse containing the cleared access and refresh token cookies and the user ID
   *     of the logged-out user.
   * @throws UserNotAuthenticatedException if the user is not authenticated.
   */
  AuthResponse logoutUser();
}
