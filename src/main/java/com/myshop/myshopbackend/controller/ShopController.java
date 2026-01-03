package com.myshop.myshopbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.myshop.myshopbackend.model.Shop;
import com.myshop.myshopbackend.repository.ShopRepository;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "*")
public class ShopController {

    @Autowired
    private ShopRepository shopRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestBody Shop shop) {
        try {
            if (shop.getOwnerId() == null) {
                return ResponseEntity.badRequest().body("Error: ownerId is missing in request!");
            }
            Shop savedShop = shopRepo.save(shop);
            return ResponseEntity.ok(savedShop);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long ownerId) {
        return shopRepo.findByOwnerId(ownerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}