package com.lekha.travel_planner.dto;

import com.lekha.travel_planner.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus status
) {}
