package com.travelai.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelai.hotel.dto.HotelDtos.DestinationListResponse;
import com.travelai.hotel.dto.HotelDtos.HotelDetailResponse;
import com.travelai.hotel.dto.HotelDtos.HotelSearchRequest;
import com.travelai.hotel.dto.HotelDtos.HotelSearchResponse;
import com.travelai.hotel.service.HotelSearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelSearchService hotelSearchService;

    public HotelController(HotelSearchService hotelSearchService) {
        this.hotelSearchService = hotelSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<HotelSearchResponse> search(@Valid @RequestBody HotelSearchRequest request) {
        return ResponseEntity.ok(hotelSearchService.search(request));
    }

    @GetMapping("/destinations")
    public ResponseEntity<DestinationListResponse> destinations(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(hotelSearchService.destinations(query));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetailResponse> getHotel(@PathVariable String hotelId) {
        return ResponseEntity.ok(hotelSearchService.getHotel(hotelId));
    }
}
