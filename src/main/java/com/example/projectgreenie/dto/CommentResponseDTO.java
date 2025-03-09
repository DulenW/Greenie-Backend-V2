package com.example.projectgreenie.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDTO {
    private String commentId;
    private String userId;
    private String fullName;
    private String username;
    private String profileImage;
    private String commentText;
//    private LocalDateTime timestamp;
}