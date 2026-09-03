package com.lekha.travel_planner.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.lekha.travel_planner.entity.OrderStatus;

public record OrderResponse(
    Long id,
    Long userId,
    String userEmail,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items,
    Instant createdAt,
    Instant updatedAt
) {}
