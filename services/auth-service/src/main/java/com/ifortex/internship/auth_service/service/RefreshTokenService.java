package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.dto.TokenRefreshResponse;
import com.ifortex.internship.auth_service.entity.RefreshToken;
import com.ifortex.internship.auth_service.entity.User;
import com.ifortex.internship.auth_service.repository.RefreshTokenRepository;
import com.ifortex.internship.auth_service.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  @Value("${app.refreshTokenExpirationMs}")
  private int refreshTokenExpirationS;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      UserRepository userRepository,
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
  }

  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    deleteTokensByUserId(userId);

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpirationS));

    return refreshTokenRepository.save(refreshToken);
  }

  public TokenRefreshResponse refreshAccessToken(String refreshToken) {
    RefreshToken token =
        refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    verifyExpiration(token);

    User user = token.getUser();

    // fixme refactor error
    if (user == null) {
      throw new RuntimeException("User not found for this refresh token");
    }

    // fixme maybe refactor this method
    //  the same block in the login service line 40
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

    String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

    return new TokenRefreshResponse(newAccessToken);
  }

  public void verifyExpiration(RefreshToken refreshToken) {
    boolean tokenIsExpired = refreshToken.getExpiryDate().isBefore(Instant.now());
    if (tokenIsExpired) {
      refreshTokenRepository.delete(refreshToken);
      // TODO refactor error
      throw new RuntimeException("Refresh token is expired. Please login again.");
    }
  }

  @Transactional
  public void deleteTokensByUserId(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }
}
