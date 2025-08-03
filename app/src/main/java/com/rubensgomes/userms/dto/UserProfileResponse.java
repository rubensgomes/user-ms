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
 * Data Transfer Object for user profile responses.
 *
 * <p>This DTO represents a user's profile information returned by the API. It excludes sensitive
 * information like passwords and tokens, providing only safe user data for client consumption.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class UserProfileResponse {

  /** The user's unique identifier. */
  @Schema(
      description = "User's unique identifier",
      example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  /** The user's email address. */
  @Schema(description = "User's email address", example = "john.doe@example.com")
  private String email;

  /** Timestamp when the user account was created. */
  @Schema(description = "Account creation timestamp", example = "2025-01-15T10:30:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime createdAt;

  /** Timestamp when the password was last changed. */
  @Schema(description = "Last password change timestamp", example = "2025-01-15T11:45:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime passwordChangedAt;

  /** Whether the user's email has been confirmed. */
  @Schema(description = "Email confirmation status", example = "true")
  private Boolean confirmed;
}
