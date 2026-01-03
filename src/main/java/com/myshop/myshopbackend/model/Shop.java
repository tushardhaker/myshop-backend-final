package com.myshop.myshopbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "shops")
@Data
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String contact;

    // Proper Relationship: Isse 'Unable to determine Dialect' ya Mapping errors nahi aayenge
    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    // Frontend se sirf ID aayegi, isliye helper method ya mapping ki zaroorat padegi
    @Transient // Isse ye database mein save nahi hoga, sirf request handle karne ke liye hai
    private Long ownerId; 
}