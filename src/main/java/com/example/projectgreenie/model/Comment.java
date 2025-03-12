package com.example.projectgreenie.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    private String postId;
    private String userId;
    private String comment;
    private LocalDateTime timestamp;
}
