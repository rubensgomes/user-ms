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
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 *
 * <p>This DTO represents the response returned after successful user authentication. It contains
 * the JWT token and basic user information needed by the client to maintain the authenticated
 * session.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body for successful authentication")
public class LoginResponse {

  /** JWT authentication token. */
  @Schema(
      description = "JWT authentication token",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String token;

  /** Token type (always "Bearer" for JWT tokens). */
  @Schema(description = "Token type", example = "Bearer")
  private String type = "Bearer";

  /** Token expiration time in seconds. */
  @Schema(description = "Token expiration time in seconds", example = "86400")
  private Long expiresIn;

  /** Basic user information. */
  @Schema(description = "Authenticated user information")
  private UserInfo user;

  /** Nested class representing basic user information included in the login response. */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Basic user information")
  public static class UserInfo {

    /** The user's unique identifier. */
    @Schema(
        description = "User's unique identifier",
        example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    /** The user's email address. */
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;
  }

  /**
   * Constructs a login response with token and user information.
   *
   * @param token the JWT authentication token
   * @param expiresIn token expiration time in seconds
   * @param userId the user's unique identifier
   * @param email the user's email address
   */
  public LoginResponse(String token, Long expiresIn, UUID userId, String email) {
    this.token = token;
    this.type = "Bearer";
    this.expiresIn = expiresIn;
    this.user = new UserInfo(userId, email);
  }
}
