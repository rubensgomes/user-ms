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

import java.util.List;
import java.util.Objects;

/**
 * Represents an email sending request with all necessary information. This record encapsulates
 * sender information, recipient details, subject, content, and optional CC/BCC lists. At least one
 * of textContent or htmlContent must be provided.
 *
 * @param from the sender's email address (optional, uses default if null)
 * @param fromName the sender's display name (optional, uses default if null)
 * @param to the recipient's email address (required)
 * @param toName the recipient's display name (optional)
 * @param subject the email subject line (required)
 * @param textContent the plain text content (optional if htmlContent provided)
 * @param htmlContent the HTML content (optional if textContent provided)
 * @param cc list of email addresses to carbon copy (optional)
 * @param bcc list of email addresses to blind carbon copy (optional)
 */
public record EmailRequest(
    String from,
    String fromName,
    String to,
    String toName,
    String subject,
    String textContent,
    String htmlContent,
    List<String> cc,
    List<String> bcc) {
  /** Compact constructor for validating email request parameters. */
  public EmailRequest {
    Objects.requireNonNull(to, "Recipient email cannot be null");
    Objects.requireNonNull(subject, "Subject cannot be null");
    if (textContent == null && htmlContent == null) {
      throw new IllegalArgumentException("Either text or HTML content must be provided");
    }
  }
}
