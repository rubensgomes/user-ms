# User Microservice (user-ms)

This project is a cloud native microservice that provides RESTful APIs to manage
the registration of users, authentication and password management using JWT tokens.
It provides APIs to create the user account, confirm the registration of the 
user account via email, change the user's account password, authenticate the 
user (e.g., during sign in), and display the user account information.


## Tech Stack

- **Java 21** - Programming language
- **Spring Boot 3.5.x** - Application framework
- **Gradle 9.0.x** - Build tool
- **MariaDB 11.8.x** - Production database
- **H2 Database** - Development/testing database
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **Lombok** - Boilerplate code reduction
- **OpenAPI 3.1** - API documentation specification
- **Swagger UI** - Interactive API documentation
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **JWT** - Token-based authentication

## Project Structure

```
com.rubensgomes.userms/
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # Data access layer
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── config/         # Configuration classes
├── security/       # Security configuration
└── exception/      # Custom exceptions
```

## Database Schema

### User Table

| Field               | Type         | Description                    |
|---------------------|--------------|--------------------------------|
| email               | VARCHAR(100) | User email address             |
| password            | VARCHAR(255) | Encrypted password             |
| created_at          | TIMESTAMP    | User creation timestamp        |
| password_changed_at | TIMESTAMP    | Last password change timestamp |

## Quick Start

### Prerequisites

- Java 21
- Gradle 9.0.x
- MariaDB 11.8.x (for production)
- H2 Database (included for development)

### Setup

#### Development Setup (Recommended)
1. Clone the repository
2. Start the application with H2 database: 
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```
3. Access H2 Console: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:userms`
   - Username: `sa`
   - Password: (leave empty)

#### Production Setup
1. Clone the repository
2. Install and configure MariaDB 11.8.x
3. Configure database connection in `application.yml`
4. Run database migrations
5. Start the application: `./gradlew bootRun`

### Environment Configuration

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/userms
    username: ${DB_USER:userms}
    password: ${DB_PASSWORD:password}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: ${JWT_EXPIRATION:86400000}
```

## API Documentation

Comprehensive API documentation is available in [API.md](./API.md).

### Quick Links

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## Development

### Development Commands

```bash
# Display Java version installed
./gradlew -q javaToolchains
```

```bash
# Lint and unit tests
./gradlew check -i
```

```bash
# Build the project
./gradlew build
```

```bash
# Only Rubens can release
./gradlew release -i
```

```bash
# Run application with H2 database (development)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

```bash
# Run application with MariaDB (production)
./gradlew bootRun
```

# Stop the running application
# Press Ctrl+C (or Cmd+C on Mac) in the terminal where the application is running

# Alternative: Kill the application process
ps aux | grep java | grep user-ms    # Find the process ID
kill <PID>                           # Replace <PID> with actual process ID

### Running with Docker

```bash
docker-compose up -d
```

## Security

- Passwords are encrypted using bcrypt
- JWT tokens for stateless authentication
- CORS configuration for cross-origin requests
- Input validation and sanitization

## Contributing

1. Follow Java coding standards
2. Write unit tests for new features
3. Update API documentation
4. Ensure all tests pass before submitting

## License

Apache License 2.0