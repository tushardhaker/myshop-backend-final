package com.myshop.myshopbackend.repository;

import com.myshop.myshopbackend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c WHERE (c.senderId = ?1 AND c.receiverId = ?2) OR (c.senderId = ?2 AND c.receiverId = ?1) ORDER BY c.timestamp ASC")
    List<ChatMessage> findChatHistory(Long user1, Long user2);
}