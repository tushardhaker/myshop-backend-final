package com.myshop.myshopbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.myshopbackend.model.Shop;
import com.myshop.myshopbackend.repository.ShopRepository;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"https://myshop-pro-tushar-2004.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "true")
public class ShopController {

    @Autowired
    private ShopRepository shopRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestBody Shop shop) {
        try {
            if (shop.getOwnerId() == null) {
                return ResponseEntity.badRequest().body("Error: ownerId missing!");
            }
            
            // Seedhe save kar do, koi extra mapping nahi
            Shop savedShop = shopRepo.save(shop);
            return ResponseEntity.ok(savedShop);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long ownerId) {
        return shopRepo.findByOwnerId(ownerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}