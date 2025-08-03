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
package com.rubensgomes.userms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration responses.
 *
 * <p>This DTO represents the response returned after successful user registration. It contains the
 * created user's information excluding sensitive data like passwords and tokens. The response
 * includes a message advising the user to check their email for confirmation.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body for successful user registration")
public class UserRegistrationResponse {

  /** The unique identifier assigned to the new user account. */
  @Schema(description = "Unique user identifier", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  /** The user's email address. */
  @Schema(description = "User's email address", example = "john.doe@example.com")
  private String email;

  /** Timestamp when the user account was created. */
  @Schema(description = "Account creation timestamp", example = "2025-01-15T10:30:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime createdAt;

  /** Timestamp when the password was last changed (same as creation for new users). */
  @Schema(description = "Last password change timestamp", example = "2025-01-15T10:30:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime passwordChangedAt;

  /** Message informing the user about the next steps. */
  @Schema(
      description = "Instructions for the user",
      example = "Registration successful. Please check your email to confirm your account.")
  private String message;

  /**
   * Constructs a registration response from user data.
   *
   * @param id the user's unique identifier
   * @param email the user's email address
   * @param createdAt timestamp when the account was created
   * @param passwordChangedAt timestamp when the password was set
   */
  public UserRegistrationResponse(
      UUID id, String email, LocalDateTime createdAt, LocalDateTime passwordChangedAt) {
    this.id = id;
    this.email = email;
    this.createdAt = createdAt;
    this.passwordChangedAt = passwordChangedAt;
    this.message = "Registration successful. Please check your email to confirm your account.";
  }
}
