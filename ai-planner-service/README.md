# AI Planner Service

Generates a personalized itinerary after the user has selected a flight and hotel. Uses the existing Groq integration.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8084`

Set `GROQ_API_KEY` before generating itineraries. Optional: `GROQ_API_MODEL`.

```bash
mvn spring-boot:run
```

Swagger: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)

This service does not call Flight Service or Hotel Service. The frontend sends selected snapshots.

## Endpoints

### POST `/api/ai/itinerary`
Auth: none  
Request includes origin, destination, dates, travelers, budgets, interests, `selectedFlight`, and `selectedHotel`.  
Response `200`: trip summary, selected flight/hotel, weather, day-by-day itinerary, budget estimate, packing suggestions, travel tips.  
Status: `200`, `400`, `503` if Groq is unavailable.

### GET `/api/ai/health`
Auth: none  
Response: `{ "status": "UP" }` when a Groq key is configured.
