package com.example.projectgreenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class WebSocketTestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/broadcast")
    public ResponseEntity<String> testBroadcast() {
        Map<String, Object> post = new HashMap<>();
        post.put("postId", "POST-TEST-001");
        post.put("userId", "test-user");
        post.put("description", "🔥 Real-time test post broadcasted!");
        post.put("likes", 0);
        post.put("commentIds", new String[]{});

        Map<String, Object> message = new HashMap<>();
        message.put("type", "post");
        message.put("payload", post);

        messagingTemplate.convertAndSend("/topic/feed", message);

        return ResponseEntity.ok("Test post sent via WebSocket!");
    }
}
