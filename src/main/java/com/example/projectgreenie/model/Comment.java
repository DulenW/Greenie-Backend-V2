package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    private String id; // MongoDB's default ObjectId

    @Builder.Default
    private String commentId = "CMT-" + UUID.randomUUID().toString().substring(0, 8); // Custom commentId

    private String postId;
    private String userId;
    private String comment;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}
