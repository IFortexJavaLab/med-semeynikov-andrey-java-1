package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.config.UserDetailsImpl;
import com.ifortex.internship.auth_service.dto.LoginRequest;
import com.ifortex.internship.auth_service.dto.LoginResponse;
import com.ifortex.internship.auth_service.entity.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginService {
  
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final AuthenticationManager authenticationManager;

  public LoginService(
      JwtTokenProvider jwtTokenProvider,
      RefreshTokenService refreshTokenService,
      AuthenticationManager authenticationManager) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenService = refreshTokenService;
    this.authenticationManager = authenticationManager;
  }

  public LoginResponse authenticateUser(LoginRequest loginRequest) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    // TODO refactor mock method
    if (true) {
      // Mock: отправка кода 2FA
      String mockCode = "123456";
      System.out.println("2FA code sent to email: " + userDetails.getUsername());
      System.out.println("Code: " + mockCode);
    }

    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return new LoginResponse(
        accessToken, refreshToken.getToken(), userDetails.getId(), userDetails.getRoles());
  }
}
