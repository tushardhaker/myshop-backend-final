package com.myshop.myshopbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Customer ki history nikalne ke liye
    List<Order> findByUserId(Long userId);
}