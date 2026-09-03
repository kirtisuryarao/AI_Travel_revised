package com.travelai.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.travelai.user.entity.Role;
import com.travelai.user.entity.RoleName;
import com.travelai.user.entity.User;
import com.travelai.user.repository.RoleRepository;
import com.travelai.user.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedUsers(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
        };
    }
}
