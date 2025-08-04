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
import com.rubensgomes.userms.security.UserPrincipal;
import com.rubensgomes.userms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management endpoints.
 *
 * <p>This controller handles user registration, confirmation, profile management, and password
 * changes. Some endpoints require authentication while others are publicly accessible.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User account management operations")
public class UserController {

  private final UserService userService;

  /**
   * Registers a new user account.
   *
   * @param request the user registration request
   * @return user registration response with account details
   */
  @PostMapping("/register")
  @Operation(summary = "Register new user", description = "Create a new user account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserRegistrationResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<UserRegistrationResponse> registerUser(
      @Valid @RequestBody UserRegistrationRequest request) {
    log.info("User registration request received for email: {}", request.getEmail());
    UserRegistrationResponse response = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Confirms user email address using confirmation token.
   *
   * @param token the confirmation token from email
   * @return success message
   */
  @GetMapping("/confirm")
  @Operation(summary = "Confirm email address", description = "Confirm user email using token")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid confirmation token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<String> confirmUser(@RequestParam String token) {
    log.info("Email confirmation request with token: {}", token);
    String message = userService.confirmUser(token);
    return ResponseEntity.ok(message);
  }

  /**
   * Retrieves authenticated user's profile.
   *
   * @param userPrincipal the authenticated user
   * @return user profile information
   */
  @GetMapping("/profile")
  @Operation(summary = "Get user profile", description = "Retrieve authenticated user's profile")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<UserProfileResponse> getUserProfile(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("Profile request for user: {}", userPrincipal.getEmail());
    UserProfileResponse profile = userService.getUserProfile(userPrincipal.getEmail());
    return ResponseEntity.ok(profile);
  }

  /**
   * Changes authenticated user's password.
   *
   * @param request the password change request
   * @param userPrincipal the authenticated user
   * @return success message
   */
  @PutMapping("/change-password")
  @Operation(summary = "Change password", description = "Change authenticated user's password")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid current password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<String> changePassword(
      @Valid @RequestBody PasswordChangeRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("Password change request for user: {}", userPrincipal.getEmail());
    String message = userService.changePassword(userPrincipal.getEmail(), request);
    return ResponseEntity.ok(message);
  }
}
