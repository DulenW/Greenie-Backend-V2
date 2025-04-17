package com.example.projectgreenie.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "savedPosts")
public class SavedPost {

    @Id
    private String savePostId;

    private String userId;   // Who saved it
    private String postId;   // Which post was saved

    private LocalDateTime savedAt;

    public SavedPost(String string, String postId, String userId) {
    }
}
