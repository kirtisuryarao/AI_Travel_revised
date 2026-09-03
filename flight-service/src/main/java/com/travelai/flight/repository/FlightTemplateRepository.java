package com.travelai.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.flight.entity.FlightTemplate;

public interface FlightTemplateRepository extends JpaRepository<FlightTemplate, Long> {
    List<FlightTemplate> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
    Optional<FlightTemplate> findByFlightId(String flightId);
}
