# Eureka Server — Requirements

## Purpose

Service discovery for the AI Travel Planner microservices. Eureka Server keeps track of available service instances so the API Gateway can route using logical service names instead of hard-coded ports.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Cloud Netflix Eureka Server
- Maven
- Port: `8761`

## Required configuration

- Enable Eureka Server.
- Disable self-registration and registry fetching for the standalone server.
- Dashboard available at `/`.

## Services expected to register

- `API-GATEWAY`
- `USER-SERVICE`
- `FLIGHT-SERVICE`
- `HOTEL-SERVICE`
- `AI-PLANNER-SERVICE`
- `TRIP-SERVICE`

## Routes

Eureka is infrastructure and does not expose application business routes. Its standard Eureka registry endpoints are provided by the Eureka server itself and should not be used by the frontend.

## README requirements

Document how to start the server and how to verify that registered services appear on the Eureka dashboard.
