package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class ReactToPostRequest {
    private String emoji;   // e.g., "❤️"
    private String userId;
}
