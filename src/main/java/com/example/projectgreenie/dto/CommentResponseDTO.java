package com.example.projectgreenie.dto;

import com.example.projectgreenie.model.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@Getter
@Setter
public class CommentResponseDTO {
    private String commentId;
    private String postId;
    private String comment;
    private String userId;
    private User user;  // Include the user details here (profile photo, name)
    private Date timestamp;

    public CommentResponseDTO(String commentId, String postId, String comment, String userId, User user, Date timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.comment = comment;
        this.userId = userId;
        this.user = user;
        this.timestamp = timestamp;
    }

    public CommentResponseDTO() {

    }
}