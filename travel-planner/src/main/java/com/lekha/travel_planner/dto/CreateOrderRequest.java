package com.lekha.travel_planner.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
    @NotEmpty @Valid List<OrderItemRequest> items
) {}
