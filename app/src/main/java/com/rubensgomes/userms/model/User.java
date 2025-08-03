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
package com.rubensgomes.userms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * User entity representing a user account in the system.
 *
 * <p>This entity stores user account information including authentication credentials, confirmation
 * status, and password reset tokens. The entity uses UUID as the primary key and includes audit
 * timestamps for tracking account creation and password changes.
 *
 * <p>Key features: - Email-based authentication with unique email constraint - Password encryption
 * (handled at service layer) - Email confirmation workflow with confirmation tokens - Password
 * reset functionality with time-limited reset tokens - Audit timestamps for creation and password
 * changes
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /** Unique identifier for the user account. Generated automatically using UUID strategy. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  /**
   * User's email address, used as the username for authentication. Must be unique across all users
   * and is used for login and communication.
   */
  @Column(name = "email", unique = true, nullable = false, length = 100)
  @Email(message = "Email must be valid")
  @NotBlank(message = "Email is required")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  /** Encrypted password for user authentication. Stored as a bcrypt hash, never in plain text. */
  @Column(name = "password", nullable = false, length = 255)
  @NotBlank(message = "Password is required")
  @Size(max = 255, message = "Encrypted password must not exceed 255 characters")
  private String password;

  /**
   * Timestamp when the user account was created. Automatically set when the entity is first
   * persisted.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  /**
   * Timestamp when the user's password was last changed. Updated whenever the password is modified.
   */
  @Column(name = "password_changed_at")
  @UpdateTimestamp
  private LocalDateTime passwordChangedAt;

  /**
   * Token used for email confirmation during user registration. Generated as a secure UUID and
   * cleared after successful confirmation.
   */
  @Column(name = "confirmation_token", length = 255)
  private String confirmationToken;

  /**
   * Flag indicating whether the user's email has been confirmed. Set to true when the user clicks
   * the confirmation link in their email.
   */
  @Column(name = "confirmed", nullable = false)
  private Boolean confirmed = false;

  /**
   * Token used for password reset functionality. Generated when user requests password reset and
   * cleared after use.
   */
  @Column(name = "reset_token", length = 255)
  private String resetToken;

  /**
   * Expiration timestamp for the password reset token. Reset tokens are time-limited for security
   * purposes.
   */
  @Column(name = "reset_token_expiry")
  private LocalDateTime resetTokenExpiry;

  /**
   * Constructs a new User with email and password. Sets default values for confirmed status.
   *
   * @param email the user's email address
   * @param password the user's encrypted password
   */
  public User(String email, String password) {
    this.email = email;
    this.password = password;
    this.confirmed = false;
  }

  /**
   * Checks if the password reset token is valid and not expired.
   *
   * @return true if reset token exists and has not expired, false otherwise
   */
  public boolean isResetTokenValid() {
    return resetToken != null
        && resetTokenExpiry != null
        && resetTokenExpiry.isAfter(LocalDateTime.now());
  }

  /**
   * Clears the password reset token and expiry. Called after successful password reset or token
   * expiration.
   */
  public void clearResetToken() {
    this.resetToken = null;
    this.resetTokenExpiry = null;
  }

  /** Marks the user account as confirmed. Called when email confirmation is successful. */
  public void confirmAccount() {
    this.confirmed = true;
    this.confirmationToken = null;
  }
}
