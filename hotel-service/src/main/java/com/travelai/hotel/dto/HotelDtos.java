package com.travelai.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HotelDtos {

    public record HotelSearchRequest(
        @NotBlank String destination,
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut,
        @Min(1) int travelers,
        Integer rooms,
        @NotNull @DecimalMin(value = "0.01", message = "Maximum hotel budget per night must be greater than 0")
        BigDecimal maxHotelBudgetPerNight,
        String currency
    ) {}

    public record HotelOption(
        String hotelId,
        String name,
        double rating,
        String location,
        String roomType,
        List<String> amenities,
        BigDecimal pricePerNight,
        BigDecimal totalPrice,
        String currency,
        String imageUrl
    ) {}

    public record HotelSearchResponse(
        String searchId,
        String destination,
        LocalDate checkIn,
        LocalDate checkOut,
        List<HotelOption> hotels
    ) {}

    public record HotelDetailResponse(
        String hotelId,
        String name,
        double rating,
        String location,
        String roomType,
        List<String> amenities,
        BigDecimal pricePerNight,
        String currency
    ) {}

    public record DestinationSuggestion(String code, String city) {}

    public record DestinationListResponse(List<DestinationSuggestion> destinations) {}
}
