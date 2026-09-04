# AI Travel Planner

AI Travel Planner is a Spring Boot microservices application for planning trips with authentication, flight search, hotel search, AI-generated itineraries, and saved trip records.

The project combines a Next.js frontend with a backend built from independent Java services connected through Spring Cloud Gateway and Eureka service discovery.

## Overview

This repository includes:

- Eureka service registry
- API Gateway for routing and CORS
- User service for authentication and profile management
- Flight service for flight search and airport data
- Hotel service for hotel search and destination data
- AI planner service for generating trip suggestions with Groq
- Trip service for saving and viewing trips
- A Next.js frontend for user interaction

## Architecture

```text
Next.js Frontend :3000
        |
        v
API Gateway :8080
        |
        +--> USER-SERVICE :8081
        +--> FLIGHT-SERVICE :8082
        +--> HOTEL-SERVICE :8083
        +--> AI-PLANNER-SERVICE :8084
        +--> TRIP-SERVICE :8085
        |
        v
Eureka Server :8761
```

### Service responsibilities

| Service | Port | Purpose |
| --- | ---: | --- |
| `eureka-server` | 8761 | Service discovery and registry |
| `api-gateway` | 8080 | Routes requests to backend services |
| `user-service` | 8081 | Login, registration, current user info |
| `flight-service` | 8082 | Airport and flight search |
| `hotel-service` | 8083 | Destination and hotel search |
| `ai-planner-service` | 8084 | AI-generated itinerary planning |
| `trip-service` | 8085 | Save and fetch trips |

## Features

### Authentication
- Register a new account
- Login with email/password
- Fetch current authenticated user

### Flight search
- Search flights by origin, destination, dates, and travel details
- Get airport list
- Fetch flight details by flight ID

### Hotel search
- Search hotels by destination or city
- Get destination suggestions
- Fetch hotel details by hotel ID

### AI itinerary generation
- Generate itinerary recommendations using Groq
- Health check for AI service configuration

### Trip management
- Save a trip
- List saved trips
- Get a trip by ID
- Delete a trip

## Technology stack

- Java 21
- Spring Boot 3.3.0
- Spring Cloud 2023.0.3
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Data JPA
- Spring Validation
- JWT authentication
- H2 database (per service)
- Maven
- Next.js 14
- Groq API for AI itinerary generation

## Project structure

```text
AI_Travel_revised/
├── api-gateway/
├── eureka-server/
├── user-service/
├── flight-service/
├── hotel-service/
├── ai-planner-service/
├── trip-service/
├── frontend/
├── travel-planner/
├── docs/
├── pom.xml
├── README.md
└── PROJECT_REQUIREMENTS.md
```

## Prerequisites

Before running the project, install:

- Java 21+
- Maven
- Node.js and npm
- Git

For AI itinerary generation, set the environment variable:

```bash
export GROQ_API_KEY="your_key_here"
```

## Run locally

Start each service in a separate terminal, in this order:

### 1. Start service discovery

```bash
cd eureka-server
mvn spring-boot:run
```

Open: http://localhost:8761

### 2. Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

### 3. Start backend services

```bash
cd user-service
mvn spring-boot:run
```

```bash
cd flight-service
mvn spring-boot:run
```

```bash
cd hotel-service
mvn spring-boot:run
```

```bash
cd ai-planner-service
mvn spring-boot:run
```

```bash
cd trip-service
mvn spring-boot:run
```

### 4. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Open:

- Frontend: http://localhost:3000
- Gateway: http://localhost:8080
- Eureka: http://localhost:8761

## Important API routes

The frontend communicates with the API Gateway instead of individual service ports.

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Flights
- `POST /api/flights/search`
- `GET /api/flights/airports`
- `GET /api/flights/{flightId}`

### Hotels
- `POST /api/hotels/search`
- `GET /api/hotels/destinations`
- `GET /api/hotels/{hotelId}`

### AI planner
- `POST /api/ai/itinerary`
- `GET /api/ai/health`

### Trips
- `POST /api/trips`
- `GET /api/trips`
- `GET /api/trips/{id}`
- `DELETE /api/trips/{id}`

## Main request flow

A common flow in the app is:

```text
Frontend -> API Gateway -> Auth / Flight / Hotel / AI / Trip services
```

Typical trip flow:

```text
Login -> search flights -> search hotels -> generate itinerary -> save trip
```

## Demo users

The app includes demo credentials for local testing:

- `demo@travelai.com` / `demo123`
- `admin@travelai.com` / `admin123`

## Notes

- The project uses a microservices architecture for modularity and independent deployment.
- The original monolith remains in the `travel-planner/` folder as a legacy reference.
- This project is meant for learning, local development, and demonstration of service-oriented architecture.

## License

This project is intended for educational and development use.
