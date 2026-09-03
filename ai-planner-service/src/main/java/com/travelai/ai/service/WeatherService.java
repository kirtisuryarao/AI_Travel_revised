package com.travelai.ai.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public String getWeather(String destination) {
        try {
            String encoded = URLEncoder.encode(destination, StandardCharsets.UTF_8);
            String weather = restTemplate.getForObject("https://wttr.in/" + encoded + "?format=3", String.class);
            if (weather == null || weather.isBlank()) {
                return "Weather data currently unavailable";
            }
            return weather.trim();
        } catch (Exception e) {
            logger.warn("Weather lookup failed for {}: {}", destination, e.getMessage());
            return "Weather data currently unavailable";
        }
    }
}
