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
package com.rubensgomes.userms.service.email;

import java.util.Map;

import com.rubensgomes.userms.domain.model.email.EmailRequest;
import com.rubensgomes.userms.domain.model.email.EmailResponse;
import com.rubensgomes.userms.exception.EmailException;

/**
 * Service interface for sending emails. Provides abstraction layer allowing easy switching between
 * email providers.
 */
public interface EmailService {

  /**
   * Sends a simple text email.
   *
   * @param request the email request with recipient, subject, and content
   * @return EmailResponse with send status and details
   * @throws EmailException if email sending fails
   */
  EmailResponse sendSimpleEmail(EmailRequest request) throws EmailException;

  /**
   * Sends an HTML email.
   *
   * @param request the email request with recipient, subject, and HTML
   * @return EmailResponse with send status and details
   * @throws EmailException if email sending fails
   */
  EmailResponse sendHtmlEmail(EmailRequest request) throws EmailException;

  /**
   * Sends an email using a template.
   *
   * @param templateName the name of the template file
   * @param variables the template variables
   * @param to the recipient email address
   * @param subject the email subject
   * @return EmailResponse with send status and details
   * @throws EmailException if email sending fails
   */
  EmailResponse sendTemplateEmail(
      String templateName, Map<String, Object> variables, String to, String subject)
      throws EmailException;
}
