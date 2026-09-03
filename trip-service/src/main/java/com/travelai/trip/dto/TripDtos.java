package com.travelai.trip.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TripDtos {

    public record SaveTripRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @Min(1) int travelers,
        BigDecimal maxTotalBudget,
        BigDecimal maxHotelBudgetPerNight,
        List<String> interests,
        Map<String, Object> selectedFlight,
        Map<String, Object> selectedHotel,
        Object itinerary,
        Map<String, Object> budgetEstimate
    ) {}

    public record SavedTripResponse(
        Long id,
        Long userId,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        int travelers,
        BigDecimal maxTotalBudget,
        BigDecimal maxHotelBudgetPerNight,
        String status
    ) {}

    public record TripSummaryResponse(
        Long id,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        int travelers,
        String status
    ) {}

    public record TripDetailResponse(
        Long id,
        Long userId,
        String origin,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        int travelers,
        BigDecimal maxTotalBudget,
        BigDecimal maxHotelBudgetPerNight,
        List<String> interests,
        Object selectedFlight,
        Object selectedHotel,
        Object itinerary,
        Object budgetEstimate,
        String status
    ) {}

    public record MessageResponse(String message) {}
}
