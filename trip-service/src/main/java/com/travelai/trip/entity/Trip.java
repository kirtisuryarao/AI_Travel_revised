package com.travelai.trip.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int travelers;

    private BigDecimal maxTotalBudget;
    private BigDecimal maxHotelBudgetPerNight;

    @Column(nullable = false)
    private String status = "SAVED";

    @Lob
    @Column(columnDefinition = "CLOB")
    private String interestsJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String selectedFlightJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String selectedHotelJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String itineraryJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String budgetEstimateJson;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTravelers() {
        return travelers;
    }

    public void setTravelers(int travelers) {
        this.travelers = travelers;
    }

    public BigDecimal getMaxTotalBudget() {
        return maxTotalBudget;
    }

    public void setMaxTotalBudget(BigDecimal maxTotalBudget) {
        this.maxTotalBudget = maxTotalBudget;
    }

    public BigDecimal getMaxHotelBudgetPerNight() {
        return maxHotelBudgetPerNight;
    }

    public void setMaxHotelBudgetPerNight(BigDecimal maxHotelBudgetPerNight) {
        this.maxHotelBudgetPerNight = maxHotelBudgetPerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInterestsJson() {
        return interestsJson;
    }

    public void setInterestsJson(String interestsJson) {
        this.interestsJson = interestsJson;
    }

    public String getSelectedFlightJson() {
        return selectedFlightJson;
    }

    public void setSelectedFlightJson(String selectedFlightJson) {
        this.selectedFlightJson = selectedFlightJson;
    }

    public String getSelectedHotelJson() {
        return selectedHotelJson;
    }

    public void setSelectedHotelJson(String selectedHotelJson) {
        this.selectedHotelJson = selectedHotelJson;
    }

    public String getItineraryJson() {
        return itineraryJson;
    }

    public void setItineraryJson(String itineraryJson) {
        this.itineraryJson = itineraryJson;
    }

    public String getBudgetEstimateJson() {
        return budgetEstimateJson;
    }

    public void setBudgetEstimateJson(String budgetEstimateJson) {
        this.budgetEstimateJson = budgetEstimateJson;
    }
}
