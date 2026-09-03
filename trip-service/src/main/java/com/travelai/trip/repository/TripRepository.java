package com.travelai.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.trip.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserIdOrderByIdDesc(Long userId);
    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
