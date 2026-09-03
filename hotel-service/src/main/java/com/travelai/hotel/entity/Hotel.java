package com.travelai.hotel.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hotelId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String destinationCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String roomType;

    @Column(nullable = false, length = 500)
    private String amenities;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @Column(nullable = false, length = 8)
    private String currency;

    private String imageUrl;

    public Hotel() {
    }

    public Hotel(
            String hotelId,
            String name,
            double rating,
            String location,
            String destinationCode,
            String city,
            String roomType,
            String amenities,
            BigDecimal pricePerNight,
            String currency,
            String imageUrl) {
        this.hotelId = hotelId;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.destinationCode = destinationCode;
        this.city = city;
        this.roomType = roomType;
        this.amenities = amenities;
        this.pricePerNight = pricePerNight;
        this.currency = currency;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getHotelId() {
        return hotelId;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getLocation() {
        return location;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public String getCity() {
        return city;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getAmenities() {
        return amenities;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public String getCurrency() {
        return currency;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
