# Trip Service

Stores completed trips with flight and hotel snapshots. User IDs come from the JWT, never from the request body.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8085`  
Database: `data/tripdb.mv.db`

```bash
mvn spring-boot:run
```

Swagger: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

Use the same `JWT_SECRET` as User Service.

## Endpoints

### POST `/api/trips`
Auth: Bearer JWT  
Request: origin, destination, dates, travelers, budgets, interests, selectedFlight, selectedHotel, itinerary, budgetEstimate  
Response `201`: `{ "id", "userId", "destination", "startDate", "endDate", "travelers", "maxTotalBudget", "maxHotelBudgetPerNight", "status": "SAVED" }`  
Status: `201`, `400`, `401`

### GET `/api/trips`
Auth: Bearer JWT  
Response: array of trip summaries for the authenticated user  
Status: `200`, `401`

### GET `/api/trips/{id}`
Auth: Bearer JWT  
Response: full trip including snapshots  
Status: `200`, `401`, `404`

### DELETE `/api/trips/{id}`
Auth: Bearer JWT  
Response: `{ "message": "Trip deleted successfully" }`  
Status: `200`, `401`, `404`
