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
package com.rubensgomes.userms.security;

import com.rubensgomes.userms.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security UserDetails implementation for authenticated users.
 *
 * <p>This class represents the authenticated user principal in the Spring Security context. It
 * implements UserDetails interface to provide user information for authentication and authorization
 * decisions.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

  /** The user's unique identifier. */
  private UUID id;

  /** The user's email address. */
  private String email;

  /** The user's encrypted password. */
  private String password;

  /** Whether the user's email has been confirmed. */
  private Boolean confirmed;

  /** The user's granted authorities for security. */
  private Collection<? extends GrantedAuthority> authorities;

  /**
   * Creates a UserPrincipal from a User entity.
   *
   * @param user the user entity
   * @return UserPrincipal instance
   */
  public static UserPrincipal create(User user) {
    Collection<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

    return new UserPrincipal(
        user.getId(), user.getEmail(), user.getPassword(), user.getConfirmed(), authorities);
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return confirmed;
  }
}
