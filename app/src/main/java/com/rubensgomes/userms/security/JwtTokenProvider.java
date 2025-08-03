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

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT token provider for generating and validating JSON Web Tokens.
 *
 * <p>This component handles JWT token creation, validation, and parsing for user authentication. It
 * uses HMAC-SHA256 algorithm for token signing and includes proper error handling for various JWT
 * validation scenarios.
 *
 * @author Rubens Gomes
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration:86400}")
  private Long jwtExpirationInSeconds;

  private Key key;

  /** Initializes the JWT signing key after bean construction. */
  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  /**
   * Generates a JWT token for the given user email.
   *
   * <p>The token includes the user's email as the subject and standard JWT claims for issued time
   * and expiration time.
   *
   * @param email the user's email address
   * @return JWT token string
   */
  public String generateToken(String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000);

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the user email from a JWT token.
   *
   * @param token the JWT token
   * @return the user's email address
   */
  public String getEmailFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  /**
   * Validates a JWT token.
   *
   * <p>This method checks token signature, expiration, and format. It logs specific error types for
   * debugging purposes.
   *
   * @param token the JWT token to validate
   * @return true if token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token format: {}", ex.getMessage());
    } catch (ExpiredJwtException ex) {
      log.error("JWT token is expired: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      log.error("JWT token is unsupported: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty: {}", ex.getMessage());
    } catch (JwtException ex) {
      log.error("JWT token validation failed: {}", ex.getMessage());
    }
    return false;
  }

  /**
   * Gets the JWT expiration time in seconds.
   *
   * @return expiration time in seconds
   */
  public Long getExpirationInSeconds() {
    return jwtExpirationInSeconds;
  }
}
