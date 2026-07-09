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

import java.io.IOException;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.rubensgomes.userms.config.EmailConfig;
import com.rubensgomes.userms.domain.model.email.EmailRequest;
import com.rubensgomes.userms.domain.model.email.EmailResponse;
import com.rubensgomes.userms.exception.EmailException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

/**
 * SendGrid implementation of EmailService. Provides email sending capabilities using SendGrid API.
 */
@Slf4j
@Service
@Primary
public final class SendGridEmailServiceImpl implements EmailService {

  /** HTTP status code indicating successful response lower bound. */
  private static final int HTTP_SUCCESS_MIN = 200;

  /** HTTP status code indicating successful response upper bound. */
  private static final int HTTP_SUCCESS_MAX = 300;

  /** HTTP status code for internal server error. */
  private static final int HTTP_INTERNAL_ERROR = 500;

  /** SendGrid client instance used to send emails via SendGrid API. */
  private final SendGrid sendGridClient;

  /** Configuration properties for email service (API key, sender info, etc.). */
  private final EmailConfig emailConfig;

  /**
   * Constructs a SendGridEmailServiceImpl with the provided config. Initializes the SendGrid client
   * with the API key from configuration.
   *
   * @param config the email configuration containing API key and sender info
   */
  public SendGridEmailServiceImpl(final EmailConfig config) {
    this.emailConfig = config;
    this.sendGridClient = new SendGrid(config.getSendgridApiKey());
    log.debug("SendGridEmailServiceImpl initialized");
  }

  @Override
  public EmailResponse sendSimpleEmail(final EmailRequest request) throws EmailException {
    log.info(
        "sendSimpleEmail called with to: {}, subject: {}, from: {}",
        request.to(),
        request.subject(),
        request.from());

    if (!emailConfig.isEnabled()) {
      log.warn("Email service is disabled. Email not sent.");
      return EmailResponse.failure(0, "Email service is disabled");
    }

    Email from = createFromEmail(request);
    Email to = createToEmail(request);
    Content content = new Content("text/plain", request.textContent());
    Mail mail = new Mail(from, request.subject(), to, content);

    return sendMail(mail);
  }

  @Override
  public EmailResponse sendHtmlEmail(final EmailRequest request) throws EmailException {
    log.info(
        "sendHtmlEmail called with to: {}, subject: {}, from: {}",
        request.to(),
        request.subject(),
        request.from());

    if (!emailConfig.isEnabled()) {
      log.warn("Email service is disabled. Email not sent.");
      return EmailResponse.failure(0, "Email service is disabled");
    }

    Email from = createFromEmail(request);
    Email to = createToEmail(request);
    Content content = new Content("text/html", request.htmlContent());
    Mail mail = new Mail(from, request.subject(), to, content);

    return sendMail(mail);
  }

  @Override
  public EmailResponse sendTemplateEmail(
      final String templateName,
      final Map<String, Object> variables,
      final String to,
      final String subject)
      throws EmailException {
    log.info("sendTemplateEmail called with templateName: {}, to: {}", templateName, to);
    throw new EmailException("Template email not yet implemented");
  }

  /**
   * Creates the 'from' Email object from request. Falls back to configuration defaults if not
   * specified.
   *
   * @param request the email request containing optional sender info
   * @return Email object representing the sender
   */
  private Email createFromEmail(final EmailRequest request) {
    String fromAddr = request.from() != null ? request.from() : emailConfig.getFromEmail();
    String fromName = request.fromName() != null ? request.fromName() : emailConfig.getFromName();
    return new Email(fromAddr, fromName);
  }

  /**
   * Creates the 'to' Email object from request.
   *
   * @param request the email request containing recipient information
   * @return Email object representing the recipient
   */
  private Email createToEmail(final EmailRequest request) {
    return new Email(request.to(), request.toName());
  }

  /**
   * Sends mail using SendGrid API. Constructs a SendGrid API request and processes the response.
   *
   * @param mail the Mail object with sender, recipient, subject, content
   * @return EmailResponse indicating success with message ID, or failure
   * @throws EmailException if SendGrid API returns an error or IOException
   */
  private EmailResponse sendMail(final Mail mail) throws EmailException {
    log.debug("sendMail called with mail: {}", mail);
    try {
      Request request = new Request();
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());

      log.debug("Sending email via SendGrid API");
      Response response = sendGridClient.api(request);

      int statusCode = response.getStatusCode();
      String messageId = response.getHeaders().get("X-Message-Id");
      if (messageId == null) {
        messageId = "unknown-" + System.currentTimeMillis();
        log.warn("X-Message-Id header not present, using: {}", messageId);
      }

      if (statusCode >= HTTP_SUCCESS_MIN && statusCode < HTTP_SUCCESS_MAX) {
        log.info("Email sent. Status: {}, MessageId: {}", statusCode, messageId);
        return EmailResponse.success(messageId, statusCode);
      } else {
        String errorMsg = "SendGrid API error: " + statusCode + " - " + response.getBody();
        log.error(errorMsg);
        throw new EmailException(errorMsg, null, statusCode, "SendGrid");
      }

    } catch (IOException e) {
      log.error("Failed to send email via SendGrid", e);
      throw new EmailException(
          "Failed to send email: " + e.getMessage(), e, HTTP_INTERNAL_ERROR, "SendGrid");
    }
  }
}
