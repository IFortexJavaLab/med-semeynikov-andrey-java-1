package com.ifortex.internship.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ifortex.internship.auth_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private Long userId;
  private Set<Role> roles;

  public LoginResponse(String accessToken, Long userId, Set<Role> roles) {
    this.accessToken = accessToken;
    this.userId = userId;
    this.roles = roles;
    this.refreshToken = null;
  }
}
