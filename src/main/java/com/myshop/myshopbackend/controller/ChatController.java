package com.myshop.myshopbackend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map; // Import added

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myshop.myshopbackend.model.ChatMessage;
import com.myshop.myshopbackend.repository.ChatMessageRepository;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"*", "https://myshop-backend-final.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "false")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatRepo;

    @Value("${app.backend.url:https://myshop-backend-final-1.onrender.com}")
    private String backendUrl;

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage msg) {
        if (msg.getSenderId() == null || msg.getReceiverId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // FIX: Using LocalDateTime to match your Model's data type
        if (msg.getTimestamp() == null) {
            msg.setTimestamp(LocalDateTime.now());
        }
        
        return ResponseEntity.ok(chatRepo.save(msg));
    }

    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String user1, @PathVariable String user2) {
        try {
            // Safely parse String to Long
            Long u1 = Long.parseLong(user1);
            Long u2 = Long.parseLong(user2);
            return ResponseEntity.ok(chatRepo.findChatHistory(u1, u2));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
            
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            Path path = Paths.get("uploads/" + fileName);
            
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = backendUrl + "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error uploading file: " + e.getMessage()));
        }
    }
}