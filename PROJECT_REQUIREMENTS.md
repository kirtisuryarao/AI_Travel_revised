# AI Travel Planner — Microservices Project Requirements

## 1. Project Goal

Transform the existing AI Travel Planner application into a Spring Boot microservices application while preserving and improving the uploaded TravelAI UI. The application must provide this user journey:

1. User opens the existing TravelAI-style landing/planner page.
2. User fills the trip form.
3. The form includes origin, destination, travel dates, number of travelers, maximum total trip budget, **maximum hotel budget per night**, and interests.
4. After form submission, show available **flight options first**.
5. User selects a flight.
6. Then show available **hotel options**, filtered by destination, dates, travelers, and maximum hotel budget per night.
7. User selects a hotel.
8. Only after flight + hotel selection, call the AI Planner Service to generate a personalized itinerary using the selected travel details and interests.
9. User can save the final trip and view saved trips.

The system must use Eureka Service Discovery and Spring Cloud API Gateway. The frontend must communicate through the API Gateway rather than calling individual backend services directly.

## 2. Existing Project Baseline

The uploaded project is currently a single Spring Boot application using Java 21, Spring Boot 3.3.0, Spring Data JPA, Spring Security/JWT, H2, and a Groq-based AI integration. The current frontend is HTML/CSS/JavaScript and already contains the TravelAI visual language, planner form, loading UI, itinerary page, authentication pages, and saved-trip functionality.

The new architecture should reuse the existing UI/design wherever practical, but the backend must be split into independent services.

## 3. Required Architecture

```text
Next.js Frontend
      |
      v
API Gateway :8080
      |
      +-------------------+------------------+------------------+------------------+
      |                   |                  |                  |
      v                   v                  v                  v
 User Service        Flight Service     Hotel Service      AI Planner Service
   :8081                :8082              :8083               :8084
      |
   H2 DB

Trip Service :8085
      |
   H2 DB

Eureka Server :8761
```

### Services

1. `eureka-server` — service discovery.
2. `api-gateway` — single entry point for frontend/API traffic.
3. `user-service` — registration, login, JWT, roles, current-user data.
4. `flight-service` — flight search and flight selection data.
5. `hotel-service` — hotel search and hotel selection data.
6. `ai-planner-service` — AI itinerary generation.
7. `trip-service` — save/view/delete completed trips.

Each service must be in a **separate folder** and must be independently buildable and runnable.

## 4. Service Communication Rule

Use communication only when it is actually required. Do not make every service call every other service.

### Frontend flow

```text
Frontend
  |
  +--> Gateway --> Flight Service --> External Flight API
  |
  +--> Gateway --> Hotel Service --> External Hotel API
  |
  +--> Gateway --> AI Planner Service --> Groq/LLM API
  |
  +--> Gateway --> Trip Service --> Trip DB
  |
  +--> Gateway --> User Service --> User DB
```

### Important

- Frontend calls only the API Gateway.
- Gateway uses Eureka service discovery and routes requests to services.
- Flight Service does not call Hotel Service.
- Hotel Service does not call Flight Service.
- User Service does not call Flight/Hotel/AI services.
- AI Planner Service is called only after the user has selected the required trip options.
- Trip Service should store the final trip and selected flight/hotel snapshots rather than sharing another service's database.
- Service-to-service calls, when genuinely required, should use OpenFeign/WebClient.
- Do not use shared database tables between services.

## 5. Technology Requirements

- Java **21** for every service.
- Spring Boot **3.3.0** for consistency with the uploaded project.
- Use a Spring Cloud release train compatible with Spring Boot 3.3.x; keep the versions centralized in Maven dependency management.
- Maven.
- Spring Data JPA/Hibernate for persistence.
- H2 file-based local database for each business service to keep local development simple.
- Spring Security + JWT in User Service.
- Spring Cloud Netflix Eureka Server/Client.
- Spring Cloud Gateway.
- Spring Cloud OpenFeign where service-to-service calls are required.
- Springdoc OpenAPI/Swagger for every business API service.
- Bean Validation (`jakarta.validation`).
- Lombok only if already used consistently; it is optional.
- REST/JSON APIs.

## 6. JDK Compatibility

All services must compile and run with JDK 21.

Do not mix Java 17/21/22 configurations across services. Every `pom.xml` must explicitly use Java 21. Maven compiler settings, Spring Boot version, and Spring Cloud version must be compatible with JDK 21.

The README for every service must state:

```text
Java: 21
Spring Boot: 3.3.0
Build: Maven
```

## 7. Local Database Requirement

Every business service that persists data must have its **own local H2 database**.

Example:

```text
user-service/data/userdb.mv.db
flight-service/data/flightdb.mv.db
hotel-service/data/hoteldb.mv.db
trip-service/data/tripdb.mv.db
```

No service may directly access another service's database.

Use Spring Data JPA/Hibernate. Configure H2 in file mode so data survives application restarts.

## 8. Eureka Requirement

Eureka Server must run on port `8761`.

All business services and the API Gateway must register with Eureka using logical service names:

```text
USER-SERVICE
FLIGHT-SERVICE
HOTEL-SERVICE
AI-PLANNER-SERVICE
TRIP-SERVICE
API-GATEWAY
```

The frontend must never depend on hard-coded service ports.

## 9. API Gateway Requirement

API Gateway runs on port `8080` and is the only backend URL used by the frontend.

Suggested routes:

```text
/api/auth/**       -> USER-SERVICE
/api/users/**      -> USER-SERVICE
/api/flights/**    -> FLIGHT-SERVICE
/api/hotels/**     -> HOTEL-SERVICE
/api/ai/**         -> AI-PLANNER-SERVICE
/api/trips/**      -> TRIP-SERVICE
```

