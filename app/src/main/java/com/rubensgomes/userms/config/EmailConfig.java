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
package com.rubensgomes.userms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring configuration class for email service properties using SendGrid.
 *
 * <p>This class is annotated with {@code @ConfigurationProperties} to enable type-safe binding of
 * configuration properties from application.yaml or environment variables with the 'sendgrid'
 * prefix.
 *
 * <p>Properties can be configured in application.yaml as:
 *
 * <pre>
 * sendgrid:
 *   api-key: your-api-key
 *   from-email: noreply@example.com
 *   from-name: Example Application
 *   enabled: true
 * </pre>
 *
 * <p>The API key can also be set via the SENDGRID_API_KEY environment variable. Configuration is
 * validated on bean initialization.
 */
@Configuration
@ConfigurationProperties(prefix = "app.email")
@Data
@Slf4j
public class EmailConfig {

  /**
   * The API key used to authenticate with the SendGrid email service. Should be set via
   * SENDGRID_API_KEY environment variable or yaml.
   */
  private String sendgridApiKey;

  /** The email address that will appear as the sender of outgoing emails. */
  private String fromEmail;

  /** The name that will appear as the sender of outgoing emails. */
  private String fromName;

  /** The email address for company support to address questions or issues. */
  private String supportEmail;

  /** Flag to enable or disable the SendGrid email service. Defaults to true. */
  private boolean enabled = true;

  /**
   * Validates the SendGrid email configuration properties after construction.
   *
   * <p>If the email service is enabled, this method checks whether the API key is set and properly
   * configured. Logs a warning if the API key is missing, empty, or contains a placeholder value.
   * Otherwise, logs initialization information. If disabled, logs that status. This method is
   * invoked automatically after bean initialization.
   */
  @PostConstruct
  public void validateConfiguration() {
    if (enabled) {
      boolean notConfigured =
          sendgridApiKey == null
              || sendgridApiKey.isEmpty()
              || sendgridApiKey.contains("your-api-key");
      if (notConfigured) {
        log.warn("SendGrid API key is not properly configured. " + "Email service will not work.");
        log.warn("Set SENDGRID_API_KEY environment variable " + "or configure in application.yaml");
      } else {
        log.info("SendGrid email service initialized from: {} <{}>", fromName, fromEmail);
      }
    } else {
      log.info("SendGrid email service is disabled");
    }
  }
}
