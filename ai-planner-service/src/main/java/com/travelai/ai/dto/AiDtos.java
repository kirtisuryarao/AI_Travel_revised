package com.travelai.ai.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AiDtos {

    public record SelectedFlight(
        String flightId,
        String airline,
        String flightNumber,
        BigDecimal price,
        String currency
    ) {}

    public record SelectedHotel(
        String hotelId,
        String name,
        BigDecimal pricePerNight,
        String currency
    ) {}

    public record ItineraryRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @Min(1) int travelers,
        BigDecimal maxTotalBudget,
        BigDecimal maxHotelBudgetPerNight,
        List<String> interests,
        @NotNull @Valid SelectedFlight selectedFlight,
        @NotNull @Valid SelectedHotel selectedHotel
    ) {}

    public record TripSummary(
        String origin,
        String destination,
        int travelers,
        LocalDate startDate,
        LocalDate endDate
    ) {}

    public record Activity(String time, String title, String description) {}

    public record DayPlan(int day, LocalDate date, List<Activity> activities) {}

    public record BudgetEstimate(
        BigDecimal flight,
        BigDecimal hotel,
        BigDecimal food,
        BigDecimal activities,
        BigDecimal localTransport,
        BigDecimal total,
        String currency
    ) {}

    public record ItineraryResponse(
        TripSummary tripSummary,
        SelectedFlight selectedFlight,
        SelectedHotel selectedHotel,
        String weather,
        List<DayPlan> itinerary,
        BudgetEstimate budgetEstimate,
        List<String> packingSuggestions,
        List<String> travelTips
    ) {}

    public record HealthResponse(String status, Map<String, String> details) {}
}
