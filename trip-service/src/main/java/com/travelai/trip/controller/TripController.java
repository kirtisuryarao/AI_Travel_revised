package com.travelai.trip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelai.trip.dto.TripDtos.MessageResponse;
import com.travelai.trip.dto.TripDtos.SaveTripRequest;
import com.travelai.trip.dto.TripDtos.SavedTripResponse;
import com.travelai.trip.dto.TripDtos.TripDetailResponse;
import com.travelai.trip.dto.TripDtos.TripSummaryResponse;
import com.travelai.trip.service.TripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<SavedTripResponse> save(@Valid @RequestBody SaveTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.save(request));
    }

    @GetMapping
    public ResponseEntity<List<TripSummaryResponse>> list() {
        return ResponseEntity.ok(tripService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDetailResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.delete(id));
    }
}
