package com.myshop.myshopbackend.controller;

import java.util.Optional;

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
import com.myshop.myshopbackend.model.User;
import com.myshop.myshopbackend.repository.ShopRepository;
import com.myshop.myshopbackend.repository.UserRepository;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"https://myshop-backend-final.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "true")
public class ShopController {

    @Autowired
    private ShopRepository shopRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestBody Shop shopRequest) {
        try {
            if (shopRequest.getOwnerId() == null) {
                return ResponseEntity.badRequest().body("Error: ownerId is missing!");
            }

            // 1. Check if user exists
            Optional<User> userOpt = userRepo.findById(shopRequest.getOwnerId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Error: User not found!");
            }

            // 2. Map data to new Shop object
            Shop newShop = new Shop();
            newShop.setName(shopRequest.getName());
            newShop.setAddress(shopRequest.getAddress());
            newShop.setContact(shopRequest.getContact());
            
            // Link the actual User object
            User user = userOpt.get();
            newShop.setOwner(user);

            // 3. Save Shop
            Shop savedShop = shopRepo.save(newShop);

            // 4. Update user role to SHOPKEEPER if not already
            if (!"SHOPKEEPER".equals(user.getRole())) {
                user.setRole("SHOPKEEPER");
                userRepo.save(user);
            }

            return ResponseEntity.ok(savedShop);
        } catch (Exception e) {
            e.printStackTrace(); // Logs mein detail dekhne ke liye
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long ownerId) {
        // Repository mein bhi change karna hoga thoda sa
        Optional<Shop> shop = shopRepo.findByOwnerId(ownerId);
        return shop.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}