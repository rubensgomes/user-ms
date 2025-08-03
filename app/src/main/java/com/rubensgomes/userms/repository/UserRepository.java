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
package com.rubensgomes.userms.repository;

import com.rubensgomes.userms.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity data access operations.
 *
 * <p>This repository provides CRUD operations and custom query methods for User entities. It
 * extends Spring Data JPA's JpaRepository to inherit standard database operations and defines
 * additional methods for user-specific queries.
 *
 * <p>Key operations include: - Finding users by email for authentication - Finding users by
 * confirmation tokens for email verification - Finding users by reset tokens for password reset -
 * Checking email existence for validation
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Finds a user by their email address.
   *
   * <p>This method is used for authentication and user lookup operations. Email addresses are
   * unique in the system, so this will return at most one user.
   *
   * @param email the email address to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  Optional<User> findByEmail(String email);

  /**
   * Finds a user by their confirmation token.
   *
   * <p>This method is used during the email confirmation process when users click the confirmation
   * link in their registration email.
   *
   * @param confirmationToken the confirmation token to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  Optional<User> findByConfirmationToken(String confirmationToken);

  /**
   * Finds a user by their password reset token.
   *
   * <p>This method is used during the password reset process when users click the reset link in
   * their password reset email.
   *
   * @param resetToken the reset token to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  Optional<User> findByResetToken(String resetToken);

  /**
   * Checks if a user exists with the given email address.
   *
   * <p>This method is used for validation during user registration to ensure email uniqueness
   * without loading the entire user entity.
   *
   * @param email the email address to check
   * @return true if a user with this email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Finds all confirmed users.
   *
   * <p>This method can be used for administrative purposes or analytics to get a list of all users
   * who have confirmed their email addresses.
   *
   * @return a list of all confirmed users
   */
  @Query("SELECT u FROM User u WHERE u.confirmed = true")
  java.util.List<User> findAllConfirmedUsers();

  /**
   * Counts the number of confirmed users.
   *
   * <p>This method provides a count of active, confirmed users for metrics and analytics purposes.
   *
   * @return the number of confirmed users
   */
  @Query("SELECT COUNT(u) FROM User u WHERE u.confirmed = true")
  long countConfirmedUsers();

  /**
   * Finds users created within a specific date range.
   *
   * <p>This method can be used for analytics and reporting purposes to track user registration
   * trends over time.
   *
   * @param startDate the start date of the range
   * @param endDate the end date of the range
   * @return a list of users created within the date range
   */
  @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
  java.util.List<User> findUsersCreatedBetween(
      @Param("startDate") java.time.LocalDateTime startDate,
      @Param("endDate") java.time.LocalDateTime endDate);
}
