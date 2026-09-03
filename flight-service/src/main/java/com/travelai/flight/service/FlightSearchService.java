package com.travelai.flight.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelai.flight.dto.FlightDtos.AirportListResponse;
import com.travelai.flight.dto.FlightDtos.AirportResponse;
import com.travelai.flight.dto.FlightDtos.AirportTime;
import com.travelai.flight.dto.FlightDtos.FlightDetailResponse;
import com.travelai.flight.dto.FlightDtos.FlightOption;
import com.travelai.flight.dto.FlightDtos.FlightSearchRequest;
import com.travelai.flight.dto.FlightDtos.FlightSearchResponse;
import com.travelai.flight.entity.Airport;
import com.travelai.flight.entity.FlightSearch;
import com.travelai.flight.entity.FlightTemplate;
import com.travelai.flight.repository.AirportRepository;
import com.travelai.flight.repository.FlightSearchRepository;
import com.travelai.flight.repository.FlightTemplateRepository;

@Service
public class FlightSearchService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneOffset IST = ZoneOffset.ofHoursMinutes(5, 30);

    private final AirportRepository airportRepository;
    private final FlightTemplateRepository flightTemplateRepository;
    private final FlightSearchRepository flightSearchRepository;
    private final ObjectMapper objectMapper;

    public FlightSearchService(
            AirportRepository airportRepository,
            FlightTemplateRepository flightTemplateRepository,
            FlightSearchRepository flightSearchRepository,
            ObjectMapper objectMapper) {
        this.airportRepository = airportRepository;
        this.flightTemplateRepository = flightTemplateRepository;
        this.flightSearchRepository = flightSearchRepository;
        this.objectMapper = objectMapper;
    }

    public FlightSearchResponse search(FlightSearchRequest request) {
        if (request.returnDate() != null && request.returnDate().isBefore(request.departureDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return date cannot be before departure date");
        }

        String origin = resolveAirportCode(request.origin());
        String destination = resolveAirportCode(request.destination());
        if (origin.equalsIgnoreCase(destination)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin and destination must be different");
        }

        List<FlightTemplate> templates = flightTemplateRepository
            .findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination);

        List<FlightOption> flights = new ArrayList<>();
        if (templates.isEmpty()) {
            flights.addAll(syntheticFlights(origin, destination, request.departureDate()));
        } else {
            for (FlightTemplate template : templates) {
                flights.add(toOption(template, request.departureDate()));
            }
        }

        if (request.maxTotalBudget() != null && request.maxTotalBudget().signum() > 0) {
            BigDecimal perTravelerCap = request.maxTotalBudget()
                .divide(BigDecimal.valueOf(Math.max(request.travelers(), 1)), 2, RoundingMode.HALF_UP);
            flights = flights.stream()
                .filter(flight -> flight.price().compareTo(perTravelerCap) <= 0)
                .toList();
        }

        flights = flights.stream()
            .sorted(Comparator.comparing(FlightOption::price))
            .toList();

        FlightSearchResponse response = new FlightSearchResponse(
            "FL-" + (10000 + flightSearchRepository.count() + 1),
            origin,
            destination,
            flights
        );

        FlightSearch saved = new FlightSearch();
        saved.setSearchId(response.searchId());
        saved.setOrigin(origin);
        saved.setDestination(destination);
        saved.setDepartureDate(request.departureDate());
        saved.setReturnDate(request.returnDate());
        saved.setTravelers(request.travelers());
        try {
            saved.setResultJson(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            saved.setResultJson("[]");
        }
        flightSearchRepository.save(saved);
        return response;
    }

    public FlightDetailResponse getFlight(String flightId) {
        FlightTemplate template = flightTemplateRepository.findByFlightId(flightId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));
        LocalDate today = LocalDate.now();
        FlightOption option = toOption(template, today);
        return new FlightDetailResponse(
            option.flightId(),
            option.airline(),
            option.flightNumber(),
            template.getOrigin(),
            template.getDestination(),
            option.departure().time(),
            option.arrival().time(),
            option.durationMinutes(),
            option.stops(),
            option.price(),
            option.currency()
        );
    }

    public AirportListResponse airports(String query) {
        List<Airport> matches;
        if (query == null || query.isBlank()) {
            matches = airportRepository.findAll();
        } else {
            matches = airportRepository
                .findByCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrNameContainingIgnoreCase(query, query, query);
        }
        return new AirportListResponse(matches.stream()
            .map(airport -> new AirportResponse(airport.getCode(), airport.getName(), airport.getCity(), airport.getCountry()))
            .toList());
    }

    private String resolveAirportCode(String value) {
        String trimmed = value.trim();
        return airportRepository.findByCodeIgnoreCase(trimmed)
            .map(Airport::getCode)
            .orElseGet(() -> airportRepository.findAll().stream()
                .filter(airport -> airport.getCity().equalsIgnoreCase(trimmed)
                    || airport.getName().toLowerCase(Locale.ROOT).contains(trimmed.toLowerCase(Locale.ROOT)))
                .map(Airport::getCode)
                .findFirst()
                .orElse(trimmed.toUpperCase(Locale.ROOT)));
    }

    private FlightOption toOption(FlightTemplate template, LocalDate date) {
        OffsetDateTime departure = OffsetDateTime.of(
            date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            template.getDepartureHour(), template.getDepartureMinute(), 0, 0, IST);
        OffsetDateTime arrival = departure.plusMinutes(template.getDurationMinutes());
        return new FlightOption(
            template.getFlightId(),
            template.getAirline(),
            template.getFlightNumber(),
            new AirportTime(template.getOrigin(), departure.format(ISO)),
            new AirportTime(template.getDestination(), arrival.format(ISO)),
            template.getDurationMinutes(),
            template.getStops(),
            template.getPrice(),
            template.getCurrency()
        );
    }

    private List<FlightOption> syntheticFlights(String origin, String destination, LocalDate date) {
        List<int[]> slots = List.of(
            new int[] {6, 30, 150, 0, 7200},
            new int[] {9, 15, 165, 0, 8100},
            new int[] {14, 45, 210, 1, 6400},
            new int[] {19, 10, 155, 0, 9800}
        );
        List<FlightOption> flights = new ArrayList<>();
        String[] airlines = {"IndiGo", "Air India", "Vistara", "Akasa Air"};
        for (int i = 0; i < slots.size(); i++) {
            int[] slot = slots.get(i);
            OffsetDateTime departure = OffsetDateTime.of(
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), slot[0], slot[1]), IST);
            OffsetDateTime arrival = departure.plusMinutes(slot[2]);
            flights.add(new FlightOption(
                origin + "-" + destination + "-" + (i + 1),
                airlines[i],
                "6E" + (100 + i),
                new AirportTime(origin, departure.format(ISO)),
                new AirportTime(destination, arrival.format(ISO)),
                slot[2],
                slot[3],
                BigDecimal.valueOf(slot[4]),
                "INR"
            ));
        }
        return flights;
    }
}
