package com.example.projectgreenie.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "feedPost")
public class FeedPost {

    @Id
    private String postId;
    private String userId;
    private String content;
    private String image;
    private long timestamp;


}
