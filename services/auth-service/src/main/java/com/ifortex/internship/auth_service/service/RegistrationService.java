package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.dto.RegistrationRequest;
import com.ifortex.internship.auth_service.dto.RegistrationResponse;
import com.ifortex.internship.auth_service.entity.Role;
import com.ifortex.internship.auth_service.entity.User;
import com.ifortex.internship.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class RegistrationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public RegistrationResponse register(RegistrationRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      // fixme refactor error
      throw new IllegalArgumentException("Email is already registered.");
    }

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(hashedPassword);
    user.setRoles(Set.of(Role.NON_SUBSCRIBED_USER));
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    return new RegistrationResponse("Registration successful.", user.getId());
  }
}
