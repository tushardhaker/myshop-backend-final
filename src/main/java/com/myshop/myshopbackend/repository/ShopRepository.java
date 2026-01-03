package com.myshop.myshopbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // Relationship ke base par find karega
    Optional<Shop> findByOwnerId(Long ownerId);
}