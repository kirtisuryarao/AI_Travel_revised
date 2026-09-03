package com.lekha.travel_planner.dto;

import java.util.Set;

public record AuthResponse(
    String token,
    String email,
    String fullName,
    Set<String> roles
) {}
