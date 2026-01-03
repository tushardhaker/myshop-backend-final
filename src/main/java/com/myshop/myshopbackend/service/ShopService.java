package com.myshop.myshopbackend.service;

import org.springframework.stereotype.Service;

import com.myshop.myshopbackend.model.Shop;
import com.myshop.myshopbackend.model.User;
import com.myshop.myshopbackend.repository.ShopRepository;
import com.myshop.myshopbackend.repository.UserRepository;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public Shop createShop(String name, String address, String contact, Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (shopRepository.findByOwnerId(ownerId).isPresent()) {
            throw new RuntimeException("Shop already exists for this user");
        }

        Shop shop = new Shop();
        shop.setName(name);
        shop.setAddress(address);
        shop.setContact(contact);
        // shop.setOwner(user);

        Shop savedShop = shopRepository.save(shop);

        // User table mein shop mapping update karein
        // user.setShop(savedShop);
        userRepository.save(user);

        return savedShop;
    }
}