package com.lekha.travel_planner.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lekha.travel_planner.service.GroqService;
import com.lekha.travel_planner.service.WeatherService;

/**
 * REST controller for the travel planning API.
 * Accepts trip parameters and returns an AI-generated itinerary with weather data.
 */
@RestController
@RequestMapping("/api")
public class TravelController {

    private static final Logger logger = LoggerFactory.getLogger(TravelController.class);

    @Autowired
    private GroqService groqService;

    @Autowired
    private WeatherService weatherService;

    /**
     * Generates a personalized travel itinerary.
     *
     * @param request JSON body with keys: destination, duration, budget, interests
     * @return JSON response with itinerary, weather, and destination
     */
    @PostMapping("/plan")
    public ResponseEntity<Map<String, Object>> generateTravelPlan(@RequestBody Map<String, String> request) {
        String destination = request.get("destination");
        String duration = request.get("duration");
        String budget = request.get("budget");
        String interests = request.get("interests");

        // Input validation
        if (destination == null || destination.isBlank()) {
            logger.warn("Request rejected: missing destination");
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Destination is required"
            ));
        }
        if (duration == null || duration.isBlank()) {
            logger.warn("Request rejected: missing duration");
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Duration is required"
            ));
        }

        logger.info("Generating travel plan — destination: {}, duration: {}, budget: {}",
                destination, duration, budget);

        String itinerary = groqService.generateItinerary(destination, duration, budget, interests);
        String weather = weatherService.getWeather(destination);

        Map<String, Object> response = new HashMap<>();
        response.put("itinerary", itinerary);
        response.put("weather", weather);
        response.put("destination", destination);

        logger.info("Travel plan generated successfully for: {}", destination);
        return ResponseEntity.ok(response);
    }
}
