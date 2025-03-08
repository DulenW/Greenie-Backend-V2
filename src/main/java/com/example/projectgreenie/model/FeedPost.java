package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Setter
@Getter
@Document(collection = "feedPost")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPost {
    
    @Field("postId")
    private String postId;

    private String userId;
    private String content;
    private String image;
    private long timestamp;

    private int likes;
    private List<String> commentIds;

}
