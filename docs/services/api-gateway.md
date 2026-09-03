# API Gateway — Requirements

## Purpose

The API Gateway is the single backend entry point for the frontend. The frontend must never directly call a microservice port.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Cloud Gateway
- Eureka Client
- Maven
- Port: `8080`

## Routing

Use Eureka service discovery and load-balanced URIs.

| Public route | Target service |
|---|---|
| `/api/auth/**` | `USER-SERVICE` |
| `/api/users/**` | `USER-SERVICE` |
| `/api/flights/**` | `FLIGHT-SERVICE` |
| `/api/hotels/**` | `HOTEL-SERVICE` |
| `/api/ai/**` | `AI-PLANNER-SERVICE` |
| `/api/trips/**` | `TRIP-SERVICE` |

## Gateway routes

### `POST /api/auth/register`
Forward to `USER-SERVICE /api/auth/register`.

Request:
```json
{
  "fullName": "Diya Sharma",
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

### `POST /api/auth/login`
Forward to `USER-SERVICE /api/auth/login`.

Request:
```json
{
  "email": "diya@example.com",
  "password": "StrongPassword123"
}
```

Response: same authentication response shape as registration.

### `GET /api/auth/me`
Forward to User Service. Requires Bearer JWT.

### `POST /api/flights/search`
Forward to Flight Service.

### `POST /api/hotels/search`
Forward to Hotel Service.

### `POST /api/ai/itinerary`
Forward to AI Planner Service.

### `POST /api/trips`
Forward to Trip Service. Requires Bearer JWT.

### `GET /api/trips`
Forward to Trip Service. Requires Bearer JWT.

### `GET /api/trips/{id}`
Forward to Trip Service. Requires Bearer JWT.

### `DELETE /api/trips/{id}`
Forward to Trip Service. Requires Bearer JWT.

## Security

- Forward the `Authorization` header.
- Do not store JWTs in the gateway database.
- Add CORS configuration for the Next.js frontend.
- Return clean 404/503 JSON errors when a target service is unavailable.

## Swagger

Gateway should expose its own OpenAPI information for gateway-facing routes if practical. Business endpoint Swagger remains available in each downstream service.
