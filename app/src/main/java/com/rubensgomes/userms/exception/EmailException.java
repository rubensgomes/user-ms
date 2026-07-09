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

/**
 * Exception thrown when an error occurs during email processing.
 *
 * <p>This exception provides additional context such as the HTTP status code and the email provider
 * involved.
 */
public class EmailException extends Exception {

  /** Default HTTP status code for unknown errors. */
  private static final int DEFAULT_STATUS_CODE = 500;

  /** The HTTP status code associated with the email error. */
  private final int statusCode;

  /** The name of the email provider where the error occurred. */
  private final String provider;

  /**
   * Constructs a new EmailException with the specified error message. Uses default values for
   * statusCode (500) and provider ("Unknown").
   *
   * @param message the detail message describing the error
   */
  public EmailException(final String message) {
    super(message);
    this.statusCode = DEFAULT_STATUS_CODE;
    this.provider = "Unknown";
  }

  /**
   * Constructs a new EmailException with detailed error information.
   *
   * @param message the detail message describing the error
   * @param cause the underlying cause of the error (can be null)
   * @param httpStatusCode the HTTP status code associated with the error
   * @param providerName the name of the email provider
   */
  public EmailException(
      final String message,
      final Throwable cause,
      final int httpStatusCode,
      final String providerName) {
    super(message, cause);
    this.statusCode = httpStatusCode;
    this.provider = providerName;
  }

  /**
   * Returns the HTTP status code associated with this email exception.
   *
   * @return the HTTP status code
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the provider name associated with this email exception.
   *
   * @return the provider name
   */
  public String getProvider() {
    return provider;
  }
}
