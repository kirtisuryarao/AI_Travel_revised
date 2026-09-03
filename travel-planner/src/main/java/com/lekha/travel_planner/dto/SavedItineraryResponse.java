package com.lekha.travel_planner.dto;

import java.time.Instant;

public record SavedItineraryResponse(
    Long id,
    String destination,
    String duration,
    String budget,
    String interests,
    String weather,
    String itinerary,
    String heroImageUrl,
    Instant savedAt
) {}
