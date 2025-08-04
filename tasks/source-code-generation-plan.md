# Complete User Microservice Source Code Generation Plan

## Overview

Generate all source code for the user microservice based on README.md and API.md
specifications. This includes the complete Spring Boot application with
comprehensive documentation and tests.

## Architecture Structure

```
com.rubensgomes.userms/
├── controller/     # REST controllers for API endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer (Spring Data JPA)
├── model/          # JPA entity classes
├── dto/            # Data transfer objects
├── config/         # Configuration classes
├── security/       # Security configuration & JWT handling
└── exception/      # Custom exceptions & error handling
```

## Core Components to Generate

### 1. Model Layer (JPA Entities)

**File: `model/User.java`**

- Main user entity with JPA annotations
- Fields: id (UUID), email, password, createdAt, passwordChangedAt,
  confirmationToken, confirmed, resetToken, resetTokenExpiry
- Lombok annotations for getters/setters
- Validation annotations
- Proper JPA relationships and constraints

### 2. DTO Layer (Data Transfer Objects)

**Files to create:**

- `dto/UserRegistrationRequest.java` - Registration input with validation
- `dto/UserRegistrationResponse.java` - Registration output
- `dto/LoginRequest.java` - Authentication input
- `dto/LoginResponse.java` - Authentication output with JWT token
- `dto/PasswordChangeRequest.java` - Password change input
- `dto/PasswordResetRequest.java` - Password reset input
- `dto/UserProfileResponse.java` - User profile output
- `dto/ErrorResponse.java` - Standardized error responses

### 3. Repository Layer

**File: `repository/UserRepository.java`**

- Spring Data JPA repository interface
- Custom query methods for finding by email, confirmation token, reset token
- Optional methods for user existence checks

### 4. Service Layer

**Files to create:**

- `service/UserService.java` - Core user management business logic
    - User registration with email confirmation
    - User profile management
    - Password change functionality
- `service/AuthService.java` - Authentication & JWT service
    - User login/authentication
    - JWT token generation and validation
    - Password reset functionality
- `service/EmailService.java` - Email sending service
    - Asynchronous email sending
    - HTML email templates
    - Confirmation and reset email logic

### 5. Controller Layer

**Files to create:**

- `controller/UserController.java` - User management endpoints
    - POST /api/user/register
    - GET /api/user/confirm
    - PUT /api/user/change-password
    - GET /api/user/profile
- `controller/AuthController.java` - Authentication endpoints
    - POST /api/auth/login
    - POST /api/auth/forgot-password
    - POST /api/auth/reset-password

### 6. Security Configuration

**Files to create:**

- `security/SecurityConfig.java` - Spring Security configuration
    - JWT authentication setup
    - Endpoint security rules
    - CORS configuration
- `security/JwtAuthenticationFilter.java` - JWT token validation filter
- `security/JwtTokenProvider.java` - JWT token generation/validation utility
- `security/UserPrincipal.java` - Security user details implementation

### 7. Configuration Classes

**Files to create:**

- `config/DatabaseConfig.java` - Database configuration
- `config/EmailConfig.java` - Email service configuration
- `config/OpenApiConfig.java` - Swagger/OpenAPI documentation configuration

### 8. Exception Handling

**Files to create:**

- `exception/GlobalExceptionHandler.java` - Centralized exception handling with
  @ControllerAdvice
- `exception/UserNotFoundException.java` - Custom exception
- `exception/EmailAlreadyExistsException.java` - Custom exception
- `exception/InvalidTokenException.java` - Custom exception
- `exception/InvalidCredentialsException.java` - Custom exception

### 9. Main Application Class

**File: `UserMsApplication.java`** (replace App.java)

- Spring Boot main application class
- Enable JPA repositories, async processing
- Proper component scanning

## API Endpoints Implementation

### Authentication Not Required

1. **POST /api/user/register** - User registration
    - Validate email format and password strength
    - Check email uniqueness
    - Generate confirmation token
    - Send confirmation email asynchronously
    - Return user info without sensitive data

2. **GET /api/user/confirm?token=...** - Email confirmation
    - Validate confirmation token
    - Activate user account
    - Update confirmed timestamp

3. **POST /api/auth/login** - User authentication
    - Validate credentials
    - Generate JWT token
    - Return token with user info

4. **POST /api/auth/forgot-password** - Password reset request
    - Validate email exists
    - Generate reset token with expiry
    - Send reset email asynchronously

5. **POST /api/auth/reset-password** - Password reset
    - Validate reset token and expiry
    - Update password with encryption
    - Clear reset token

### Authentication Required (JWT)

6. **PUT /api/user/change-password** - Password change
    - Validate current password
    - Update with new encrypted password
    - Update password change timestamp

7. **GET /api/user/profile** - User profile
    - Return current user information
    - No sensitive data exposure

## Testing Strategy

### Unit Tests (src/test/java)

**Files to create:**

- `service/UserServiceTest.java`
- `service/AuthServiceTest.java`
- `service/EmailServiceTest.java`
- `security/JwtTokenProviderTest.java`
- `repository/UserRepositoryTest.java`

### Integration Tests

**Files to create:**

- `controller/UserControllerTest.java`
- `controller/AuthControllerTest.java`
- `security/SecurityConfigTest.java`

### Test Configuration

- `config/TestConfig.java` - Test-specific configuration
- `TestUserMsApplication.java` - Test application class

## Implementation Tasks Breakdown

### Phase 1: Core Infrastructure (8 files)

1. Update UserMsApplication.java (main class)
2. Create User.java (entity)
3. Create UserRepository.java
4. Create basic DTO classes (4 files)

### Phase 2: Service Layer (3 files)

5. Create UserService.java
6. Create AuthService.java
7. Create EmailService.java

### Phase 3: Security Layer (4 files)

8. Create SecurityConfig.java
9. Create JwtTokenProvider.java
10. Create JwtAuthenticationFilter.java
11. Create UserPrincipal.java

### Phase 4: Controller Layer (2 files)

12. Create UserController.java
13. Create AuthController.java

### Phase 5: Exception Handling (5 files)

14. Create GlobalExceptionHandler.java
15. Create custom exception classes (4 files)

### Phase 6: Configuration (3 files)

16. Create configuration classes (3 files)

### Phase 7: Testing (8+ files)

17. Create unit tests (5 files)
18. Create integration tests (3 files)

## Documentation Standards

### JavaDoc Requirements

- All public classes must have class-level JavaDoc
- All public methods must have method-level JavaDoc
- Parameter descriptions with @param
- Return value descriptions with @return
- Exception documentation with @throws
- Usage examples where applicable
- Author and version information

### Code Standards

- Use Lombok annotations to reduce boilerplate
- Follow Spring Boot conventions
- Implement proper validation with Bean Validation
- Use meaningful variable and method names
- Add inline comments for complex business logic
- Follow RESTful API design principles

## Application Properties

Create application.yml with:

- Database configuration
- JWT configuration
- Email service configuration
- Logging configuration
- Server configuration

## Validation & Security

- Input validation on all endpoints
- Password encryption with BCrypt
- JWT token expiration and refresh
- Rate limiting considerations
- CORS configuration
- SQL injection prevention
- XSS protection

## Total Files to Generate

- **Main source files**: ~25 files
- **Test files**: ~10 files
- **Configuration files**: ~3 files
- **Total**: ~38 files

This comprehensive plan will result in a production-ready user microservice with
full test coverage and documentation.