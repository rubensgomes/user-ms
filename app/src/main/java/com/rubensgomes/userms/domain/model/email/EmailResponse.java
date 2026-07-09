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
package com.rubensgomes.userms.domain.model.email;

import java.time.LocalDateTime;

/**
 * Represents the response from an email sending operation. This record encapsulates the result of
 * sending an email, including success status, message identifiers, and timing information.
 *
 * @param success indicates whether the email was sent successfully
 * @param messageId the unique message identifier from the email provider
 * @param statusCode the HTTP status code returned by the email provider
 * @param message a descriptive message about the operation result
 * @param sentAt the timestamp when the email was processed
 */
public record EmailResponse(
    boolean success, String messageId, int statusCode, String message, LocalDateTime sentAt) {
  /**
   * Creates a successful email response.
   *
   * @param msgId the unique message identifier from the email provider
   * @param status the HTTP status code from the email provider
   * @return EmailResponse instance representing a successful email send
   */
  public static EmailResponse success(final String msgId, final int status) {
    return new EmailResponse(true, msgId, status, "Email sent successfully", LocalDateTime.now());
  }

  /**
   * Creates a failed email response.
   *
   * @param status the HTTP status code from the email provider
   * @param msg the error message describing the failure reason
   * @return EmailResponse instance representing a failed email send
   */
  public static EmailResponse failure(final int status, final String msg) {
    return new EmailResponse(false, null, status, msg, LocalDateTime.now());
  }
}
