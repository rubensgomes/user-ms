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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 *
 * <p>This DTO encapsulates the data required for user registration, including validation
 * constraints to ensure data integrity and security. It is used as the request body for the user
 * registration API endpoint.
 *
 * <p>Validation rules enforce: - Valid email format and uniqueness (checked at service layer) -
 * Strong password requirements including length and complexity - Proper data sanitization to
 * prevent injection attacks
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for user registration")
public class UserRegistrationRequest {

  /**
   * The user's email address.
   *
   * <p>Must be a valid email format and will be used as the username for authentication. Email
   * uniqueness is validated at the service layer.
   */
  @Schema(
      description = "User's email address (used as username)",
      example = "john.doe@example.com",
      required = true)
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be a valid email address")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  /**
   * The user's password.
   *
   * <p>Must meet strong password requirements: - At least 8 characters long - Contains at least one
   * uppercase letter - Contains at least one lowercase letter - Contains at least one digit -
   * Contains at least one special character
   */
  @Schema(
      description =
          "User's password (minimum 8 characters, must contain uppercase, lowercase, number, and special character)",
      example = "SecurePassword123!",
      required = true)
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)")
  private String password;
}
