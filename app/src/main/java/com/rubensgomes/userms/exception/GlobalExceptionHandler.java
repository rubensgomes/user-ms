/*
 * Copyright 2026 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.rubensgomes.userms.exception;

import java.net.URI;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the Orders API. Handles database constraint violations and custom
 * exceptions, translating them to appropriate HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Maximum length for extracted root cause messages. */
  private static final int MAX_CAUSE_LENGTH = 100;

  /**
   * Handles DataIntegrityViolationException thrown by JPA/Hibernate. Parses the exception message
   * to identify constraint violation type.
   *
   * @param ex the DataIntegrityViolationException
   * @return ProblemDetail with HTTP 400 status and error details
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolation(final DataIntegrityViolationException ex) {
    log.error("Data integrity violation occurred", ex);

    String message = ex.getMessage();
    String detail = "Order validation failed";
    String fieldName = null;

    if (message != null) {
      if (message.contains("NULL not allowed")) {
        fieldName = extractFieldName(message, "NULL not allowed for column \"", "\"");
        String field = fieldName != null ? fieldName.toLowerCase() : "unknown";
        detail = "Required field is missing: " + field;
      } else if (message.contains("UNIQUE")) {
        detail = "Duplicate order detected";
      } else if (message.contains("CHECK constraint")) {
        fieldName = extractFieldName(message, "constraint [", "]");
        String field = fieldName != null ? fieldName.toLowerCase() : "unknown";
        detail = "Invalid value for field: " + field;
      } else {
        detail = "Database constraint violation: " + extractRootCause(message);
      }
    }

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problemDetail.setTitle("Order Validation Error");
    problemDetail.setType(URI.create("https://api.example.com/errors/validation-error"));
    if (fieldName != null) {
      problemDetail.setProperty("field", fieldName.toLowerCase());
    }

    return problemDetail;
  }

  /**
   * Handles OrderValidationException.
   *
   * @param ex the OrderValidationException
   * @return ProblemDetail with HTTP 400 status and error details
   */
  @ExceptionHandler(OrderValidationException.class)
  public ProblemDetail handleOrderValidation(final OrderValidationException ex) {
    log.warn("Order validation failed: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Order Validation Error");
    problemDetail.setType(URI.create("https://api.example.com/errors/validation-error"));

    if (ex.getFieldName() != null) {
      problemDetail.setProperty("field", ex.getFieldName());
    }

    return problemDetail;
  }

  /**
   * Handles OrderCreationException.
   *
   * @param ex the OrderCreationException
   * @return ProblemDetail with HTTP 500 status and error details
   */
  @ExceptionHandler(OrderCreationException.class)
  public ProblemDetail handleOrderCreation(final OrderCreationException ex) {
    log.error("Order creation failed", ex);

    String errorDetail = "An error occurred while creating the order. " + "Please try again later.";
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, errorDetail);
    problemDetail.setTitle("Order Creation Error");
    problemDetail.setType(URI.create("https://api.example.com/errors/creation-error"));

    return problemDetail;
  }

  /**
   * Extracts field name from exception message using start and end markers.
   *
   * @param message the exception message
   * @param startMarker the marker before the field name
   * @param endMarker the marker after the field name
   * @return the extracted field name, or null if not found
   */
  private String extractFieldName(
      final String message, final String startMarker, final String endMarker) {
    try {
      int startIndex = message.indexOf(startMarker);
      if (startIndex != -1) {
        startIndex += startMarker.length();
        int endIndex = message.indexOf(endMarker, startIndex);
        if (endIndex != -1) {
          return message.substring(startIndex, endIndex);
        }
      }
    } catch (Exception e) {
      log.debug("Failed to extract field name from message: {}", message);
    }
    return null;
  }

  /**
   * Extracts the root cause message from exception message.
   *
   * @param message the full exception message
   * @return simplified error message
   */
  private String extractRootCause(final String message) {
    if (message == null) {
      return "Unknown error";
    }

    int newlineIndex = message.indexOf('\n');
    if (newlineIndex > 0 && newlineIndex < MAX_CAUSE_LENGTH) {
      return message.substring(0, newlineIndex).trim();
    }

    return message.length() > MAX_CAUSE_LENGTH
        ? message.substring(0, MAX_CAUSE_LENGTH) + "..."
        : message;
  }
}
