package com.ifortex.internship.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
  // TODO generate secret
  public static final String SECRET =
      "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

  public void validateToken(final String token) {
    SecretKey key = getSignKey();
    Jwts.parser().verifyWith(key);
  }

  public String generateToken(String userName) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userName);
  }

  private String createToken(Map<String, Object> claims, String userName) {
    return Jwts.builder()
        .claims(claims)
        .subject(userName)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
        .signWith(getSignKey())
        .compact();
  }

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes());
  }
}
