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

import com.rubensgomes.userms.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter for validating JWT tokens in requests.
 *
 * <p>This filter intercepts incoming HTTP requests, extracts JWT tokens from the Authorization
 * header, validates them, and sets up the security context for authenticated users.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserRepository userRepository;

  /**
   * Processes each HTTP request to validate JWT tokens and set authentication context.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if servlet processing fails
   * @throws IOException if I/O operation fails
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        String email = tokenProvider.getEmailFromToken(jwt);

        // Load user details from database
        userRepository
            .findByEmail(email)
            .ifPresent(
                user -> {
                  UserPrincipal userPrincipal = UserPrincipal.create(user);
                  UsernamePasswordAuthenticationToken authentication =
                      new UsernamePasswordAuthenticationToken(
                          userPrincipal, null, userPrincipal.getAuthorities());
                  authentication.setDetails(
                      new WebAuthenticationDetailsSource().buildDetails(request));

                  SecurityContextHolder.getContext().setAuthentication(authentication);
                });
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts JWT token from the Authorization header.
   *
   * <p>Expects the header format: "Bearer {token}"
   *
   * @param request the HTTP request
   * @return JWT token string or null if not found
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
