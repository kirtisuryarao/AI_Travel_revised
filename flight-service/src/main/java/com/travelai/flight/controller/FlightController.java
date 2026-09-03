package com.travelai.flight.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelai.flight.dto.FlightDtos.AirportListResponse;
import com.travelai.flight.dto.FlightDtos.FlightDetailResponse;
import com.travelai.flight.dto.FlightDtos.FlightSearchRequest;
import com.travelai.flight.dto.FlightDtos.FlightSearchResponse;
import com.travelai.flight.service.FlightSearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<FlightSearchResponse> search(@Valid @RequestBody FlightSearchRequest request) {
        return ResponseEntity.ok(flightSearchService.search(request));
    }

    @GetMapping("/airports")
    public ResponseEntity<AirportListResponse> airports(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(flightSearchService.airports(query));
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDetailResponse> getFlight(@PathVariable String flightId) {
        return ResponseEntity.ok(flightSearchService.getFlight(flightId));
    }
}
