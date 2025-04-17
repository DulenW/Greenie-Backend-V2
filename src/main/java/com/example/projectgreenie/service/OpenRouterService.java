package com.example.projectgreenie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenRouterService {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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"; // 🔁 Replace with your actual key

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenRouterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String checkImage(String imageUrl, String description) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 🧠 Strict Prompt
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "google/gemini-pro-vision");

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", """
                You are an expert AI tasked with verifying the authenticity of images submitted for environmental cleanup challenges. You must determine if the image is a **real, natural photograph** or a **fake, AI-generated, animated, digitally illustrated, or cartoon image**.

                🛑 Do NOT approve:
                - AI-generated or cartoon-style images
                - Digitally illustrated characters or backgrounds
                - Unrealistic lighting, textures, faces, or hands
                - Anything that lacks real-world photographic characteristics
                Even if the description matches, the image must still be rejected if it is not a natural photo.

                ✅ Only approve:
                - Authentic, natural, real-world photographs
                - Clear, unedited, human-taken images

                🔍 Now, analyze the image and the description provided. Determine:

                Status: Verified or Issue  
                Reason:Provide a strict explanation for your decision. Be clear whether the image is AI-generated, cartoon-like, or real.

                Always err on the side of caution. Any sign of non-natural or digital creation must result in "Issue".

                Description: %s
                """.formatted(description));

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");
            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", imageUrl);
            imagePart.put("image_url", imageUrlMap);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", new Object[]{textPart, imagePart});

            requestBody.put("messages", Collections.singletonList(message));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            System.out.println("📡 OpenRouter API Response: " + response.getBody());

            return extractStatusAndReason(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return "Issue | AI validation failed due to server error.";
        }
    }

    private String extractStatusAndReason(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            System.out.println("🔍 FULL AI RESPONSE: " + content);

            // Parse for format: "Status: ... Reason: ..."
            if (content.toLowerCase().contains("status:") && content.toLowerCase().contains("reason:")) {
                String status = content.split("(?i)status:")[1].split("(?i)reason:")[0].trim();
                String reason = content.split("(?i)reason:")[1].trim();

                return status + " | " + reason;
            }

            return "Issue | AI response format invalid.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Issue | Error parsing AI response.";
        }
    }
}
