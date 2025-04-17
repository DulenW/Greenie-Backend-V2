package com.example.projectgreenie.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUserDTO {

    private String fullName;
    private String username;
    private String profileImage;

}
