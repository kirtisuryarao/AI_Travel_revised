# AI Travel Planner

Microservices rewrite of the original Spring Boot TravelAI app. The Next.js frontend keeps the existing TravelAI visual language and talks only to the API Gateway.

```text
Java: 21
Spring Boot: 3.3.0
Spring Cloud: 2023.0.3
Build: Maven
Frontend: Next.js 14
```

## Run locally

1. Start Eureka: `cd eureka-server && mvn spring-boot:run`
2. Start Gateway: `cd api-gateway && mvn spring-boot:run`
3. Start `user-service` (8081), `flight-service` (8082), `hotel-service` (8083), `ai-planner-service` (8084), `trip-service` (8085)
4. Set `GROQ_API_KEY` for itinerary generation
5. Frontend: `cd frontend && npm install && npm run dev`

Open [http://localhost:3000](http://localhost:3000) and [http://localhost:8761](http://localhost:8761).

From the repository root you can also run:

```bash
mvn -q test
```

## Flow

Trip form (including **Maximum Hotel Budget Per Night**) → `POST /api/flights/search` → select flight → `POST /api/hotels/search` → select hotel → `POST /api/ai/itinerary` → `POST /api/trips`.

The original monolith remains under `travel-planner/` as a reference. Product/order features were not migrated, per the current requirements.

## Demo users

- `demo@travelai.com` / `demo123`
- `admin@travelai.com` / `admin123`
