# Trip Service — Requirements

## Purpose

Owns saved/final trips. It stores the final trip plan and snapshots of the selected flight and hotel so a saved trip remains viewable even if external provider data changes.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA/Hibernate
- Spring Security resource validation/JWT handling
- H2 file database
- Springdoc OpenAPI
- Eureka Client
- Port: `8085`

## Database

Database file: `data/tripdb.mv.db`.

Trip Service must not use User Service's database. Store the authenticated `userId` from the JWT and trip-owned data.

## POST `/api/trips`

Save a completed trip. Requires JWT.

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
  "interests": ["Food", "Nature"],
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
  "itinerary": [],
  "budgetEstimate": {
    "total": 42500,
    "currency": "INR"
  }
}
```

Response `201`:
```json
{
  "id": 1,
  "userId": 7,
  "destination": "GOI",
  "startDate": "2026-10-10",
  "endDate": "2026-10-15",
  "travelers": 2,
  "maxTotalBudget": 50000,
  "maxHotelBudgetPerNight": 5000,
  "status": "SAVED"
}
```

## GET `/api/trips`

Return trips belonging to the authenticated user. Requires JWT.

Response:
```json
[
  {
    "id": 1,
    "destination": "GOI",
    "startDate": "2026-10-10",
    "endDate": "2026-10-15",
    "travelers": 2,
    "status": "SAVED"
  }
]
```

## GET `/api/trips/{id}`

Return one saved trip belonging to the authenticated user.

Response:
```json
{
  "id": 1,
  "userId": 7,
  "origin": "DEL",
  "destination": "GOI",
  "startDate": "2026-10-10",
  "endDate": "2026-10-15",
  "travelers": 2,
  "maxTotalBudget": 50000,
  "maxHotelBudgetPerNight": 5000,
  "interests": ["Food", "Nature"],
  "selectedFlight": {},
  "selectedHotel": {},
  "itinerary": [],
  "budgetEstimate": {}
}
```

## DELETE `/api/trips/{id}`

Delete a saved trip belonging to the authenticated user. Requires JWT.

Response:
```json
{
  "message": "Trip deleted successfully"
}
```

## Swagger

- `/swagger-ui.html`
- `/v3/api-docs`

## Security

Users must only be able to read/delete their own trips. Do not trust a user ID supplied in the request body; derive it from the authenticated JWT.
