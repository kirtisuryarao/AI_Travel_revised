package com.lekha.travel_planner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lekha.travel_planner.entity.SavedItinerary;

public interface SavedItineraryRepository extends JpaRepository<SavedItinerary, Long> {
    List<SavedItinerary> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<SavedItinerary> findByIdAndUserId(Long id, Long userId);
}
