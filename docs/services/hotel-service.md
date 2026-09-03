# Hotel Service — Requirements

## Purpose

Search hotels for the selected destination and dates. The service must support the **maximum hotel budget per night** collected in the first form.

## Runtime

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA/Hibernate if persistence is needed
- H2 file database if persistence is used
- Springdoc OpenAPI
- Eureka Client
- Port: `8083`

## Main flow

```text
Frontend -> API Gateway -> Hotel Service -> External Hotel API
```

The Hotel Service does not call Flight Service.

## POST `/api/hotels/search`

Search/filter hotels.

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

Response:
```json
{
  "searchId": "HT-20001",
  "destination": "GOI",
  "checkIn": "2026-10-10",
  "checkOut": "2026-10-15",
  "hotels": [
    {
      "hotelId": "H-101",
      "name": "Example Beach Hotel",
      "rating": 4.3,
      "location": "Calangute, Goa",
      "roomType": "Deluxe Room",
      "amenities": ["WiFi", "Pool", "Breakfast"],
      "pricePerNight": 4200,
      "totalPrice": 21000,
      "currency": "INR",
      "imageUrl": "https://example.com/hotel.jpg"
    }
  ]
}
```

## GET `/api/hotels/{hotelId}`

Return details for a hotel.

Response:
```json
{
  "hotelId": "H-101",
  "name": "Example Beach Hotel",
  "rating": 4.3,
  "location": "Calangute, Goa",
  "roomType": "Deluxe Room",
  "amenities": ["WiFi", "Pool", "Breakfast"],
  "pricePerNight": 4200,
  "currency": "INR"
}
```

## GET `/api/hotels/destinations?query=Goa`

Optional destination suggestions for hotel search.

## Budget rule

`pricePerNight <= maxHotelBudgetPerNight` should be the default filter. If the external provider cannot guarantee this, filter the normalized results before returning them to the frontend.

## Swagger

- `/swagger-ui.html`
- `/v3/api-docs`
