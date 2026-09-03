# AI Planner Service — Requirements

## Purpose

Generate the final personalized travel itinerary using the existing Groq integration after the user has selected a flight and hotel.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Springdoc OpenAPI
- Eureka Client
- Groq/LLM HTTP integration
- Port: `8084`

## Important communication rule

This service is called **only after flight and hotel selection**. It does not need to call Flight Service or Hotel Service if the frontend sends the selected-flight and selected-hotel snapshots in the request.

This keeps service coupling low.

## POST `/api/ai/itinerary`

Generate an itinerary.

Request:
```json
{
  "origin": "DEL",
  "destination": "GOI",
  "startDate": "2026-10-10",
  "endDate": "2026-10-15",
  "travelers": 2,
  "maxTotalBudget": 50000,
  "maxHotelBudgetPerNight": 5000,
  "interests": ["Food", "Nature", "Adventure"],
  "selectedFlight": {
    "flightId": "AI-123",
    "airline": "Example Air",
    "flightNumber": "EA123",
    "price": 8500,
    "currency": "INR"
  },
  "selectedHotel": {
    "hotelId": "H-101",
    "name": "Example Beach Hotel",
    "pricePerNight": 4200,
    "currency": "INR"
  }
}
```

Response:
```json
{
  "tripSummary": {
    "origin": "DEL",
    "destination": "GOI",
    "travelers": 2,
    "startDate": "2026-10-10",
    "endDate": "2026-10-15"
  },
  "selectedFlight": {
    "flightId": "AI-123",
    "airline": "Example Air",
    "flightNumber": "EA123",
    "price": 8500,
    "currency": "INR"
  },
  "selectedHotel": {
    "hotelId": "H-101",
    "name": "Example Beach Hotel",
    "pricePerNight": 4200,
    "currency": "INR"
  },
  "itinerary": [
    {
      "day": 1,
      "date": "2026-10-10",
      "activities": [
        {
          "time": "14:00",
          "title": "Hotel check-in",
          "description": "Check in and relax."
        }
      ]
    }
  ],
  "budgetEstimate": {
    "flight": 8500,
    "hotel": 21000,
    "food": 6000,
    "activities": 4000,
    "localTransport": 3000,
    "total": 42500,
    "currency": "INR"
  },
  "travelTips": ["Carry light cotton clothing."]
}
```

## GET `/api/ai/health`

Simple service/AI dependency health check.

Response:
```json
{
  "status": "UP"
}
```

## AI requirements

- Move the existing Groq API integration from the monolith into this service.
- Keep the model configurable using environment variables/properties.
- Never hard-code the API key.
- Validate and sanitize user input before constructing the LLM prompt.
- Return structured JSON rather than raw LLM text whenever possible.
- If the AI provider fails, return a clear `503` response.

## Swagger

- `/swagger-ui.html`
- `/v3/api-docs`
