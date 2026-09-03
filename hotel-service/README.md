# Hotel Service

Search hotels filtered by destination, dates, travelers, and maximum hotel budget per night.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8083`  
Database: `data/hoteldb.mv.db`

```bash
mvn spring-boot:run
```

Swagger: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

This service does not call Flight Service.

## Endpoints

### POST `/api/hotels/search`
Auth: none  
Request:
```json
{
  "destination": "GOI",
  "checkIn": "2026-10-10",
  "checkOut": "2026-10-15",
  "travelers": 2,
  "rooms": 1,
  "maxHotelBudgetPerNight": 5000,
  "currency": "INR"
}
```
Response `200`: `{ "searchId", "destination", "checkIn", "checkOut", "hotels": [ { "hotelId", "name", "rating", "location", "roomType", "amenities", "pricePerNight", "totalPrice", "currency", "imageUrl" } ] }`  
Hotels with `pricePerNight` above `maxHotelBudgetPerNight` are excluded.  
Status: `200`, `400`

### GET `/api/hotels/{hotelId}`
Auth: none  
Response: hotel details  
Status: `200`, `404`

### GET `/api/hotels/destinations?query=Goa`
Auth: none  
Response: `{ "destinations": [ { "code", "city" } ] }`  
Status: `200`
