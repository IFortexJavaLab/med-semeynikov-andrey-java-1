package com.ifortex.internship.auth_service.controller;

import com.ifortex.internship.auth_service.dto.LoginRequest;
import com.ifortex.internship.auth_service.dto.LoginResponse;
import com.ifortex.internship.auth_service.dto.RegistrationRequest;
import com.ifortex.internship.auth_service.dto.RegistrationResponse;
import com.ifortex.internship.auth_service.dto.TokenRefreshRequest;
import com.ifortex.internship.auth_service.dto.TokenRefreshResponse;
import com.ifortex.internship.auth_service.service.JwtTokenProvider;
import com.ifortex.internship.auth_service.service.LoginService;
import com.ifortex.internship.auth_service.service.RefreshTokenService;
import com.ifortex.internship.auth_service.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final RegistrationService registrationService;
  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(
      RegistrationService service,
      LoginService loginService,
      RefreshTokenService refreshTokenService,
      JwtTokenProvider jwtTokenProvider) {
    this.registrationService = service;
    this.loginService = loginService;
    this.refreshTokenService = refreshTokenService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // fixme handle error valid
  @PostMapping("/register")
  public ResponseEntity<RegistrationResponse> addNewUser(
      @RequestBody @Valid RegistrationRequest request) {
    RegistrationResponse response = registrationService.register(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
    LoginResponse response = loginService.authenticateUser(loginRequest);

    // fixme add secure for https or other profile
    ResponseCookie refreshTokenCookie =
        ResponseCookie.from("refreshToken", response.getRefreshToken())
            .httpOnly(true)
            .path("/auth/refresh-token") // Устанавливаем путь для токена
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
        .body(
            new LoginResponse(
                response.getAccessToken(), response.getUserId(), response.getRoles()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenRefreshResponse> refreshAccessToken(
      @RequestBody TokenRefreshRequest request) {

    TokenRefreshResponse response =
        refreshTokenService.refreshAccessToken(request.getRefreshToken());

    return ResponseEntity.ok(response);
  }

  // fixme Move to the api gateway
  @GetMapping("/validate")
  public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
    boolean isValid = jwtTokenProvider.validateJwtToken(token);
    if (isValid) {
      String username = jwtTokenProvider.getUserNameFromJwtToken(token);
      return ResponseEntity.ok("Token is valid for user: " + username);
    } else {
      return ResponseEntity.status(401).body("Invalid or expired token.");
    }
  }
}
