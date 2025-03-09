package com.example.projectgreenie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String userId;
    private String fullName;
    private String email;
    private String profileImgUrl;
    private int pointsCount;
}
