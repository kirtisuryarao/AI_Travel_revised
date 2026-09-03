package com.lekha.travel_planner.config;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lekha.travel_planner.entity.Product;
import com.lekha.travel_planner.entity.Role;
import com.lekha.travel_planner.entity.RoleName;
import com.lekha.travel_planner.entity.User;
import com.lekha.travel_planner.repository.ProductRepository;
import com.lekha.travel_planner.repository.RoleRepository;
import com.lekha.travel_planner.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.USER)));
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));

            if (!userRepository.existsByEmail("admin@travelai.com")) {
                User admin = new User();
                admin.setEmail("admin@travelai.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Travel Admin");
                admin.getRoles().add(adminRole);
                admin.getRoles().add(userRole);
                userRepository.save(admin);
                logger.info("Seeded admin user: admin@travelai.com / admin123");
            }

            if (!userRepository.existsByEmail("demo@travelai.com")) {
                User demo = new User();
                demo.setEmail("demo@travelai.com");
                demo.setPassword(passwordEncoder.encode("demo123"));
                demo.setFullName("Demo Traveler");
                demo.getRoles().add(userRole);
                userRepository.save(demo);
                logger.info("Seeded demo user: demo@travelai.com / demo123");
            }

            if (productRepository.count() == 0) {
                productRepository.saveAll(java.util.List.of(
                    buildProduct("Kyoto Rail Pass (3-Day)", "Unlimited JR local travel around Kyoto", "Transport", "1299.00", 50),
                    buildProduct("Universal Travel Adapter", "Works in 150+ countries with USB-C", "Accessories", "899.00", 120),
                    buildProduct("Packing Cube Set", "Lightweight 6-piece organizer set", "Accessories", "1499.00", 80),
                    buildProduct("Travel Insurance (7 days)", "Medical + trip cancellation cover", "Insurance", "2499.00", 200),
                    buildProduct("Airport Lounge Day Pass", "Single-entry lounge access voucher", "Comfort", "1999.00", 40),
                    buildProduct("Portable Power Bank 20K", "Fast-charge battery for long trips", "Electronics", "2199.00", 65)
                ));
                logger.info("Seeded sample travel products");
            }
        };
    }

    private Product buildProduct(String name, String description, String category, String price, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        product.setActive(true);
        return product;
    }
}
