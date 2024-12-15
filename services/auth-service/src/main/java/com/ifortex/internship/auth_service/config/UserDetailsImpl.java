package com.ifortex.internship.auth_service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ifortex.internship.auth_service.entity.Role;
import com.ifortex.internship.auth_service.entity.User;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

  private final long id;
  private final String email;
  @JsonIgnore private final String password;
  private final boolean isTwoFactorEnabled;
  private final Set<Role> roles;
  private final Set<GrantedAuthority> authorities;

  public UserDetailsImpl(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.isTwoFactorEnabled = user.isTwoFactorEnabled();
    this.roles = user.getRoles();
    this.authorities =
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
  }

  public Set<Role> getRoles() {
    return roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Long getId() {
    return id;
  }

  public boolean isTwoFactorEnabled() {
    return isTwoFactorEnabled;
  }
}
