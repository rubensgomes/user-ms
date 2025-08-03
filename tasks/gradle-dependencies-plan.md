
# Gradle Build File Dependencies Update Plan

## Current State Analysis
The current `app/build.gradle.kts` has basic Spring Boot setup but is missing essential dependencies for the user microservice features described in README.md and API.md.

## Missing Dependencies to Add

### 1. Security & JWT (Critical)
- `spring-boot-starter-security` - Spring Security framework for authentication/authorization
- `io.jsonwebtoken:jjwt-api` - JWT API
- `io.jsonwebtoken:jjwt-impl` - JWT implementation (runtimeOnly)
- `io.jsonwebtoken:jjwt-jackson` - JWT Jackson integration (runtimeOnly)

### 2. Database & JPA (Critical)
- `spring-boot-starter-data-jpa` - Spring Data JPA for database operations
- `org.mariadb.jdbc:mariadb-java-client` - MariaDB JDBC driver (runtimeOnly)

### 3. API Documentation (Required)
- `org.springdoc:springdoc-openapi-starter-webmvc-ui` - OpenAPI 3.1/Swagger UI integration

### 4. Email Support (Required)
- `spring-boot-starter-mail` - Email functionality for account confirmations and password resets

### 5. Boilerplate Reduction (Required)
- `org.projectlombok:lombok` - Reduce boilerplate code
  - Add as `compileOnly` dependency
  - Add as `annotationProcessor` dependency

### 6. Testing Enhancements
- `org.springframework.security:spring-security-test` - Security testing utilities
- `org.testcontainers:mariadb` - Database testing with containers (optional)

## Implementation Tasks

### Task 1: Update Implementation Dependencies
Add to the `implementation` section:
```kotlin
// Security and JWT
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("io.jsonwebtoken:jjwt-api:0.12.3")

// Database
implementation("org.springframework.boot:spring-boot-starter-data-jpa")

// API Documentation
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

// Email
implementation("org.springframework.boot:spring-boot-starter-mail")
```

### Task 2: Update CompileOnly Dependencies
Add to the `compileOnly` section:
```kotlin
compileOnly("org.projectlombok:lombok")
```

### Task 3: Update AnnotationProcessor Dependencies
Add new section:
```kotlin
// ########## annotationProcessor ####################################
annotationProcessor("org.projectlombok:lombok")
```

### Task 4: Update RuntimeOnly Dependencies
Add to the `runtimeOnly` section:
```kotlin
runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
```

### Task 5: Update TestImplementation Dependencies
Add to the `testImplementation` section:
```kotlin
testImplementation("org.springframework.security:spring-security-test")
// Optional: testImplementation("org.testcontainers:mariadb")
```

### Task 6: Clean Up TODO Comments
Remove or update the TODO comments that reference adding dependencies.

## Validation Checklist
- [ ] All dependencies align with tech stack in README.md
- [ ] Dependencies support API requirements from API.md
- [ ] JWT authentication capabilities added
- [ ] Database connectivity for MariaDB added
- [ ] Email functionality for user workflows added
- [ ] API documentation (Swagger/OpenAPI) added
- [ ] Lombok for code reduction added
- [ ] Security testing utilities added
- [ ] No breaking changes to existing configuration

## Notes
- Version numbers should be managed by Spring Boot dependency management where possible
- JWT library versions are explicitly specified as they're not managed by Spring Boot
- OpenAPI library version is specified to ensure compatibility with Spring Boot 3.x
- All dependencies are aligned with the user microservice requirements for registration, authentication, password management, and profile management