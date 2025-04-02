package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "feedPost")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPost {
    @Id
    // meka mongo id ek
    private String id;

    // custom post id eka
    private String postId;
    private String userId;
    private String content;
    private String image;
    private LocalDateTime timestamp;
    private int likes;
    @Builder.Default
    private List<String> commentIds = new ArrayList<>();
}
