package com.lekha.travel_planner.dto;

import java.util.Set;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    Set<String> roles
) {}
