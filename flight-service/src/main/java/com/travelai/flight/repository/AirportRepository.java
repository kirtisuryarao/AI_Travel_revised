package com.travelai.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.flight.entity.Airport;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCodeIgnoreCase(String code);
    List<Airport> findByCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrNameContainingIgnoreCase(
        String code, String city, String name);
}
