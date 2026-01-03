package com.myshop.myshopbackend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

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
// NAYA: Isse hum SecurityConfig se manage karenge, isliye yahan broad permission di hai 
// taaki production par error na aaye.
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatRepo;

    // Isse hum Render ka URL environment variable se uthayenge
    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage msg) {
        return chatRepo.save(msg);
    }

    @GetMapping("/history/{user1}/{user2}")
    public List<ChatMessage> getHistory(@PathVariable Long user1, @PathVariable Long user2) {
        return chatRepo.findChatHistory(user1, user2);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // NAYA: Ab ye dynamic backendUrl use karega (Render ka link)
            String fileUrl = backendUrl + "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading file");
        }
    }
}

