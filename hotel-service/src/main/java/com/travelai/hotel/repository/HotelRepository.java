package com.travelai.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.hotel.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByHotelId(String hotelId);
    List<Hotel> findByDestinationCodeIgnoreCase(String destinationCode);
    List<Hotel> findByCityContainingIgnoreCaseOrDestinationCodeContainingIgnoreCase(String city, String destinationCode);
}
