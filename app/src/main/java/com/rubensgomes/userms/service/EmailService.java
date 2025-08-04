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
package com.rubensgomes.userms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications asynchronously.
 *
 * <p>This service handles all email communications for the user microservice, including account
 * confirmation emails and password reset emails. All emails are sent asynchronously to avoid
 * blocking API responses.
 *
 * <p>Email templates are HTML-formatted and include security warnings to protect users from
 * phishing attempts.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender javaMailSender;

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Value("${spring.mail.username}")
  private String fromEmail;

  /**
   * Sends an account confirmation email asynchronously.
   *
   * <p>This method sends an HTML email to the user with a confirmation link that includes their
   * confirmation token. The email includes security warnings about not clicking the link if they
   * didn't register.
   *
   * @param email the recipient's email address
   * @param confirmationToken the unique confirmation token
   */
  @Async
  public void sendConfirmationEmail(String email, String confirmationToken) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(email);
      helper.setSubject("Confirm Your Account - User Microservice");

      String confirmationUrl = baseUrl + "/api/user/confirm?token=" + confirmationToken;
      String htmlContent = buildConfirmationEmailContent(confirmationUrl);

      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("Confirmation email sent successfully to: {}", email);

    } catch (MessagingException | MailException e) {
      log.error("Failed to send confirmation email to: {}", email, e);
      throw new MailSendException("Failed to send confirmation email", e);
    }
  }

  /**
   * Sends a password reset email asynchronously.
   *
   * <p>This method sends an HTML email to the user with a password reset link that includes their
   * reset token. The email includes information about token expiration and security warnings.
   *
   * @param email the recipient's email address
   * @param resetToken the unique password reset token
   */
  @Async
  public void sendPasswordResetEmail(String email, String resetToken) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(email);
      helper.setSubject("Password Reset Request - User Microservice");

      String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
      String htmlContent = buildPasswordResetEmailContent(resetUrl);

      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("Password reset email sent successfully to: {}", email);

    } catch (MessagingException | MailException e) {
      log.error("Failed to send password reset email to: {}", email, e);
      throw new MailSendException("Failed to send password reset email", e);
    }
  }

  /**
   * Builds the HTML content for account confirmation emails.
   *
   * @param confirmationUrl the URL for account confirmation
   * @return HTML email content
   */
  private String buildConfirmationEmailContent(String confirmationUrl) {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Confirm Your Account</title>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                .content { padding: 30px; background-color: #f8f9fa; }
                .button {
                    display: inline-block;
                    padding: 12px 30px;
                    background-color: #28a745;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .warning {
                    background-color: #fff3cd;
                    border: 1px solid #ffeaa7;
                    padding: 15px;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Welcome to User Microservice</h1>
                </div>
                <div class="content">
                    <h2>Confirm Your Account</h2>
                    <p>Thank you for registering with our service. To complete your registration and activate your account, please click the button below:</p>

                    <div style="text-align: center;">
                        <a href="%s" class="button">Confirm My Account</a>
                    </div>

                    <p>Or copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; background-color: #e9ecef; padding: 10px; border-radius: 3px;">%s</p>

                    <div class="warning">
                        <strong>Security Notice:</strong> If you did not create an account with us, please ignore this email and do not click the confirmation link. Your email address will not be added to our system.
                    </div>

                    <p>This confirmation link will expire in 24 hours for security purposes.</p>
                </div>
                <div class="footer">
                    <p>This is an automated message from User Microservice. Please do not reply to this email.</p>
                </div>
            </div>
        </body>
        </html>
        """
        .formatted(confirmationUrl, confirmationUrl);
  }

  /**
   * Builds the HTML content for password reset emails.
   *
   * @param resetUrl the URL for password reset
   * @return HTML email content
   */
  private String buildPasswordResetEmailContent(String resetUrl) {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Password Reset Request</title>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                .content { padding: 30px; background-color: #f8f9fa; }
                .button {
                    display: inline-block;
                    padding: 12px 30px;
                    background-color: #dc3545;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .warning {
                    background-color: #f8d7da;
                    border: 1px solid #f5c6cb;
                    padding: 15px;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Password Reset Request</h1>
                </div>
                <div class="content">
                    <h2>Reset Your Password</h2>
                    <p>We received a request to reset the password for your account. If you made this request, click the button below to set a new password:</p>

                    <div style="text-align: center;">
                        <a href="%s" class="button">Reset My Password</a>
                    </div>

                    <p>Or copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; background-color: #e9ecef; padding: 10px; border-radius: 3px;">%s</p>

                    <div class="warning">
                        <strong>Security Notice:</strong> If you did not request a password reset, please ignore this email and your password will remain unchanged. Consider changing your password if you suspect unauthorized access to your account.
                    </div>

                    <p><strong>Important:</strong> This password reset link will expire in 24 hours for security purposes.</p>
                </div>
                <div class="footer">
                    <p>This is an automated message from User Microservice. Please do not reply to this email.</p>
                </div>
            </div>
        </body>
        </html>
        """
        .formatted(resetUrl, resetUrl);
  }
}
