package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String fullName;
    private String username;
    private String bio;
    private String coverImgUrl;
}