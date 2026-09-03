package com.lekha.travel_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lekha.travel_planner.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByNameAsc();
    List<Product> findByCategoryIgnoreCaseAndActiveTrueOrderByNameAsc(String category);
}
