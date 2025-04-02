package com.example.projectgreenie.dto;

import com.example.projectgreenie.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private String commentId;
    private String postId;
    private String comment;
    private String userId;
    private User user;  // Include user details (profile photo, name)
    private LocalDateTime timestamp;  // Fix type mismatch
}