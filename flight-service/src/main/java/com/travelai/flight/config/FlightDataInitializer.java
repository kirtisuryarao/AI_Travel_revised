package com.travelai.flight.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.travelai.flight.entity.Airport;
import com.travelai.flight.entity.FlightTemplate;
import com.travelai.flight.repository.AirportRepository;
import com.travelai.flight.repository.FlightTemplateRepository;

@Configuration
public class FlightDataInitializer {

    @Bean
    CommandLineRunner seedFlights(AirportRepository airportRepository, FlightTemplateRepository flightTemplateRepository) {
        return args -> {
            if (airportRepository.count() == 0) {
                airportRepository.saveAll(List.of(
                    new Airport("DEL", "Indira Gandhi International Airport", "Delhi", "India"),
                    new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India"),
                    new Airport("BLR", "Kempegowda International Airport", "Bengaluru", "India"),
                    new Airport("GOI", "Manohar International Airport", "Goa", "India"),
                    new Airport("MAA", "Chennai International Airport", "Chennai", "India"),
                    new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India"),
                    new Airport("CCU", "Netaji Subhas Chandra Bose International Airport", "Kolkata", "India"),
                    new Airport("PNQ", "Pune Airport", "Pune", "India"),
                    new Airport("JFK", "John F. Kennedy International Airport", "New York", "United States"),
                    new Airport("CDG", "Charles de Gaulle Airport", "Paris", "France"),
                    new Airport("LHR", "Heathrow Airport", "London", "United Kingdom")
                ));
            }

            if (flightTemplateRepository.count() == 0) {
                flightTemplateRepository.saveAll(List.of(
                    new FlightTemplate("AI-123", "Air India", "AI123", "DEL", "GOI", 150, 0, new BigDecimal("8500"), "INR", 8, 30),
                    new FlightTemplate("6E-204", "IndiGo", "6E204", "DEL", "GOI", 155, 0, new BigDecimal("7200"), "INR", 6, 15),
                    new FlightTemplate("UK-511", "Vistara", "UK511", "DEL", "GOI", 205, 1, new BigDecimal("6900"), "INR", 13, 40),
                    new FlightTemplate("QP-118", "Akasa Air", "QP118", "DEL", "GOI", 160, 0, new BigDecimal("9100"), "INR", 19, 5),
                    new FlightTemplate("AI-441", "Air India", "AI441", "BOM", "GOI", 75, 0, new BigDecimal("4500"), "INR", 7, 20),
                    new FlightTemplate("6E-318", "IndiGo", "6E318", "BOM", "GOI", 80, 0, new BigDecimal("3900"), "INR", 11, 10),
                    new FlightTemplate("AI-802", "Air India", "AI802", "BLR", "GOI", 90, 0, new BigDecimal("5200"), "INR", 9, 45),
                    new FlightTemplate("6E-901", "IndiGo", "6E901", "DEL", "BOM", 130, 0, new BigDecimal("6100"), "INR", 8, 0),
                    new FlightTemplate("UK-940", "Vistara", "UK940", "DEL", "BOM", 140, 0, new BigDecimal("7800"), "INR", 17, 30),
                    new FlightTemplate("AI-111", "Air India", "AI111", "DEL", "BLR", 160, 0, new BigDecimal("6700"), "INR", 6, 50),
                    new FlightTemplate("6E-555", "IndiGo", "6E555", "BOM", "BLR", 105, 0, new BigDecimal("4800"), "INR", 10, 25),
                    new FlightTemplate("AI-101", "Air India", "AI101", "DEL", "JFK", 870, 1, new BigDecimal("72000"), "INR", 2, 15),
                    new FlightTemplate("AI-141", "Air India", "AI141", "DEL", "LHR", 540, 0, new BigDecimal("48000"), "INR", 13, 10),
                    new FlightTemplate("AF-225", "Air France", "AF225", "DEL", "CDG", 560, 0, new BigDecimal("51000"), "INR", 1, 40)
                ));
            }
        };
    }
}
