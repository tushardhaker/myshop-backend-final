package com.myshop.myshopbackend.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
@CrossOrigin(origins = { "https://myshop-backend-final.vercel.app",
        "http://localhost:5500" }, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ShopRepository shopRepo;
    @Autowired
    private JavaMailSender mailSender;

    private final String FRONTEND_BASE = "https://myshop-backend-final.vercel.app/frontend/";

    // 1. Google Login & Registration Success Logic
    @GetMapping("/loginSuccess")
    public RedirectView getLoginInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return new RedirectView(FRONTEND_BASE + "login.html?error=auth_failed");
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        User user = userRepo.findByEmail(email);

        // Naya user hai ya role select nahi kiya
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

        // Purana user hai, role pata hai
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

        try {
            String encodedName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8.toString());
            String finalUrl = FRONTEND_BASE + targetPage + "?id=" + user.getId() +
                    "&name=" + encodedName + "&role=" + user.getRole() + extraParams;
            return new RedirectView(finalUrl);
        } catch (Exception e) {
            return new RedirectView(FRONTEND_BASE + targetPage + "?id=" + user.getId());
        }
    }

    // 2. Role Update (After Google Registration)
    @PostMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String selectedRole = (String) request.get("role");

            Optional<User> userOpt = userRepo.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRole(selectedRole.toUpperCase());
                userRepo.save(user);

                return ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "role", user.getRole()));
            }
            return ResponseEntity.status(404).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 3. Normal Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        User user = userRepo.findByEmailOrMobile(loginRequest.get("identifier"));
        if (user != null && user.getPassword().equals(loginRequest.get("password"))) {
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
    }

    // 4. Normal Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: Email already registered!");
        }
        if (user.getRole() == null)
            user.setRole("CUSTOMER");
        user.setRole(user.getRole().toUpperCase());
        return ResponseEntity.ok(userRepo.save(user));
    }

    // 5. Forgot Password (OTP Hybrid Logic)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        User user = userRepo.findByEmailOrMobile(identifier);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found!"));
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        System.out.println("DEBUG: OTP for " + identifier + " is: " + otp);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Reset Your Password - MyShop");
            message.setText("Your OTP is: " + otp);
            mailSender.send(message);

            return ResponseEntity.ok(Map.of("message", "Email Sent"));
        } catch (Exception e) {
            // Render block karega toh ye wala response jayega
            return ResponseEntity.ok(Map.of(
                    "message", "Mail Server Offline",
                    "otp", otp));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        User user = userRepo.findByEmailOrMobile(identifier);
        if (user != null && otp != null && otp.equals(user.getOtp())) {
            if (user.getOtpExpiry().isAfter(LocalDateTime.now())) {
                user.setPassword(newPassword);
                user.setOtp(null);
                user.setOtpExpiry(null);
                userRepo.save(user);
                return ResponseEntity.ok(Map.of("message", "Success"));
            }
            return ResponseEntity.badRequest().body(Map.of("message", "OTP Expired!"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP!"));
    }
}