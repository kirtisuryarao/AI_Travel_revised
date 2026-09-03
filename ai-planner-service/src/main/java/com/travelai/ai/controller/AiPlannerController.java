package com.travelai.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelai.ai.dto.AiDtos.HealthResponse;
import com.travelai.ai.dto.AiDtos.ItineraryRequest;
import com.travelai.ai.dto.AiDtos.ItineraryResponse;
import com.travelai.ai.service.GroqService;
import com.travelai.ai.service.ItineraryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiPlannerController {

    private final ItineraryService itineraryService;
    private final GroqService groqService;

    public AiPlannerController(ItineraryService itineraryService, GroqService groqService) {
        this.itineraryService = itineraryService;
        this.groqService = groqService;
    }

    @PostMapping("/itinerary")
    public ResponseEntity<ItineraryResponse> itinerary(@Valid @RequestBody ItineraryRequest request) {
        return ResponseEntity.ok(itineraryService.generate(request));
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        boolean ready = groqService.isConfigured();
        return ResponseEntity.ok(new HealthResponse(
            ready ? "UP" : "DOWN",
            Map.of("groq", ready ? "configured" : "missing-api-key")
        ));
    }
}
