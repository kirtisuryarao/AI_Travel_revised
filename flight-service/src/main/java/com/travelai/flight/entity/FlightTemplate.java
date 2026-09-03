package com.travelai.flight.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flight_templates")
public class FlightTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightId;

    @Column(nullable = false)
    private String airline;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false, length = 8)
    private String origin;

    @Column(nullable = false, length = 8)
    private String destination;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int stops;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, length = 8)
    private String currency;

    @Column(nullable = false)
    private int departureHour;

    @Column(nullable = false)
    private int departureMinute;

    public FlightTemplate() {
    }

    public FlightTemplate(
            String flightId,
            String airline,
            String flightNumber,
            String origin,
            String destination,
            int durationMinutes,
            int stops,
            BigDecimal price,
            String currency,
            int departureHour,
            int departureMinute) {
        this.flightId = flightId;
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.durationMinutes = durationMinutes;
        this.stops = stops;
        this.price = price;
        this.currency = currency;
        this.departureHour = departureHour;
        this.departureMinute = departureMinute;
    }

    public Long getId() {
        return id;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getAirline() {
        return airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getStops() {
        return stops;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public int getDepartureHour() {
        return departureHour;
    }

    public int getDepartureMinute() {
        return departureMinute;
    }
}
