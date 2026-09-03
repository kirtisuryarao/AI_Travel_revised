package com.travelai.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelai.user.entity.Role;
import com.travelai.user.entity.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
