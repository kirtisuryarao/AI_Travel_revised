package com.lekha.travel_planner.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lekha.travel_planner.dto.DestinationPhotosResponse;
import com.lekha.travel_planner.dto.SaveItineraryRequest;
import com.lekha.travel_planner.dto.SavedItineraryResponse;
import com.lekha.travel_planner.service.DestinationImageService;
import com.lekha.travel_planner.service.SavedItineraryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ItineraryController {

    private final SavedItineraryService savedItineraryService;
    private final DestinationImageService destinationImageService;

    public ItineraryController(
            SavedItineraryService savedItineraryService,
            DestinationImageService destinationImageService) {
        this.savedItineraryService = savedItineraryService;
        this.destinationImageService = destinationImageService;
    }

    @GetMapping("/destinations/photos")
    public ResponseEntity<DestinationPhotosResponse> getDestinationPhotos(@RequestParam String destination) {
        return ResponseEntity.ok(destinationImageService.getPhotos(destination));
    }

    @PostMapping("/itineraries")
    public ResponseEntity<SavedItineraryResponse> saveItinerary(@Valid @RequestBody SaveItineraryRequest request) {
        return ResponseEntity.ok(savedItineraryService.save(request));
    }

    @GetMapping("/itineraries")
    public ResponseEntity<List<SavedItineraryResponse>> listSavedItineraries() {
        return ResponseEntity.ok(savedItineraryService.listForCurrentUser());
    }

    @GetMapping("/itineraries/{id}")
    public ResponseEntity<SavedItineraryResponse> getSavedItinerary(@PathVariable Long id) {
        return ResponseEntity.ok(savedItineraryService.getForCurrentUser(id));
    }

    @DeleteMapping("/itineraries/{id}")
    public ResponseEntity<Void> deleteSavedItinerary(@PathVariable Long id) {
        savedItineraryService.deleteForCurrentUser(id);
        return ResponseEntity.noContent().build();
    }
}
