package com.example.projectgreenie.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "posts")
public class Post {

    @Id
    private String postId;
    private String userId;
    private String content;
    private String image;


    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
