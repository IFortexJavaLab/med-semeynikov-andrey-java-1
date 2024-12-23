package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.dto.response.CookieTokensResponse;
import com.ifortex.internship.auth_service.exception.custom.TokensRefreshException;
import com.ifortex.internship.auth_service.model.RefreshToken;
import com.ifortex.internship.auth_service.model.Role;
import com.ifortex.internship.auth_service.model.User;
import java.util.List;

/**
 * Service interface for managing JWT and refresh tokens.
 *
 * <p>Provides functionality for generating, validating, and refreshing access tokens, as well as
 * managing refresh tokens and their associated cookies.
 */
public interface TokenService {

  /**
   * Generates a JWT access token for a {@link User} based on their email and roles.
   *
   * @param email the email of the User
   * @param roles the list of {@link Role} assigned to the User
   * @return the generated JWT access token as a String
   */
  String generateAccessToken(String email, List<Role> roles);

  /**
   * Refreshes the access and refresh tokens for a User.
   *
   * <p>Validates the provided refresh token and generates new access and refresh tokens. Also
   * creates HTTP cookies for storing the new tokens.
   *
   * @param refreshToken the refresh token to validate and refresh
   * @return a {@link CookieTokensResponse} containing the new tokens as cookies
   * @throws TokensRefreshException if the refresh token is invalid or the associated user is not
   *     found
   */
  CookieTokensResponse refreshTokens(String refreshToken);

  /**
   * Validates the provided JWT access token.
   *
   * @param authToken the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  boolean isValid(String authToken);

  /**
   * Creates a new refresh token for the specified user ID.
   *
   * @param userId the ID of the user for whom the refresh token is created
   * @return the created {@link RefreshToken}
   */
  RefreshToken createRefreshToken(Long userId);

  /**
   * Extracts the username from the provided JWT access token.
   *
   * @param token the JWT token
   * @return the username (email) extracted from the token
   */
  String getUsernameFromToken(String token);
}
