package com.lekha.travel_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lekha.travel_planner.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
