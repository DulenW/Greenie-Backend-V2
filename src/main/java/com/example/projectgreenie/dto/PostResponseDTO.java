package com.example.projectgreenie.dto;


import lombok.*;
import java.util.List;
import java.util.Map;

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
    private int commentCount;
    private List<String> commentIds;
    private Map<String, List<String>> reactions; // ✅ NEW
}
