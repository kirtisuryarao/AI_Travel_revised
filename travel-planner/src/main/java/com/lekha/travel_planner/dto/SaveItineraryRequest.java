package com.lekha.travel_planner.dto;

import jakarta.validation.constraints.NotBlank;

public record SaveItineraryRequest(
    @NotBlank String destination,
    String duration,
    String budget,
    String interests,
    String weather,
    @NotBlank String itinerary,
    String heroImageUrl
) {}
