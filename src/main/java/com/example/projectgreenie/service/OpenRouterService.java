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
    private static final String API_KEY = "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";

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

            // Construct API Request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "google/gemini-2.0-flash-lite-001");

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", "Does this image show a real environmental cleanup effort?");

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");

            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", imageUrl);
            imagePart.put("image_url", imageUrlMap);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", new Object[]{textPart, imagePart});

            requestBody.put("messages", Collections.singletonList(userMessage));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make API Call
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // Log API response
            System.out.println("📡 OpenRouter API Response: " + response.getBody());

            // Parse AI Response
            return parseResponseAndReturnAIContent(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error"; // Handle failures
        }
    }

    private String parseResponseAndReturnAIContent(String responseBody) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            System.out.println("🔍 FULL API RESPONSE: " + jsonResponse.toPrettyString());

            JsonNode choices = jsonResponse.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                // Extract the relevant message content
                String aiResponseText = choices.get(0).path("message").path("content").asText();

                // Log and return only the relevant description from the AI response
                System.out.println("🤖 AI Response: " + aiResponseText);
                return aiResponseText.trim(); // Trim whitespace and return the relevant description
            }

            return "Issue"; // If no valid response
        } catch (Exception e) {
            e.printStackTrace();
            return "Error"; // Parsing error
        }
    }

}
