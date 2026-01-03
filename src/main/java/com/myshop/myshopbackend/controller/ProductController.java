package com.myshop.myshopbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Added this
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
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ShopRepository shopRepo; // Added this to fix the error in getShopInfo

    // Product save karne ke liye
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepo.save(product));
    }

    // Ek specific shop ke saare products dikhane ke liye
    @GetMapping("/shop/{shopId}")
    public List<Product> getProductsByShop(@PathVariable Long shopId) {
        return productRepo.findByShopId(shopId);
    }

    // Product delete karne ke liye
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productRepo.deleteById(id);
        return ResponseEntity.ok("Product Deleted");
    }

    // Saare products dikhane ke liye (Customer ke liye)
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // Specific shop ki details dikhane ke liye
    @GetMapping("/shop-info/{shopId}")
    public ResponseEntity<?> getShopInfo(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopRepo.findById(shopId));
    }
}