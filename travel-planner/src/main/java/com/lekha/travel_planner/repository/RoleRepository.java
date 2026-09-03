package com.lekha.travel_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lekha.travel_planner.entity.Role;
import com.lekha.travel_planner.entity.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
