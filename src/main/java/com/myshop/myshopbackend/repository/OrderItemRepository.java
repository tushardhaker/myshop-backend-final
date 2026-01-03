package com.myshop.myshopbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Shopkeeper ke dashboard ke liye
    List<OrderItem> findByShopId(Long shopId);
}