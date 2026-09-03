# Flight Service — Requirements

## Purpose

Search available flights for the trip form and return normalized flight options. The service owns flight-search integration and does not call Hotel Service.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA/Hibernate if search history/cache is persisted
- H2 file database if persistence is used
- Springdoc OpenAPI
- Eureka Client
- Port: `8082`

## Main flow

```text
Frontend -> API Gateway -> Flight Service -> External Flight API
```

The service should hide external provider-specific response formats from the frontend.

## POST `/api/flights/search`

Search flights.

Request:
```json
{
  "origin": "DEL",
  "destination": "GOI",
  "departureDate": "2026-10-10",
  "returnDate": "2026-10-15",
  "travelers": 2,
  "maxTotalBudget": 50000,
  "currency": "INR"
}
```

Response:
```json
{
  "searchId": "FL-10001",
  "origin": "DEL",
  "destination": "GOI",
  "flights": [
    {
      "flightId": "AI-123",
      "airline": "Example Air",
      "flightNumber": "EA123",
      "departure": {
        "airport": "DEL",
        "time": "2026-10-10T08:30:00+05:30"
      },
      "arrival": {
        "airport": "GOI",
        "time": "2026-10-10T11:00:00+05:30"
      },
      "durationMinutes": 150,
      "stops": 0,
      "price": 8500,
      "currency": "INR"
    }
  ]
}
```

## GET `/api/flights/{flightId}`

Return details for a selected flight reference.

Response:
```json
{
  "flightId": "AI-123",
  "airline": "Example Air",
  "flightNumber": "EA123",
  "origin": "DEL",
  "destination": "GOI",
  "departureTime": "2026-10-10T08:30:00+05:30",
  "arrivalTime": "2026-10-10T11:00:00+05:30",
  "durationMinutes": 150,
  "stops": 0,
  "price": 8500,
  "currency": "INR"
}
```

## Optional GET `/api/flights/airports?query=Delhi`

Return airport/city suggestions for the form.

Response:
```json
{
  "airports": [
    {
      "code": "DEL",
      "name": "Indira Gandhi International Airport",
      "city": "Delhi",
      "country": "India"
    }
  ]
}
```

## Swagger

- `/swagger-ui.html`
- `/v3/api-docs`

## Important rule

Do not call Hotel Service or AI Planner Service from this service.
