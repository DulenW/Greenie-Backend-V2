package com.example.projectgreenie.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDTO {

    private String postId;
    private String userId;
    private String fullName;
    private String username;
    private String profileImage;
    private String description;
    private String image;
    private int likes;
    private List<String> commentIds;
    private long timestamp;

}
