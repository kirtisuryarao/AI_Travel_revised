package com.travelai.flight.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlightDtos {

    public record FlightSearchRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDate departureDate,
        LocalDate returnDate,
        @Min(1) int travelers,
        BigDecimal maxTotalBudget,
        String currency
    ) {}

    public record AirportTime(String airport, String time) {}

    public record FlightOption(
        String flightId,
        String airline,
        String flightNumber,
        AirportTime departure,
        AirportTime arrival,
        int durationMinutes,
        int stops,
        BigDecimal price,
        String currency
    ) {}

    public record FlightSearchResponse(
        String searchId,
        String origin,
        String destination,
        List<FlightOption> flights
    ) {}

    public record FlightDetailResponse(
        String flightId,
        String airline,
        String flightNumber,
        String origin,
        String destination,
        String departureTime,
        String arrivalTime,
        int durationMinutes,
        int stops,
        BigDecimal price,
        String currency
    ) {}

    public record AirportResponse(String code, String name, String city, String country) {}

    public record AirportListResponse(List<AirportResponse> airports) {}
}
