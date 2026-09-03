package com.travelai.ai.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelai.ai.dto.AiDtos.Activity;
import com.travelai.ai.dto.AiDtos.BudgetEstimate;
import com.travelai.ai.dto.AiDtos.DayPlan;
import com.travelai.ai.dto.AiDtos.ItineraryRequest;
import com.travelai.ai.dto.AiDtos.ItineraryResponse;
import com.travelai.ai.dto.AiDtos.TripSummary;

@Service
public class ItineraryService {

    private final GroqService groqService;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper;

    public ItineraryService(GroqService groqService, WeatherService weatherService, ObjectMapper objectMapper) {
        this.groqService = groqService;
        this.weatherService = weatherService;
        this.objectMapper = objectMapper;
    }

    public ItineraryResponse generate(ItineraryRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
        if (request.selectedFlight() == null || request.selectedHotel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A selected flight and hotel are required");
        }

        String origin = sanitize(request.origin());
        String destination = sanitize(request.destination());
        List<String> interests = request.interests() == null ? List.of() : request.interests().stream()
            .map(this::sanitize)
            .filter(value -> !value.isBlank())
            .toList();

        long nights = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        String weather = weatherService.getWeather(destination);
        String prompt = buildPrompt(origin, destination, request, interests, nights, weather);
        String raw = groqService.generateJson(prompt);
        JsonNode json = parseJson(raw);

        List<DayPlan> days = parseDays(json.path("itinerary"), request.startDate());
        if (days.isEmpty()) {
            days = fallbackDays(request.startDate(), request.endDate(), destination);
        }

        BudgetEstimate budget = parseBudget(json.path("budgetEstimate"), request, nights);
        List<String> tips = stringList(json.path("travelTips"));
        List<String> packing = stringList(json.path("packingSuggestions"));
        if (packing.isEmpty()) {
            packing = List.of("Light cotton clothing", "Comfortable walking shoes", "Reusable water bottle", "Sunscreen and a hat");
        }
        if (tips.isEmpty()) {
            tips = List.of("Keep digital and printed copies of tickets.", "Confirm hotel check-in time the day before arrival.");
        }

        return new ItineraryResponse(
            new TripSummary(origin, destination, request.travelers(), request.startDate(), request.endDate()),
            request.selectedFlight(),
            request.selectedHotel(),
            weather,
            days,
            budget,
            packing,
            tips
        );
    }

    private String buildPrompt(
            String origin,
            String destination,
            ItineraryRequest request,
            List<String> interests,
            long nights,
            String weather) {
        String interestText = interests.isEmpty() ? "general sightseeing" : String.join(", ", interests);
        return """
            Create a realistic travel itinerary as JSON with this exact shape:
            {
              "itinerary": [{"day": 1, "date": "YYYY-MM-DD", "activities": [{"time": "09:00", "title": "...", "description": "..."}]}],
              "budgetEstimate": {"flight": 0, "hotel": 0, "food": 0, "activities": 0, "localTransport": 0, "total": 0, "currency": "INR"},
              "packingSuggestions": ["..."],
              "travelTips": ["..."]
            }
            Origin: %s
            Destination: %s
            Dates: %s to %s (%s nights)
            Travelers: %s
            Total budget: %s
            Hotel budget per night: %s
            Interests: %s
            Selected flight: %s %s priced %s
            Selected hotel: %s priced %s per night
            Weather: %s
            Return JSON only. Use the provided flight and hotel prices in the budget. Do not invent different flight or hotel names.
            """.formatted(
                origin,
                destination,
                request.startDate(),
                request.endDate(),
                nights,
                request.travelers(),
                request.maxTotalBudget(),
                request.maxHotelBudgetPerNight(),
                interestText,
                request.selectedFlight().airline(),
                request.selectedFlight().flightNumber(),
                request.selectedFlight().price(),
                request.selectedHotel().name(),
                request.selectedHotel().pricePerNight(),
                weather
            );
    }

    private JsonNode parseJson(String raw) {
        try {
            String content = raw.trim();
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');
            if (start >= 0 && end > start) {
                content = content.substring(start, end + 1);
            }
            return objectMapper.readTree(content);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI provider returned unreadable itinerary JSON");
        }
    }

    private List<DayPlan> parseDays(JsonNode node, LocalDate startDate) {
        List<DayPlan> days = new ArrayList<>();
        if (!node.isArray()) {
            return days;
        }
        int index = 1;
        for (JsonNode dayNode : node) {
            LocalDate date = startDate.plusDays(index - 1L);
            if (dayNode.hasNonNull("date")) {
                try {
                    date = LocalDate.parse(dayNode.get("date").asText());
                } catch (Exception ignored) {
                    // keep calculated date
                }
            }
            List<Activity> activities = new ArrayList<>();
            for (JsonNode activity : dayNode.path("activities")) {
                activities.add(new Activity(
                    text(activity, "time", "09:00"),
                    text(activity, "title", "Explore"),
                    text(activity, "description", "")
                ));
            }
            days.add(new DayPlan(dayNode.path("day").asInt(index), date, activities));
            index++;
        }
        return days;
    }

    private List<DayPlan> fallbackDays(LocalDate start, LocalDate end, String destination) {
        List<DayPlan> days = new ArrayList<>();
        long count = ChronoUnit.DAYS.between(start, end) + 1;
        for (int i = 0; i < count; i++) {
            days.add(new DayPlan(i + 1, start.plusDays(i), List.of(
                new Activity("09:00", "Morning in " + destination, "Start the day with a local breakfast and a neighbourhood walk."),
                new Activity("14:00", "Afternoon highlights", "Visit a signature attraction and leave time to rest."),
                new Activity("19:00", "Evening", "Try a well-reviewed local restaurant.")
            )));
        }
        return days;
    }

    private BudgetEstimate parseBudget(JsonNode node, ItineraryRequest request, long nights) {
        BigDecimal flight = decimal(node, "flight", request.selectedFlight().price());
        BigDecimal hotelNight = request.selectedHotel().pricePerNight() == null ? BigDecimal.ZERO : request.selectedHotel().pricePerNight();
        BigDecimal hotel = decimal(node, "hotel", hotelNight.multiply(BigDecimal.valueOf(Math.max(nights, 1))));
        BigDecimal food = decimal(node, "food", BigDecimal.valueOf(1500L * request.travelers() * Math.max(nights, 1)));
        BigDecimal activities = decimal(node, "activities", BigDecimal.valueOf(1000L * request.travelers() * Math.max(nights, 1)));
        BigDecimal transport = decimal(node, "localTransport", BigDecimal.valueOf(500L * request.travelers() * Math.max(nights, 1)));
        BigDecimal total = flight.add(hotel).add(food).add(activities).add(transport);
        String currency = node.path("currency").asText("INR");
        return new BudgetEstimate(flight, hotel, food, activities, transport, total, currency);
    }

    private List<String> stringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> values.add(item.asText()));
        }
        return values.stream().map(this::sanitize).filter(value -> !value.isBlank()).toList();
    }

    private BigDecimal decimal(JsonNode node, String field, BigDecimal fallback) {
        if (node.hasNonNull(field) && node.get(field).isNumber()) {
            return node.get(field).decimalValue();
        }
        return fallback == null ? BigDecimal.ZERO : fallback;
    }

    private String text(JsonNode node, String field, String fallback) {
        String value = node.path(field).asText(fallback);
        return sanitize(value);
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "").trim();
    }
}
