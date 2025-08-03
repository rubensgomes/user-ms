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
package com.rubensgomes.userms.controller;

import com.rubensgomes.userms.dto.*;
import com.rubensgomes.userms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * <p>This controller handles user authentication, password reset, and related authentication
 * operations. All endpoints in this controller are publicly accessible (no authentication
 * required).
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and password management")
public class AuthController {

  private final AuthService authService;

  /**
   * Authenticates a user and returns a JWT token.
   *
   * @param request the login request containing email and password
   * @return JWT token and user information
   */
  @PostMapping("/login")
  @Operation(
      summary = "User authentication",
      description = "Authenticate user and receive JWT token")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login request received for email: {}", request.getEmail());
    LoginResponse response = authService.authenticateUser(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Initiates password reset process by sending reset email.
   *
   * @param request the email address for password reset
   * @return success message
   */
  @PostMapping("/forgot-password")
  @Operation(summary = "Request password reset", description = "Send password reset link to email")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Reset email sent (if email exists)")
      })
  public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailRequest request) {
    log.info("Password reset requested for email: {}", request.getEmail());
    String message = authService.initiatePasswordReset(request.getEmail());
    return ResponseEntity.ok(message);
  }

  /**
   * Resets user password using reset token.
   *
   * @param request the password reset request with token and new password
   * @return success message
   */
  @PostMapping("/reset-password")
  @Operation(summary = "Reset password", description = "Reset password using token from email")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
    log.info("Password reset attempt with token: {}", request.getToken());
    String message = authService.resetPassword(request);
    return ResponseEntity.ok(message);
  }

  /** Simple DTO for email requests. Used for forgot password functionality. */
  public static class EmailRequest {
    /** The user's email address. */
    @jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank
    private String email;

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
      return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
      this.email = email;
    }
  }
}
