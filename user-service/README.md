# User Service

Registration, login, JWT issuance, roles, and current-user data.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8081`  
Database: `data/userdb.mv.db`

```bash
mvn spring-boot:run
```

Swagger: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Endpoints

### POST `/api/auth/register`
Auth: none  
Request: `{ "fullName": "Diya Sharma", "email": "diya@example.com", "password": "StrongPassword123" }`  
Response `200`: `{ "token": "...", "user": { "id": 1, "email": "...", "fullName": "...", "roles": ["USER"] } }`  
Status: `200`, `400`, `409`

### POST `/api/auth/login`
Auth: none  
Request: `{ "email": "diya@example.com", "password": "StrongPassword123" }`  
Response: same as register  
Status: `200`, `400`, `401`

### GET `/api/auth/me`
Auth: Bearer JWT  
Response: `{ "id": 1, "email": "...", "fullName": "...", "roles": ["USER"] }`  
Status: `200`, `401`

### GET `/api/users/me`
Auth: Bearer JWT  
Same response as `/api/auth/me`.

### PUT `/api/users/me`
Auth: Bearer JWT  
Request: `{ "fullName": "Diya Sharma" }`  
Response: user object  
Status: `200`, `400`, `401`

Passwords are stored with BCrypt and never returned.
