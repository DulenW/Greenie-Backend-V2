package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document(collection = "feedPost")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPost {

    @Id
    private String id;

    private String postId;
    private String userId;
    private String username;
    private String content;
    private String image;
    private LocalDateTime timestamp;

    @Builder.Default
    private List<String> commentIds = new ArrayList<>();

    private Map<String, List<String>> reactions = new HashMap<>();
}


