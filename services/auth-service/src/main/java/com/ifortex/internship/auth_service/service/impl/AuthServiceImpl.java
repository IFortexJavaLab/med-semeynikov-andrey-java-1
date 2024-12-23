package com.ifortex.internship.auth_service.service.impl;

import com.ifortex.internship.auth_service.dto.request.LoginRequest;
import com.ifortex.internship.auth_service.dto.response.CookieTokensResponse;
import com.ifortex.internship.auth_service.dto.response.AuthResponse;
import com.ifortex.internship.auth_service.exception.custom.UserNotAuthenticatedException;
import com.ifortex.internship.auth_service.model.RefreshToken;
import com.ifortex.internship.auth_service.model.Role;
import com.ifortex.internship.auth_service.model.UserDetailsImpl;
import com.ifortex.internship.auth_service.service.CookieService;
import com.ifortex.internship.auth_service.service.AuthService;
import com.ifortex.internship.auth_service.service.RefreshTokenService;
import com.ifortex.internship.auth_service.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final CookieService cookieService;
  private final RefreshTokenService refreshTokenService;

  public AuthServiceImpl(
      TokenService tokenService,
      AuthenticationManager authenticationManager,
      CookieService cookieService,
      RefreshTokenService refreshTokenService) {
    this.tokenService = tokenService;
    this.authenticationManager = authenticationManager;
    this.cookieService = cookieService;
    this.refreshTokenService = refreshTokenService;
  }

  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    log.debug("Authenticating user with email: {}", loginRequest.getEmail());

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    // get List of roles to generateAccessToken
    // authorities in the UserDetailsImpl stores like "ROLE_NAME_OF_ROLE"
    // use substring to get only NAME_OF_ROLE that corresponds with Role enum
    List<Role> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.substring(5))
            .map(Role::valueOf)
            .toList();

    log.debug("User: {} successfully authenticated.", userDetails.getUsername());

    // feature add email verification

    String newAccessToken =
        tokenService.generateAccessToken(userDetails.getUsername(), roles);
    log.debug("Access token generated successfully for user: {}", userDetails.getEmail());

    RefreshToken newRefreshToken = tokenService.createRefreshToken(userDetails.getId());
    log.debug("Refresh token generated successfully for user: {}", userDetails.getEmail());

    ResponseCookie accessTokenCookie = cookieService.createAccessTokenCookie(newAccessToken);
    ResponseCookie refreshTokenCookie =
        cookieService.createRefreshTokenCookie(newRefreshToken.getToken());
    log.debug(
        "Cookies with access and refresh tokens generated successfully for user: {}",
        userDetails.getEmail());

    return new AuthResponse(
        new CookieTokensResponse(accessTokenCookie, refreshTokenCookie), userDetails.getId());
  }

  public AuthResponse logoutUser() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = null;
    if (!principle.toString().equals("anonymousUser")) {
      userId = ((UserDetailsImpl) principle).getId();
      refreshTokenService.deleteTokensByUserId(userId);
      log.debug("Deleted all refresh tokens for user ID: {}", userId);
    } else {
      log.warn("Logout attempt by anonymous or unauthenticated user.");
      throw new UserNotAuthenticatedException("User is not authenticated. Please log in.");
    }

    ResponseCookie accessTokenCookie = cookieService.deleteAccessTokenCookie();
    ResponseCookie refreshTokenCookie = cookieService.deleteRefreshTokenCookie();

    return new AuthResponse(new CookieTokensResponse(accessTokenCookie, refreshTokenCookie), userId);
  }
}
