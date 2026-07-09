# Add `package-info.java` to all source packages

## Goal

Add a `package-info.java` to every project-owned Java package under
`app/src/main/java`, matching the existing style in
`service/email/package-info.java` (2-sentence Javadoc + package declaration,
no license header).

## Plan

- [ ] 1. Create `package-info.java` in each of the 10 packages currently
      missing one:
      - `com.rubensgomes.userms`
      - `com.rubensgomes.userms.config`
      - `com.rubensgomes.userms.controller`
      - `com.rubensgomes.userms.domain`
      - `com.rubensgomes.userms.domain.dto`
      - `com.rubensgomes.userms.domain.entity`
      - `com.rubensgomes.userms.domain.model`
      - `com.rubensgomes.userms.repository`
      - `com.rubensgomes.userms.security`
      - `com.rubensgomes.userms.service`
- [ ] 2. `./gradlew build` to verify.

## Review

(to be filled in after completion)
