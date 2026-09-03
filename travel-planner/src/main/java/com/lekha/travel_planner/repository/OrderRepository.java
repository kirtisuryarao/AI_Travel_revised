package com.lekha.travel_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lekha.travel_planner.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items i JOIN FETCH i.product WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdWithItems(Long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.items i JOIN FETCH i.product ORDER BY o.createdAt DESC")
    List<Order> findAllWithItems();
}
