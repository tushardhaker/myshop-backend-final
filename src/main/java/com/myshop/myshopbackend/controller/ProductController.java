package com.myshop.myshopbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.myshopbackend.model.Product;
import com.myshop.myshopbackend.repository.ProductRepository;
import com.myshop.myshopbackend.repository.ShopRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"https://myshop-backend-final.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ShopRepository shopRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepo.save(product));
    }

    @GetMapping("/shop/{shopId}")
    public List<Product> getProductsByShop(@PathVariable Long shopId) {
        return productRepo.findByShopId(shopId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productRepo.deleteById(id);
        return ResponseEntity.ok("Product Deleted");
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @GetMapping("/shop-info/{shopId}")
    public ResponseEntity<?> getShopInfo(@PathVariable Long shopId) {
        return shopRepo.findById(shopId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}