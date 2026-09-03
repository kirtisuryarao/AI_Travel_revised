package com.travelai.hotel.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.travelai.hotel.dto.HotelDtos.DestinationListResponse;
import com.travelai.hotel.dto.HotelDtos.DestinationSuggestion;
import com.travelai.hotel.dto.HotelDtos.HotelDetailResponse;
import com.travelai.hotel.dto.HotelDtos.HotelOption;
import com.travelai.hotel.dto.HotelDtos.HotelSearchRequest;
import com.travelai.hotel.dto.HotelDtos.HotelSearchResponse;
import com.travelai.hotel.entity.Hotel;
import com.travelai.hotel.repository.HotelRepository;

@Service
public class HotelSearchService {

    private final HotelRepository hotelRepository;
    private long searchSequence = 20000;

    public HotelSearchService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public HotelSearchResponse search(HotelSearchRequest request) {
        if (request.checkOut().isBefore(request.checkIn()) || request.checkOut().isEqual(request.checkIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be after check-in date");
        }
        if (request.maxHotelBudgetPerNight() == null || request.maxHotelBudgetPerNight().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum hotel budget per night must be greater than 0");
        }

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        String destination = request.destination().trim();

        List<Hotel> matches = hotelRepository.findByDestinationCodeIgnoreCase(destination);
        if (matches.isEmpty()) {
            matches = hotelRepository.findByCityContainingIgnoreCaseOrDestinationCodeContainingIgnoreCase(destination, destination);
        }
        if (matches.isEmpty()) {
            matches = hotelRepository.findAll().stream()
                .filter(hotel -> hotel.getCity().toLowerCase(Locale.ROOT).contains(destination.toLowerCase(Locale.ROOT))
                    || hotel.getLocation().toLowerCase(Locale.ROOT).contains(destination.toLowerCase(Locale.ROOT)))
                .toList();
        }

        List<HotelOption> hotels = matches.stream()
            .filter(hotel -> hotel.getPricePerNight().compareTo(request.maxHotelBudgetPerNight()) <= 0)
            .map(hotel -> toOption(hotel, nights))
            .sorted(Comparator.comparing(HotelOption::pricePerNight))
            .toList();

        searchSequence++;
        return new HotelSearchResponse(
            "HT-" + searchSequence,
            destination.toUpperCase(Locale.ROOT),
            request.checkIn(),
            request.checkOut(),
            hotels
        );
    }

    public HotelDetailResponse getHotel(String hotelId) {
        Hotel hotel = hotelRepository.findByHotelId(hotelId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        return new HotelDetailResponse(
            hotel.getHotelId(),
            hotel.getName(),
            hotel.getRating(),
            hotel.getLocation(),
            hotel.getRoomType(),
            amenities(hotel),
            hotel.getPricePerNight(),
            hotel.getCurrency()
        );
    }

    public DestinationListResponse destinations(String query) {
        List<Hotel> hotels = hotelRepository.findAll();
        return new DestinationListResponse(hotels.stream()
            .filter(hotel -> query == null || query.isBlank()
                || hotel.getCity().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                || hotel.getDestinationCode().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
            .map(hotel -> new DestinationSuggestion(hotel.getDestinationCode(), hotel.getCity()))
            .distinct()
            .toList());
    }

    private HotelOption toOption(Hotel hotel, long nights) {
        BigDecimal total = hotel.getPricePerNight().multiply(BigDecimal.valueOf(Math.max(nights, 1)));
        return new HotelOption(
            hotel.getHotelId(),
            hotel.getName(),
            hotel.getRating(),
            hotel.getLocation(),
            hotel.getRoomType(),
            amenities(hotel),
            hotel.getPricePerNight(),
            total,
            hotel.getCurrency(),
            hotel.getImageUrl()
        );
    }

    private List<String> amenities(Hotel hotel) {
        return Arrays.stream(hotel.getAmenities().split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
    }
}
