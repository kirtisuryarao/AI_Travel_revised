package com.lekha.travel_planner.dto;

import java.util.List;

public record DestinationPhotosResponse(
    String destination,
    String hero,
    List<String> gallery
) {}
