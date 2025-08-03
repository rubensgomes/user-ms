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
package com.rubensgomes.userms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for the User Microservice.
 *
 * <p>This microservice provides RESTful APIs for user account management including: - User
 * registration with email confirmation - User authentication with JWT tokens - Password management
 * (change and reset) - User profile management
 *
 * <p>The application uses: - Spring Boot 3.x framework - Spring Security for authentication and
 * authorization - Spring Data JPA for database operations - MariaDB as the database - JWT for
 * stateless authentication - Spring Mail for email functionality
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
public class UserMsApplication {

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args command line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(UserMsApplication.class, args);
  }
}
