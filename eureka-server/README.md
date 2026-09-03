# Eureka Server

Service discovery for AI Travel Planner.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8761`

## Start

From this folder:

```bash
mvn spring-boot:run
```

Open [http://localhost:8761](http://localhost:8761). Registered applications should include `API-GATEWAY`, `USER-SERVICE`, `FLIGHT-SERVICE`, `HOTEL-SERVICE`, `AI-PLANNER-SERVICE`, and `TRIP-SERVICE` after those services start.

## Routes

Eureka is infrastructure. It does not expose business APIs. Use the dashboard at `/` to confirm registrations. The frontend must never call this port.
