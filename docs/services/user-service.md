# User Service — Requirements

## Purpose

Owns user registration, authentication, roles, JWT generation, and current-user information.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA/Hibernate
- Spring Security
- JWT
- H2 file database
- Springdoc OpenAPI
- Port: `8081`

## Database

Database file: `data/userdb.mv.db`.

Entities:

- User
- Role

A user owns its own authentication data. Other services must not query this database directly.

## Routes

### POST `/api/auth/register`
Register a user and return a JWT.

Request:
```json
{
  "fullName": "Diya Sharma",
  "email": "diya@example.com",
  "password": "StrongPassword123"
}
```

Response `200`:
```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "email": "diya@example.com",
    "fullName": "Diya Sharma",
    "roles": ["USER"]
  }
}
```

### POST `/api/auth/login`
Authenticate a user.

Request:
```json
{
  "email": "diya@example.com",
  "password": "StrongPassword123"
}
```

Response:
```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "email": "diya@example.com",
    "fullName": "Diya Sharma",
    "roles": ["USER"]
  }
}
```

### GET `/api/auth/me`
Return the authenticated user. Requires JWT.

Response:
```json
{
  "id": 1,
  "email": "diya@example.com",
  "fullName": "Diya Sharma",
  "roles": ["USER"]
}
```

### GET `/api/users/me`
Alias for current-user information. Requires JWT.

### PUT `/api/users/me`
Update editable profile information. Requires JWT.

Request:
```json
{
  "fullName": "Diya Sharma"
}
```

Response:
```json
{
  "id": 1,
  "email": "diya@example.com",
  "fullName": "Diya Sharma",
  "roles": ["USER"]
}
```

## Swagger

- `/swagger-ui.html`
- `/v3/api-docs`

## Notes

- Passwords must be hashed with BCrypt.
- Never return a password in any response.
- JWT should contain user ID/email and roles.
