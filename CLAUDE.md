# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Claude rules

1. First think through the problem, read the codebase for relevant files, and
   write a plan to tasks/todo.md.
2. The plan should have a list of todo items that you can check off as you
   complete them
3. Before you begin working, check in with me and I will verify the plan.
4. Then, begin working on the todo items, marking them as complete as you go.
5. Please every step of the way just give me a high level explanation of what
   changes you made
6. Make every task and code change you do as simple as possible. We want to
   avoid making any massive or complex changes. Every change should impact as
   little code as possible. Everything is about simplicity.
7. Finally, add a review section to the [todo.md](http://todo.md/) file with a
   summary of the changes you made and any other relevant information.

## Project Overview

This is a cloud-native Spring Boot microservice for user account management
written in Java 21. It provides RESTful APIs for user registration,
authentication, password management, and profile management using JWT tokens.

**Tech Stack**: Java 21, Spring Boot 3.5.x, Gradle 9.0.x, MariaDB 11.8.x, Spring
Security, Spring Data JPA, JWT

## Development Commands

### Build and Test

```bash
# Build the project
./gradlew build

# Run tests with JUnit 5
./gradlew test

# Run tests with coverage report (JaCoCo)
./gradlew jacocoTestReport

# Run application locally with MariaDB (production profile)
./gradlew bootRun

# Run application with H2 in-memory database (development profile)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Check code quality (includes tests, jacoco, spotless)
./gradlew check
```

### Code Quality

```bash
# Apply code formatting (Google Java Format)
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck

# Run SonarQube analysis (requires SONAR_TOKEN env var)
./gradlew sonar
```

### Docker

```bash
# Run with Docker Compose (includes MariaDB)
docker-compose up -d
```

### Release Management

```bash
# Create release (requires release branch)
./gradlew release
```

## Project Architecture

### Package Structure

- `com.rubensgomes.userms/`
    - `controller/` - REST controllers for API endpoints
    - `service/` - Business logic layer
    - `repository/` - Data access layer (Spring Data JPA)
    - `model/` - JPA entity classes
    - `dto/` - Data transfer objects for API requests/responses
    - `config/` - Configuration classes
    - `security/` - Security configuration and JWT handling
    - `exception/` - Custom exception classes

### Key APIs

- `POST /api/user/register` - User registration
- `GET /api/user/confirm` - Email confirmation
- `POST /api/auth/login` - User authentication
- `POST /api/auth/forgot-password` - Password reset request
- `POST /api/auth/reset-password` - Password reset
- `PUT /api/user/change-password` - Password change (authenticated)
- `GET /api/user/profile` - User profile (authenticated)

### Database Schema

Single `User` table with fields: email (primary), password (bcrypt), created_at,
password_changed_at, confirmation tokens

## Environment Configuration

Required environment variables:

- `DB_USER` - Database username (default: userms)
- `DB_PASSWORD` - Database password (default: password)
- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - JWT expiration time (default: 86400000ms)
- `SONAR_TOKEN` - SonarQube token for code analysis
- `REPSY_USERNAME` - Maven repository username

Database connections:

- Production: `jdbc:mariadb://localhost:3306/userms`
- Development: `jdbc:h2:mem:userms` (H2 in-memory database)

## Development Database Setup

### H2 Database (Recommended for Development)

```bash
# Run with development profile (uses H2)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Access H2 Console
http://localhost:8080/h2-console

# H2 Console Login:
# JDBC URL: jdbc:h2:mem:userms
# User Name: sa
# Password: (leave empty)
```

### MariaDB (Production)

Requires MariaDB 11.8.x installation and database setup as per README.md

## Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
- H2 Console (dev profile): http://localhost:8080/h2-console
- API specifications: [API.md](./API.md)

## Code Standards

- Java 21 with Amazon Corretto JDK
- Google Java Format for code styling (applied via Spotless)
- JUnit 5 for testing with Mockito for mocking
- Spring Boot conventions for REST APIs
- Lombok for reducing boilerplate
- bcrypt for password encryption
- Layered JAR packaging enabled for containerization