package com.myshop.myshopbackend.controller;

import java.util.List;
import java.util.Map;

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

import com.myshop.myshopbackend.model.Staff;
import com.myshop.myshopbackend.repository.StaffRepository;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = {"https://myshop-pro-tushar-2004.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "true")
public class StaffController {

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping("/add")
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        return ResponseEntity.ok(staffRepository.save(staff));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Staff>> getStaffByShop(@PathVariable Long shopId) {
        List<Staff> staffList = staffRepository.findByShopId(shopId);
        return ResponseEntity.ok(staffList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        try {
            if (staffRepository.existsById(id)) {
                staffRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Staff deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "Staff member not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}