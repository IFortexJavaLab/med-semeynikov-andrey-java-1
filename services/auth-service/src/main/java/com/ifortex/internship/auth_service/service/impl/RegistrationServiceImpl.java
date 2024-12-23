package com.ifortex.internship.auth_service.service.impl;

import com.ifortex.internship.auth_service.dto.request.RegistrationRequest;
import com.ifortex.internship.auth_service.dto.response.RegistrationResponse;
import com.ifortex.internship.auth_service.exception.custom.EmailAlreadyRegistered;
import com.ifortex.internship.auth_service.exception.custom.PasswordMismatchException;
import com.ifortex.internship.auth_service.model.Role;
import com.ifortex.internship.auth_service.model.User;
import com.ifortex.internship.auth_service.repository.UserRepository;
import com.ifortex.internship.auth_service.service.RegistrationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public RegistrationResponse register(RegistrationRequest request) {
    log.debug("Register user: {}", request.getEmail());
    if (userRepository.existsByEmail(request.getEmail())) {
      log.error("Email: {} is already registered.", request.getEmail());
      throw new EmailAlreadyRegistered("Email: " + request.getEmail() + " is already registered.");
    }

    boolean passwordMismatch = !request.getPassword().equals(request.getPasswordConfirmation());
    if (passwordMismatch) {
      log.error("Password and confirmation password do not match.");
      throw new PasswordMismatchException("Password and confirmation password do not match.");
    }

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(hashedPassword);
    user.setRoles(List.of(Role.NON_SUBSCRIBED_USER));
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
    log.debug("User: {} saved to db successfully", request.getEmail());

    log.info("User: {} register successfully", request.getEmail());
    return new RegistrationResponse("Registration successful.", user.getId());
  }
}
