package com.myshop.myshopbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private Double price;
    private Integer stock;
    private String description;
    private String imageUrl;

    private Long shopId; // Isse product shop se link hoga
}