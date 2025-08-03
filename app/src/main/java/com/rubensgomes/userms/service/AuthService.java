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

import com.rubensgomes.userms.dto.LoginRequest;
import com.rubensgomes.userms.dto.LoginResponse;
import com.rubensgomes.userms.dto.PasswordResetRequest;
import com.rubensgomes.userms.model.User;
import com.rubensgomes.userms.repository.UserRepository;
import com.rubensgomes.userms.security.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication and authorization operations.
 *
 * <p>This service handles user authentication, JWT token generation, and password reset
 * functionality. It manages secure authentication workflows and coordinates with email service for
 * password reset notifications.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${app.jwt.expiration:86400}")
  private Long jwtExpirationInSeconds;

  /**
   * Authenticates a user and generates a JWT token.
   *
   * <p>This method validates user credentials and returns a JWT token for authenticated access to
   * protected endpoints. Only confirmed users can authenticate successfully.
   *
   * @param request the login request containing email and password
   * @return login response with JWT token and user info
   * @throws RuntimeException if authentication fails
   */
  public LoginResponse authenticateUser(LoginRequest request) {
    log.info("Attempting authentication for user: {}", request.getEmail());

    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

    // Check if user is confirmed
    if (!user.getConfirmed()) {
      log.warn("Authentication failed: User not confirmed: {}", request.getEmail());
      throw new RuntimeException("Please confirm your email address before logging in");
    }

    // Verify password
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      log.warn("Authentication failed: Invalid password for user: {}", request.getEmail());
      throw new RuntimeException("Invalid email or password");
    }

    // Generate JWT token
    String token = jwtTokenProvider.generateToken(user.getEmail());

    log.info("User authenticated successfully: {}", user.getEmail());
    return new LoginResponse(token, jwtExpirationInSeconds, user.getId(), user.getEmail());
  }

  /**
   * Initiates password reset process by sending reset email.
   *
   * <p>This method generates a time-limited reset token and sends it via email. For security, it
   * always returns success even if the email doesn't exist.
   *
   * @param email the user's email address
   * @return success message
   */
  public String initiatePasswordReset(String email) {
    log.info("Password reset requested for email: {}", email);

    userRepository
        .findByEmail(email)
        .ifPresent(
            user -> {
              // Generate reset token with 24-hour expiry
              String resetToken = UUID.randomUUID().toString();
              user.setResetToken(resetToken);
              user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

              userRepository.save(user);

              // Send reset email asynchronously
              emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
              log.info("Password reset email sent to: {}", email);
            });

    // Always return success for security (don't reveal if email exists)
    return "If an account with that email exists, you will receive password reset instructions.";
  }

  /**
   * Resets user password using a valid reset token.
   *
   * <p>This method validates the reset token and expiry, then updates the user's password and
   * clears the reset token.
   *
   * @param request the password reset request with token and new password
   * @return success message
   * @throws RuntimeException if token is invalid or expired
   */
  public String resetPassword(PasswordResetRequest request) {
    log.info("Attempting password reset with token: {}", request.getToken());

    User user =
        userRepository
            .findByResetToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

    // Check token expiry
    if (!user.isResetTokenValid()) {
      log.warn("Password reset failed: Token expired for user: {}", user.getEmail());
      user.clearResetToken();
      userRepository.save(user);
      throw new RuntimeException("Reset token has expired. Please request a new password reset.");
    }

    // Update password and clear reset token
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setPasswordChangedAt(LocalDateTime.now());
    user.clearResetToken();
    userRepository.save(user);

    log.info("Password reset successfully for user: {}", user.getEmail());
    return "Your password has been reset successfully. You can now login with your new password.";
  }

  /**
   * Validates a JWT token and returns the associated user.
   *
   * <p>This method is used by security filters to validate incoming JWT tokens and extract user
   * information for authorization.
   *
   * @param token the JWT token to validate
   * @return the user associated with the token
   * @throws RuntimeException if token is invalid
   */
  @Transactional(readOnly = true)
  public User validateTokenAndGetUser(String token) {
    if (!jwtTokenProvider.validateToken(token)) {
      throw new RuntimeException("Invalid JWT token");
    }

    String email = jwtTokenProvider.getEmailFromToken(token);
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found for token"));
  }

  /**
   * Checks if a user account is locked or disabled.
   *
   * @param email the user's email address
   * @return true if account is active, false if locked/disabled
   */
  @Transactional(readOnly = true)
  public boolean isAccountActive(String email) {
    return userRepository.findByEmail(email).map(User::getConfirmed).orElse(false);
  }
}
