package com.lekha.travel_planner.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    Integer stockQuantity,
    boolean active,
    String imageUrl,
    Instant createdAt
) {}