Gateway should discover services through Eureka (`lb://SERVICE-NAME`).

## 10. UI Requirements

Use the uploaded TravelAI UI as the visual reference. Preserve the clean cream/off-white background, large heading, rounded form fields, pill-style interest chips, orange primary button, spacing, typography hierarchy, and premium loading/result-card feel.

The planner page must be converted into a multi-step flow without losing the existing visual identity.

### Step 1 — Trip Details

Fields:

- Flying From
- Flying To
- Travel Start Date
- Travel End Date
- Travelers
- Maximum Total Trip Budget (₹)
- **Maximum Hotel Budget Per Night (₹)**
- Interests
- Custom interest

Validation:

- Origin required.
- Destination required.
- Start date required.
- End date required and not before start date.
- Travelers must be at least 1.
- Total budget must be positive.
- Maximum hotel budget per night must be positive.
- At least one interest is recommended.

### Step 2 — Flight Options

Display flight cards with:

- Airline
- Flight number
- Departure airport/time
- Arrival airport/time
- Duration
- Stops
- Price
- Currency
- Select Flight button

The user must explicitly select one flight before continuing.

### Step 3 — Hotel Options

Display hotel cards with:

- Hotel name
- Rating
- Location
- Room type
- Amenities
- Price per night
- Total estimated stay price
- Image if available
- Select Hotel button

Only show/filter hotels according to the requested maximum hotel budget per night.

### Step 4 — AI Itinerary

After flight and hotel selection, generate the AI itinerary. Show:

- Trip summary
- Selected flight
- Selected hotel
- Weather information if available
- Day-by-day itinerary
- Estimated budget breakdown
- Activities based on interests
- Packing suggestions
- Useful travel notes
- Save Trip button
- PDF export if retained from the existing UI

## 11. Authentication

Login and signup must remain available.

JWT should be issued by User Service. Protected requests must send:

```http
Authorization: Bearer <JWT_TOKEN>
```

Gateway should forward the Authorization header to protected services.

## 12. Swagger/OpenAPI

Every business microservice must expose Swagger/OpenAPI documentation.

Required endpoints:

```text
/swagger-ui.html
/v3/api-docs
```

Each service README must document all application routes, including:

- HTTP method
- Endpoint path
- Purpose
- Authentication requirement
- Request body
- Response body
- Important status codes

Eureka itself is infrastructure and does not need a business Swagger API. Gateway should have route documentation or aggregated OpenAPI documentation if practical.

## 13. Error Handling

All services should return consistent JSON errors, for example:

```json
{
  "timestamp": "2026-09-03T18:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Maximum hotel budget per night must be greater than 0",
  "path": "/api/hotels/search"
}
```

Use `@RestControllerAdvice` in business services.

## 14. Project Structure

```text
ai-travel-planner/
├── frontend/                 # Next.js frontend using the existing UI design
├── eureka-server/
│   ├── pom.xml
│   ├── README.md
│   └── src/
├── api-gateway/
│   ├── pom.xml
│   ├── README.md
│   └── src/
├── user-service/
│   ├── pom.xml
│   ├── README.md
│   ├── data/
│   └── src/
├── flight-service/
│   ├── pom.xml
│   ├── README.md
│   ├── data/
│   └── src/
├── hotel-service/
│   ├── pom.xml
│   ├── README.md
│   ├── data/
│   └── src/
├── ai-planner-service/
│   ├── pom.xml
│   ├── README.md
│   └── src/
├── trip-service/
│   ├── pom.xml
│   ├── README.md
│   ├── data/
│   └── src/
└── PROJECT_REQUIREMENTS.md
```

## 15. Core End-to-End API Flow

### 15.1 Submit trip search

Frontend -> Gateway -> Flight Service:

```http
POST /api/flights/search
```

The response contains flight options.

### 15.2 Select flight

The selected flight ID/reference is retained by the frontend.

### 15.3 Search hotels

Frontend -> Gateway -> Hotel Service:

```http
POST /api/hotels/search
```

Request includes the selected flight-independent trip details and `maxHotelBudgetPerNight`.

### 15.4 Generate itinerary

Frontend -> Gateway -> AI Planner Service:

```http
POST /api/ai/itinerary
```

Request contains trip details, selected flight, selected hotel, and interests.

### 15.5 Save trip

Frontend -> Gateway -> Trip Service:

```http
POST /api/trips
```

The completed trip is stored in Trip Service's database.

## 16. Do Not Over-Engineer

For this phase, do not add Kafka, Config Server, distributed tracing, payment service, order service, product service, or a separate notification service unless specifically required later.

The goal is a clean, demonstrable microservices architecture with Eureka, API Gateway, JPA/Hibernate, local databases, REST communication where required, Swagger, authentication, flight/hotel search, and AI itinerary generation.

## 17. Definition of Done

- [ ] Existing UI style is retained and adapted to the new multi-step flow.
- [ ] Hotel max budget per night exists in the first form.
- [ ] Flight options appear after submitting the first form.
- [ ] Hotel options appear after selecting a flight.
- [ ] AI itinerary is generated only after flight + hotel selection.
- [ ] Eureka Server works on `8761`.
- [ ] API Gateway works on `8080`.
- [ ] Every business service has a separate folder and Maven project.
- [ ] Every business service has its own local H2 database where persistence is needed.
- [ ] No shared database between services.
- [ ] Services register with Eureka.
- [ ] Gateway routes through Eureka.
- [ ] Frontend calls only the Gateway.
- [ ] JWT authentication works.
- [ ] Swagger is available for each business service.
- [ ] Every service has a README documenting all endpoints, request bodies, response bodies, and status codes.
- [ ] All services build with JDK 21.
- [ ] Existing Groq AI integration is moved into AI Planner Service.
