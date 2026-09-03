# Architecture

There is no separate historical architecture file in the original monolith. This is the target layout implemented in this repository.

```text
Next.js Frontend :3000
      |
      v
API Gateway :8080  (Eureka: API-GATEWAY)
      |
      +-- USER-SERVICE :8081 + H2 userdb
      +-- FLIGHT-SERVICE :8082 + H2 flightdb
      +-- HOTEL-SERVICE :8083 + H2 hoteldb
      +-- AI-PLANNER-SERVICE :8084 (Groq + wttr.in)
      +-- TRIP-SERVICE :8085 + H2 tripdb

Eureka Server :8761
```

The frontend never calls service ports. Services do not share databases. Flight, hotel, and AI calls are orchestrated by the frontend through the gateway. Trip Service validates the User Service JWT locally using the shared `JWT_SECRET`. OpenFeign is not used because no service currently needs to call another service.
