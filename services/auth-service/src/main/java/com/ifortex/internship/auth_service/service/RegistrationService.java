package com.ifortex.internship.auth_service.service;

import com.ifortex.internship.auth_service.dto.request.RegistrationRequest;
import com.ifortex.internship.auth_service.dto.response.RegistrationResponse;
import com.ifortex.internship.auth_service.exception.custom.EmailAlreadyRegistered;
import com.ifortex.internship.auth_service.exception.custom.PasswordMismatchException;
import com.ifortex.internship.auth_service.model.User;

/**
 * Service interface for handling user registration.
 *
 * <p>Provides functionality for registering new users and validating registration data.
 */
public interface RegistrationService {

  /**
   * Registers a new user in the system.
   *
   * <p>This method performs the following steps:
   *
   * <ul>
   *   <li>Checks if the email is already registered.
   *   <li>Validates that the password matches its confirmation.
   *   <li>Encodes the password.
   *   <li>Creates and saves a new {@link User} entity in the database.
   * </ul>
   *
   * @param request the {@link RegistrationRequest} containing the user's email, password, and
   *     confirmation password
   * @return a {@link RegistrationResponse} containing the success message and user ID
   * @throws EmailAlreadyRegistered if the email is already registered
   * @throws PasswordMismatchException if the password does not match its confirmation
   */
  RegistrationResponse register(RegistrationRequest request);
}
