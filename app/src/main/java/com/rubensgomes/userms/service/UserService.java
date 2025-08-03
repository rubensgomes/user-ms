/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubensgomes.userms.service;

import com.rubensgomes.userms.dto.*;
import com.rubensgomes.userms.model.User;
import com.rubensgomes.userms.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for user management operations.
 *
 * <p>This service handles core user management functionality including user registration, email
 * confirmation, profile management, and password changes. It coordinates with the email service for
 * sending notifications and ensures data integrity through transactional operations.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  /**
   * Registers a new user account.
   *
   * <p>This method creates a new user account with email confirmation workflow. It validates email
   * uniqueness, encrypts the password, generates a confirmation token, and sends a confirmation
   * email asynchronously.
   *
   * @param request the user registration request containing email and password
   * @return user registration response with account details
   * @throws RuntimeException if email already exists
   */
  public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
    log.info("Attempting to register user with email: {}", request.getEmail());

    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Registration failed: Email already exists: {}", request.getEmail());
      throw new RuntimeException("Email already exists");
    }

    // Create new user
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setConfirmed(false);
    user.setConfirmationToken(UUID.randomUUID().toString());

    // Save user
    User savedUser = userRepository.save(user);
    log.info("User registered successfully with ID: {}", savedUser.getId());

    // Send confirmation email asynchronously
    emailService.sendConfirmationEmail(savedUser.getEmail(), savedUser.getConfirmationToken());

    return new UserRegistrationResponse(
        savedUser.getId(),
        savedUser.getEmail(),
        savedUser.getCreatedAt(),
        savedUser.getPasswordChangedAt());
  }

  /**
   * Confirms a user's email address using a confirmation token.
   *
   * <p>This method validates the confirmation token and activates the user account. Once confirmed,
   * the user can authenticate and access protected endpoints.
   *
   * @param token the confirmation token from the email
   * @return success message
   * @throws RuntimeException if token is invalid or user already confirmed
   */
  public String confirmUser(String token) {
    log.info("Attempting to confirm user with token: {}", token);

    User user =
        userRepository
            .findByConfirmationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid confirmation token"));

    if (user.getConfirmed()) {
      log.warn("User already confirmed: {}", user.getEmail());
      throw new RuntimeException("User account already confirmed");
    }

    user.confirmAccount();
    userRepository.save(user);

    log.info("User confirmed successfully: {}", user.getEmail());
    return "Your account has been confirmed successfully. You can now login.";
  }

  /**
   * Retrieves a user's profile information.
   *
   * <p>This method returns safe user profile data excluding sensitive information like passwords
   * and tokens.
   *
   * @param email the user's email address
   * @return user profile response
   * @throws RuntimeException if user not found
   */
  @Transactional(readOnly = true)
  public UserProfileResponse getUserProfile(String email) {
    log.info("Retrieving profile for user: {}", email);

    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    return new UserProfileResponse(
        user.getId(),
        user.getEmail(),
        user.getCreatedAt(),
        user.getPasswordChangedAt(),
        user.getConfirmed());
  }

  /**
   * Changes a user's password after verifying their current password.
   *
   * <p>This method validates the current password, encrypts the new password, and updates the
   * password change timestamp.
   *
   * @param email the user's email address
   * @param request the password change request
   * @return success message
   * @throws RuntimeException if current password is incorrect or user not found
   */
  public String changePassword(String email, PasswordChangeRequest request) {
    log.info("Attempting to change password for user: {}", email);

    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    // Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      log.warn("Password change failed: Incorrect current password for user: {}", email);
      throw new RuntimeException("Current password is incorrect");
    }

    // Check if new password is different from current
    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      log.warn("Password change failed: New password same as current for user: {}", email);
      throw new RuntimeException("New password must be different from current password");
    }

    // Update password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setPasswordChangedAt(LocalDateTime.now());
    userRepository.save(user);

    log.info("Password changed successfully for user: {}", email);
    return "Password changed successfully";
  }

  /**
   * Finds a user by email address.
   *
   * <p>This method is used internally by other services for user lookup operations.
   *
   * @param email the user's email address
   * @return the user entity
   * @throws RuntimeException if user not found
   */
  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  /**
   * Checks if a user exists with the given email.
   *
   * @param email the email address to check
   * @return true if user exists, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
