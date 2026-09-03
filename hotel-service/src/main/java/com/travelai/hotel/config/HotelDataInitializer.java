package com.travelai.hotel.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.travelai.hotel.entity.Hotel;
import com.travelai.hotel.repository.HotelRepository;

@Configuration
public class HotelDataInitializer {

    @Bean
    CommandLineRunner seedHotels(HotelRepository hotelRepository) {
        return args -> {
            if (hotelRepository.count() > 0) {
                return;
            }
            hotelRepository.saveAll(List.of(
                new Hotel("H-101", "Calangute Beach Hotel", 4.3, "Calangute, Goa", "GOI", "Goa", "Deluxe Room",
                    "WiFi,Pool,Breakfast,Beach access", new BigDecimal("4200"), "INR",
                    "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800"),
                new Hotel("H-102", "Panaji Riverside Stay", 4.1, "Panaji, Goa", "GOI", "Goa", "Superior Room",
                    "WiFi,Restaurant,Airport shuttle", new BigDecimal("3100"), "INR",
                    "https://images.unsplash.com/photo-1551882547-ff40c63ea374?w=800"),
                new Hotel("H-103", "Anjuna Cliff Resort", 4.6, "Anjuna, Goa", "GOI", "Goa", "Sea View Suite",
                    "WiFi,Pool,Spa,Breakfast", new BigDecimal("5800"), "INR",
                    "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800"),
                new Hotel("H-104", "Baga Budget Inn", 3.8, "Baga, Goa", "GOI", "Goa", "Standard Room",
                    "WiFi,Cafe", new BigDecimal("1900"), "INR",
                    "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800"),
                new Hotel("H-201", "Colaba Harbour Hotel", 4.4, "Colaba, Mumbai", "BOM", "Mumbai", "Deluxe Room",
                    "WiFi,Pool,Breakfast,Gym", new BigDecimal("6500"), "INR",
                    "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800"),
                new Hotel("H-202", "Bandra Boutique Stay", 4.2, "Bandra, Mumbai", "BOM", "Mumbai", "King Room",
                    "WiFi,Restaurant,Workspace", new BigDecimal("4800"), "INR",
                    "https://images.unsplash.com/photo-1564501049412-61c2a308c1ba?w=800"),
                new Hotel("H-301", "Connaught Place Hotel", 4.5, "Connaught Place, Delhi", "DEL", "Delhi", "Executive Room",
                    "WiFi,Breakfast,Gym,Concierge", new BigDecimal("7200"), "INR",
                    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800"),
                new Hotel("H-302", "Karol Bagh Comfort Inn", 4.0, "Karol Bagh, Delhi", "DEL", "Delhi", "Standard Room",
                    "WiFi,Breakfast", new BigDecimal("2800"), "INR",
                    "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800"),
                new Hotel("H-401", "MG Road Business Hotel", 4.3, "MG Road, Bengaluru", "BLR", "Bengaluru", "Deluxe Room",
                    "WiFi,Breakfast,Gym", new BigDecimal("5400"), "INR",
                    "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800"),
                new Hotel("H-501", "Le Marais Garden Hotel", 4.6, "Le Marais, Paris", "CDG", "Paris", "Classic Room",
                    "WiFi,Breakfast,Concierge", new BigDecimal("18000"), "INR",
                    "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800"),
                new Hotel("H-601", "SoHo Loft Hotel", 4.4, "SoHo, New York", "JFK", "New York", "Queen Room",
                    "WiFi,Gym,Workspace", new BigDecimal("22000"), "INR",
                    "https://images.unsplash.com/photo-1496417263034-38ec4f0d665e?w=800"),
                new Hotel("H-701", "South Bank View Hotel", 4.2, "South Bank, London", "LHR", "London", "Deluxe Room",
                    "WiFi,Breakfast,River view", new BigDecimal("19500"), "INR",
                    "https://images.unsplash.com/photo-1486299267070-83823f5448dd?w=800")
            ));
        };
    }
}
