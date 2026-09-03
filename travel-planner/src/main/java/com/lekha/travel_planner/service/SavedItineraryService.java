package com.lekha.travel_planner.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lekha.travel_planner.dto.SaveItineraryRequest;
import com.lekha.travel_planner.dto.SavedItineraryResponse;
import com.lekha.travel_planner.entity.SavedItinerary;
import com.lekha.travel_planner.entity.User;
import com.lekha.travel_planner.repository.SavedItineraryRepository;

@Service
public class SavedItineraryService {

    private final SavedItineraryRepository savedItineraryRepository;
    private final AuthService authService;

    public SavedItineraryService(SavedItineraryRepository savedItineraryRepository, AuthService authService) {
        this.savedItineraryRepository = savedItineraryRepository;
        this.authService = authService;
    }

    @Transactional
    public SavedItineraryResponse save(SaveItineraryRequest request) {
        User user = authService.getCurrentUserEntity();

        SavedItinerary saved = new SavedItinerary();
        saved.setUser(user);
        saved.setDestination(request.destination().trim());
        saved.setDuration(request.duration());
        saved.setBudget(request.budget());
        saved.setInterests(request.interests());
        saved.setWeather(request.weather());
        saved.setItineraryText(request.itinerary());
        saved.setHeroImageUrl(request.heroImageUrl());

        return toResponse(savedItineraryRepository.save(saved));
    }

    @Transactional(readOnly = true)
    public List<SavedItineraryResponse> listForCurrentUser() {
        User user = authService.getCurrentUserEntity();
        return savedItineraryRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SavedItineraryResponse getForCurrentUser(Long id) {
        User user = authService.getCurrentUserEntity();
        SavedItinerary saved = savedItineraryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saved itinerary not found"));
        return toResponse(saved);
    }

    @Transactional
    public void deleteForCurrentUser(Long id) {
        User user = authService.getCurrentUserEntity();
        SavedItinerary saved = savedItineraryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saved itinerary not found"));
        savedItineraryRepository.delete(saved);
    }

    private SavedItineraryResponse toResponse(SavedItinerary saved) {
        return new SavedItineraryResponse(
            saved.getId(),
            saved.getDestination(),
            saved.getDuration(),
            saved.getBudget(),
            saved.getInterests(),
            saved.getWeather(),
            saved.getItineraryText(),
            saved.getHeroImageUrl(),
            saved.getCreatedAt()
        );
    }
}
