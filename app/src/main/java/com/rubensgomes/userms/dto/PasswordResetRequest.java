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
 * Data Transfer Object for password reset requests using a reset token.
 *
 * <p>This DTO is used when users reset their password using a token received via email. It contains
 * the reset token and the new password.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for password reset with token")
public class PasswordResetRequest {

  /** The password reset token received via email. */
  @Schema(
      description = "Password reset token from email",
      example = "reset-token-uuid-here",
      required = true)
  @NotBlank(message = "Reset token is required")
  @Size(max = 255, message = "Reset token must not exceed 255 characters")
  private String token;

  /** The new password to set for the user account. */
  @Schema(
      description =
          "New password (minimum 8 characters, must contain uppercase, lowercase, number, and special character)",
      example = "NewSecurePassword789!",
      required = true)
  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
      message =
          "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)")
  private String newPassword;
}
