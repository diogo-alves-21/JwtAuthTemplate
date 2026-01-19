# JWT Authentication Template

A robust, production-ready JWT authentication system built with Spring Boot 3.5, providing secure user authentication, authorization, and password management features.

## 🚀 Features

-  **JWT Authentication** - Secure token-based authentication with access and refresh tokens
-  **User Management** - User registration, login, and profile management
-  **Password Reset** - Secure password reset functionality via email
-  **Email Integration** - SMTP-based email service for password resets
-  **Token Refresh** - Automatic token refresh mechanism
- ️ **PostgreSQL Database** - Robust data persistence with JPA/Hibernate
-  **Security Best Practices** - CORS, CSRF protection, secure headers
-  **Input Validation** - Comprehensive validation using Bean Validation
-  **Transaction Management** - Proper database transaction handling

## 🏗️ Architecture

This template follows a layered architecture with clear separation of concerns:

- **Controller Layer** - REST API endpoints
- **Service Layer** - Business logic and transaction management
- **Repository Layer** - Data access with Spring Data JPA
- **Configuration Layer** - Security, email, and application configuration
- **DTO Layer** - Data transfer objects for API communication

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.5.7
- **Security:** Spring Security 6.x
- **JWT:** JJWT (JSON Web Tokens)
- **Database:** PostgreSQL with Hibernate/JPA
- **Email:** JavaMail with SMTP
- **Build Tool:** Maven
- **Java Version:** 25

## 📋 Prerequisites

- **Java:** JDK 25+
- **Database:** PostgreSQL 12+
- **Build Tool:** Maven 3.6+
- **Email Service:** SMTP server (Gmail, SendGrid, etc.)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd jwt-auth-template
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/jwt_auth_db
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET_KEY=your-256-bit-secret-key-here-minimum-32-characters-long

# Email Configuration
SUPPORT_EMAIL=your-email@example.com
APP_PASSWORD=your-app-password-or-smtp-password
```

### 3. Database Setup

Create a PostgreSQL database named `jwt_auth_db` and ensure the user has appropriate permissions.

### 4. Build and Run

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### User Registration
```http
POST /auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### User Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresAt": "2024-01-20T10:30:00Z"
}
```

#### Token Refresh
```http
POST /auth/refresh-token
Authorization: Bearer <refresh-token>
```

#### Password Reset Request
```http
POST /auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

#### Password Reset Validation
```http
GET /auth/reset-password?token=<reset-token>
```

#### Password Reset Confirmation
```http
POST /auth/reset-password/confirm
Content-Type: application/json

{
  "token": "<reset-token>",
  "password": "NewSecurePass123"
}
```

### User Management Endpoints

#### Get Current User Profile
```http
GET /users/me
Authorization: Bearer <access-token>
```

#### Get All Users
```http
GET /users
Authorization: Bearer <access-token>
```

## 🔒 Security Features

- **JWT Token Authentication** with configurable expiration times
- **Refresh Token Rotation** for enhanced security
- **Password Encryption** using BCrypt
- **CORS Configuration** for cross-origin requests
- **CSRF Protection** disabled for stateless API design
- **Input Validation** on all endpoints
- **Role-Based Authorization** (USER, ADMIN)
- **Secure Headers** configuration

## 📧 Email Configuration

The application includes email functionality for password resets. Configure your SMTP settings in the `.env` file:

```env
# For Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# For other providers, adjust accordingly
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=your-sendgrid-api-key
```

## 🔧 Configuration

### Application Properties

Key configuration properties (set via environment variables):

```properties
# Database
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=create-drop

# JWT
security.jwt.secret-key=${JWT_SECRET_KEY}
security.jwt.expiration-time=3600000
security.jwt.refresh-token-time=1209600000

# Email
spring.mail.host=smtp.io.com
spring.mail.port=587
spring.mail.username=${SUPPORT_EMAIL}
spring.mail.password=${APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging
logging.level.org.example.security.auth.config=DEBUG
```

### CORS Configuration

CORS is configured in `SecurityConfiguration.java`. By default, it allows:
- Origins: `http://localhost:8080`, `http://localhost:3000`
- Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Headers: Authorization, Content-Type
- Credentials: true

## 🧪 Testing

Run the tests using Maven:

```bash
mvn test
```

## 🚀 Deployment

### Building for Production

```bash
mvn clean package -DskipTests
```

This creates a JAR file in the `target/` directory that can be run with:

```bash
java -jar target/security-0.0.1-SNAPSHOT.jar
```

### Docker Deployment (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:25-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:

```bash
docker build -t jwt-auth-app .
docker run -p 8080:8080 jwt-auth-app
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🔗 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - Learn more about JSON Web Tokens
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
