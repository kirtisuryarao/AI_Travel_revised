package com.travelai.trip.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelai.trip.dto.TripDtos.MessageResponse;
import com.travelai.trip.dto.TripDtos.SaveTripRequest;
import com.travelai.trip.dto.TripDtos.SavedTripResponse;
import com.travelai.trip.dto.TripDtos.TripDetailResponse;
import com.travelai.trip.dto.TripDtos.TripSummaryResponse;
import com.travelai.trip.entity.Trip;
import com.travelai.trip.repository.TripRepository;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final ObjectMapper objectMapper;

    public TripService(TripRepository tripRepository, ObjectMapper objectMapper) {
        this.tripRepository = tripRepository;
        this.objectMapper = objectMapper;
    }

    public SavedTripResponse save(SaveTripRequest request) {
        Long userId = currentUserId();
        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setOrigin(request.origin().trim());
        trip.setDestination(request.destination().trim());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setTravelers(request.travelers());
        trip.setMaxTotalBudget(request.maxTotalBudget());
        trip.setMaxHotelBudgetPerNight(request.maxHotelBudgetPerNight());
        trip.setStatus("SAVED");
        trip.setInterestsJson(writeJson(request.interests() == null ? List.of() : request.interests()));
        trip.setSelectedFlightJson(writeJson(request.selectedFlight()));
        trip.setSelectedHotelJson(writeJson(request.selectedHotel()));
        trip.setItineraryJson(writeJson(request.itinerary()));
        trip.setBudgetEstimateJson(writeJson(request.budgetEstimate()));
        tripRepository.save(trip);
        return toSaved(trip);
    }

    public List<TripSummaryResponse> list() {
        return tripRepository.findByUserIdOrderByIdDesc(currentUserId()).stream()
            .map(trip -> new TripSummaryResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getTravelers(),
                trip.getStatus()
            ))
            .toList();
    }

    public TripDetailResponse get(Long id) {
        Trip trip = tripRepository.findByIdAndUserId(id, currentUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
        return new TripDetailResponse(
            trip.getId(),
            trip.getUserId(),
            trip.getOrigin(),
            trip.getDestination(),
            trip.getStartDate(),
            trip.getEndDate(),
            trip.getTravelers(),
            trip.getMaxTotalBudget(),
            trip.getMaxHotelBudgetPerNight(),
            readList(trip.getInterestsJson()),
            readObject(trip.getSelectedFlightJson()),
            readObject(trip.getSelectedHotelJson()),
            readObject(trip.getItineraryJson()),
            readObject(trip.getBudgetEstimateJson()),
            trip.getStatus()
        );
    }

    public MessageResponse delete(Long id) {
        Trip trip = tripRepository.findByIdAndUserId(id, currentUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
        tripRepository.delete(trip);
        return new MessageResponse("Trip deleted successfully");
    }

    private SavedTripResponse toSaved(Trip trip) {
        return new SavedTripResponse(
            trip.getId(),
            trip.getUserId(),
            trip.getDestination(),
            trip.getStartDate(),
            trip.getEndDate(),
            trip.getTravelers(),
            trip.getMaxTotalBudget(),
            trip.getMaxHotelBudgetPerNight(),
            trip.getStatus()
        );
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? List.of() : value);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to store trip snapshot");
        }
    }

    private Object readObject(String json) {
        try {
            if (json == null || json.isBlank()) {
                return List.of();
            }
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }

    private List<String> readList(String json) {
        try {
            if (json == null || json.isBlank()) {
                return List.of();
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
