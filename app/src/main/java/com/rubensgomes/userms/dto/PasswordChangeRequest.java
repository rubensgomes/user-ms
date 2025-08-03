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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for password change requests by authenticated users.
 *
 * <p>This DTO is used when authenticated users want to change their current password. It requires
 * both the current password for verification and the new password that meets security requirements.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for password change by authenticated user")
public class PasswordChangeRequest {

  /** The user's current password for verification. */
  @Schema(
      description = "Current password for verification",
      example = "CurrentPassword123!",
      required = true)
  @NotBlank(message = "Current password is required")
  @Size(max = 128, message = "Current password must not exceed 128 characters")
  private String currentPassword;

  /** The new password that will replace the current password. */
  @Schema(
      description =
          "New password (minimum 8 characters, must contain uppercase, lowercase, number, and special character)",
      example = "NewSecurePassword456!",
      required = true)
  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
      message =
          "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)")
  private String newPassword;
}
