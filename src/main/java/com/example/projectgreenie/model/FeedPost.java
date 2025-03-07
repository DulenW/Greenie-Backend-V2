package com.example.projectgreenie.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@Document(collection = "feedPost")
public class FeedPost {
    
    @Field("postId")
    private String postId;

    private String userId;
    private String content;
    private String image;
    private long timestamp;


}
