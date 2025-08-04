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

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for the user microservice.
 *
 * <p>This configuration sets up JWT-based authentication, CORS policy, and endpoint security rules.
 * It defines which endpoints require authentication and configures the JWT authentication filter.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Password encoder bean for encrypting and validating passwords.
   *
   * @return BCrypt password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Security filter chain configuration.
   *
   * <p>Configures HTTP security settings including: - CORS policy - CSRF protection (disabled for
   * stateless API) - Session management (stateless) - Endpoint authorization rules - JWT
   * authentication filter
   *
   * @param http the HttpSecurity to configure
   * @return configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Enable CORS with custom configuration
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // Disable CSRF for stateless API
        .csrf(AbstractHttpConfigurer::disable)

        // Stateless session management for JWT
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Configure endpoint authorization
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints - no authentication required
                    .requestMatchers(
                        "/api/user/register",
                        "/api/user/confirm",
                        "/api/auth/login",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password")
                    .permitAll()

                    // Documentation endpoints
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                    .permitAll()

                    // H2 console (for development purposes)
                    .requestMatchers("/h2-console/**")
                    .permitAll()

                    // Actuator endpoints
                    .requestMatchers("/actuator/**")
                    .permitAll()

                    // All other endpoints require authentication
                    .anyRequest()
                    .authenticated())

        // Add JWT authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * CORS configuration for cross-origin requests.
   *
   * <p>Configures CORS policy to allow requests from frontend applications. In production, this
   * should be restricted to specific domains.
   *
   * @return CORS configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow requests from all origins (configure specific domains in production)
    configuration.setAllowedOriginPatterns(List.of("*"));

    // Allow standard HTTP methods
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Allow standard headers plus Authorization
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));

    // Allow credentials (required for Authorization header)
    configuration.setAllowCredentials(true);

    // Cache preflight requests for 1 hour
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
