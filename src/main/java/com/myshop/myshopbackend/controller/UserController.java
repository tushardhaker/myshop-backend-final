package com.myshop.myshopbackend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.myshop.myshopbackend.model.Shop;
import com.myshop.myshopbackend.model.User;
import com.myshop.myshopbackend.repository.ShopRepository;
import com.myshop.myshopbackend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ShopRepository shopRepo;
    @Autowired
    private JavaMailSender mailSender;

    private final String FRONTEND_BASE = "http://127.0.0.1:5500/frontend/";

    @GetMapping("/loginSuccess")
    public RedirectView getLoginInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return new RedirectView(FRONTEND_BASE + "login.html?error=auth_failed");
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        User user = userRepo.findByEmail(email);

        if (user == null || "NEW_USER".equals(user.getRole())) {
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole("NEW_USER");
                user.setPassword("GOOGLE_USER_" + UUID.randomUUID().toString().substring(0, 5));
                user = userRepo.save(user);
            }
            return new RedirectView(FRONTEND_BASE + "choose-role.html?userId=" + user.getId());
        }

        String targetPage = "customer.html";
        String extraParams = "";

        if ("SHOPKEEPER".equalsIgnoreCase(user.getRole())) {
            Optional<Shop> shop = shopRepo.findByOwnerId(user.getId());
            if (shop.isPresent()) {
                targetPage = "shopkeeper_dashboard.html";
                extraParams = "&shopId=" + shop.get().getId();
            } else {
                targetPage = "create-shop.html";
            }
        }

        String finalUrl = FRONTEND_BASE + targetPage + "?id=" + user.getId() + "&name=" + user.getName().replace(" ", "%20") + "&role=" + user.getRole() + extraParams;
        return new RedirectView(finalUrl);
    }

    @PostMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestBody Map<String, Object> request) {
        try {
            Object idObj = request.get("userId");
            if (idObj == null) return ResponseEntity.badRequest().body("User ID is missing");
            
            Long userId = Long.parseLong(idObj.toString());
            String selectedRole = (String) request.get("role");

            Optional<User> userOpt = userRepo.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRole(selectedRole.toUpperCase());
                userRepo.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(404).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String identifier = loginRequest.get("identifier");
            String password = loginRequest.get("password");

            if (identifier == null || password == null) {
                return ResponseEntity.badRequest().body("Identifier or Password missing");
            }

            User user = userRepo.findByEmailOrMobile(identifier);
            
            if (user != null && user.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("role", user.getRole());
                
                if ("SHOPKEEPER".equalsIgnoreCase(user.getRole())) {
                    response.put("shopId", shopRepo.findByOwnerId(user.getId()).map(Shop::getId).orElse(null));
                }
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (userRepo.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Error: Email already registered!");
            }

            if (user.getMobile() != null && !user.getMobile().isEmpty()) {
                if (userRepo.findByMobile(user.getMobile()) != null) {
                    return ResponseEntity.badRequest().body("Error: Mobile number already registered!");
                }
            }

            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("CUSTOMER");
            } else {
                user.setRole(user.getRole().toUpperCase());
            }

            User savedUser = userRepo.save(user);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Registration Failed: " + e.getMessage());
        }
    }

   // UserController.java ke forgot-password block ko isse replace karein
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
    String identifier = request.get("identifier");
    User user = userRepo.findByEmailOrMobile(identifier);
    
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
    }
    
    // 1. OTP Generate and Save
    String otp = String.valueOf(new Random().nextInt(900000) + 100000);
    user.setOtp(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
    userRepo.save(user);

    System.out.println("DEBUG: OTP for " + identifier + " is: " + otp);

    try {
        // 2. Attempt to Send Mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("OTP Reset | MyShop");
        message.setText("Your OTP for password reset is: " + otp);
        mailSender.send(message);
        
        return ResponseEntity.ok(Map.of("message", "OTP Sent Successfully to your email"));
    } catch (Exception e) {
        // 3. Fallback: Agar mail fail ho jaye toh error mat dikhao, balki OTP bhej do
        System.err.println("Mail sending failed: " + e.getMessage());
        
        Map<String, String> fallbackResponse = new HashMap<>();
        fallbackResponse.put("message", "Mail Server Busy. Use this OTP for now (Dev Mode)");
        fallbackResponse.put("otp", otp); // Ye line aapko browser mein OTP dikha degi
        return ResponseEntity.ok(fallbackResponse); 
    }
}

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        User user = userRepo.findByEmailOrMobile(identifier);
        
        if (user != null && otp != null && otp.equals(user.getOtp()) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            user.setPassword(newPassword);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepo.save(user);
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.badRequest().body("Invalid/Expired OTP");
    }
}