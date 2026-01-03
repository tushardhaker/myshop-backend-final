package com.myshop.myshopbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByShopId(Long shopId);
    
    // Case-insensitive search taaki 'Apple' aur 'apple' dono match ho jayein
    Optional<Product> findByNameIgnoreCase(String name);
}