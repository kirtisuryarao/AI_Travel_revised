package com.travelai.ai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;

@Service
public class GroqService {

    private static final Logger logger = LoggerFactory.getLogger(GroqService.class);

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void validateApiKey() {
        if (!isConfigured()) {
            logger.warn("GROQ_API_KEY is not configured. AI itinerary generation will return 503 until a key is set.");
        } else {
            logger.info("Groq API key configured ({}...)", apiKey.substring(0, Math.min(8, apiKey.length())));
        }
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("gsk_your");
    }

    @SuppressWarnings("unchecked")
    public String generateJson(String prompt) {
        if (!isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq API key is not configured");
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("temperature", 0.4);
            body.put("max_tokens", 3500);
            body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert AI travel planner. Return valid JSON only."),
                Map.of("role", "user", "content", prompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, new HttpEntity<>(body, headers), Map.class);
            Map<String, Object> payload = response.getBody();
            if (payload == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq API returned an empty response");
            }
            if (payload.containsKey("error")) {
                logger.error("Groq error: {}", payload.get("error"));
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq API error");
            }
            List<Map<String, Object>> choices = (List<Map<String, Object>>) payload.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq API returned no choices");
            }
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = message == null ? null : (String) message.get("content");
            if (content == null || content.isBlank()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq returned empty itinerary content");
            }
            return content;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Groq request failed", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Groq service is temporarily unavailable");
        }
    }
}
