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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for standardized error responses.
 *
 * <p>This DTO provides a consistent format for error responses across all API endpoints. It
 * includes error classification, descriptive messages, and timestamps to help clients handle errors
 * appropriately and aid in debugging.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standardized error response format")
public class ErrorResponse {

  /**
   * Error classification code.
   *
   * <p>Common error codes include: - VALIDATION_ERROR: Input validation failures -
   * AUTHENTICATION_FAILED: Invalid credentials - UNAUTHORIZED: Missing or invalid authentication -
   * FORBIDDEN: Insufficient permissions - NOT_FOUND: Resource not found - CONFLICT: Resource
   * already exists - INTERNAL_SERVER_ERROR: Unexpected server errors
   */
  @Schema(description = "Error classification code", example = "VALIDATION_ERROR")
  private String error;

  /** Human-readable error message. */
  @Schema(description = "Human-readable error description", example = "Email already exists")
  private String message;

  /** Timestamp when the error occurred. */
  @Schema(description = "Error timestamp", example = "2025-01-15T10:30:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime timestamp;

  /**
   * Creates an error response with current timestamp.
   *
   * @param error the error classification code
   * @param message the human-readable error message
   */
  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}
