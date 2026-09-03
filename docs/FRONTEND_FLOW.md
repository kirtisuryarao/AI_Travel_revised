# Frontend Flow — TravelAI UI

Use the uploaded TravelAI UI as the visual source of truth. The current frontend can be migrated to Next.js while retaining the same design language.

## Step 1: Start Your Adventure

Keep the existing form style and fields, adding:

`Maximum Hotel Budget Per Night (₹)`

Recommended form payload:

```json
{
  "origin": "DEL",
  "destination": "GOI",
  "startDate": "2026-10-10",
  "endDate": "2026-10-15",
  "travelers": 2,
  "maxTotalBudget": 50000,
  "maxHotelBudgetPerNight": 5000,
  "interests": ["History", "Food", "Nature"]
}
```

## Step 2: Flight Options

After submit, call:

`POST /api/flights/search`

through the Gateway.

Show a flight-results screen with cards and a `Select Flight` action. Do not call the AI endpoint yet.

## Step 3: Hotel Options

After selecting a flight, call:

`POST /api/hotels/search`

using the original trip dates/destination and `maxHotelBudgetPerNight`.

Show hotel cards and a `Select Hotel` action.

## Step 4: AI Itinerary

After hotel selection, call:

`POST /api/ai/itinerary`

using the original form data plus selected flight and hotel snapshots.

Show the existing premium loading animation while the AI request is running.

## Step 5: Save

After the itinerary is displayed, call:

`POST /api/trips`

when the user clicks `Save Trip`.

## Frontend networking rule

All requests use one base URL:

```text
http://localhost:8080
```

Never call `8081`, `8082`, `8083`, `8084`, or `8085` directly from Next.js.
