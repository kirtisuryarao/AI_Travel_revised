# Flight Service

Search and return normalized flight options.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8082`  
Database: `data/flightdb.mv.db`

```bash
mvn spring-boot:run
```

Swagger: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

This service does not call Hotel Service or AI Planner Service.

## Endpoints

### POST `/api/flights/search`
Auth: none  
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
Response `200`: `{ "searchId": "FL-10001", "origin": "DEL", "destination": "GOI", "flights": [ { "flightId", "airline", "flightNumber", "departure", "arrival", "durationMinutes", "stops", "price", "currency" } ] }`  
Status: `200`, `400`

### GET `/api/flights/{flightId}`
Auth: none  
Response: flight details  
Status: `200`, `404`

### GET `/api/flights/airports?query=Delhi`
Auth: none  
Response: `{ "airports": [ { "code", "name", "city", "country" } ] }`  
Status: `200`
