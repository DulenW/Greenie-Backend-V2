package com.example.projectgreenie.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private String commentId;
    private String postId;
    private String comment;
    private String userId;
    private CommentUserDTO user; // ✅ changed from User to lightweight CommentUserDTO
    private LocalDateTime timestamp;
}
