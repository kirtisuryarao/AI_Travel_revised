# API Gateway

Single backend entry point for the Next.js frontend.

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

Port: `8080`

Start Eureka first, then:

```bash
mvn spring-boot:run
```

## Gateway routes

| Method | Path | Target | Auth | Purpose |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/register` | USER-SERVICE | No | Register |
| POST | `/api/auth/login` | USER-SERVICE | No | Login |
| GET | `/api/auth/me` | USER-SERVICE | Bearer JWT | Current user |
| GET/PUT | `/api/users/me` | USER-SERVICE | Bearer JWT | Profile |
| POST | `/api/flights/search` | FLIGHT-SERVICE | No | Search flights |
| GET | `/api/flights/{flightId}` | FLIGHT-SERVICE | No | Flight details |
| GET | `/api/flights/airports` | FLIGHT-SERVICE | No | Airport suggestions |
| POST | `/api/hotels/search` | HOTEL-SERVICE | No | Search hotels |
| GET | `/api/hotels/{hotelId}` | HOTEL-SERVICE | No | Hotel details |
| POST | `/api/ai/itinerary` | AI-PLANNER-SERVICE | No | Generate itinerary |
| GET | `/api/ai/health` | AI-PLANNER-SERVICE | No | AI health |
| POST | `/api/trips` | TRIP-SERVICE | Bearer JWT | Save trip |
| GET | `/api/trips` | TRIP-SERVICE | Bearer JWT | List trips |
| GET | `/api/trips/{id}` | TRIP-SERVICE | Bearer JWT | Get trip |
| DELETE | `/api/trips/{id}` | TRIP-SERVICE | Bearer JWT | Delete trip |

The `Authorization` header is forwarded unchanged. CORS allows `http://localhost:3000`.

Aggregated Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
