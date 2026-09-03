package com.lekha.travel_planner.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Fetches current weather information from the wttr.in API.
 * This is a free, keyless weather service.
 */
@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Retrieves a short weather summary for the given destination.
     *
     * @param destination the city or location name
     * @return a one-line weather summary, or a fallback message on failure
     */
    public String getWeather(String destination) {
        try {
            String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8);
            String url = "https://wttr.in/" + encodedDestination + "?format=3";

            logger.info("Fetching weather for destination: {}", destination);
            String weather = restTemplate.getForObject(url, String.class);

            if (weather == null || weather.isBlank()) {
                logger.warn("Empty weather response for destination: {}", destination);
                return "Weather data currently unavailable";
            }

            logger.info("Weather retrieved successfully for: {}", destination);
            return weather.trim();
        } catch (Exception e) {
            logger.error("Failed to fetch weather for destination: {} — {}", destination, e.getMessage());
            return "Weather data currently unavailable";
        }
    }
}
