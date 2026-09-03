package com.travelai.flight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.flight.entity.FlightSearch;

public interface FlightSearchRepository extends JpaRepository<FlightSearch, Long> {
}
