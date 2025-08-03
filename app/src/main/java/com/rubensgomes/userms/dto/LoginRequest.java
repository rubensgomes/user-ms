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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user authentication requests.
 *
 * <p>This DTO encapsulates the credentials required for user authentication. It is used as the
 * request body for the login API endpoint and includes validation to ensure required fields are
 * provided.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for user authentication")
public class LoginRequest {

  /** The user's email address used as username. */
  @Schema(
      description = "User's email address (username)",
      example = "john.doe@example.com",
      required = true)
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be a valid email address")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  /** The user's password. */
  @Schema(description = "User's password", example = "SecurePassword123!", required = true)
  @NotBlank(message = "Password is required")
  @Size(max = 128, message = "Password must not exceed 128 characters")
  private String password;
}
