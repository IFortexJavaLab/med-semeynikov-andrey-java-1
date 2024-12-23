package com.ifortex.internship.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CookieTokensResponse {
  private ResponseCookie accessCookie;
  private ResponseCookie refreshCookie;
}
