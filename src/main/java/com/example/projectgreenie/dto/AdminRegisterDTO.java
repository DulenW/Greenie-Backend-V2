package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class AdminRegisterDTO {
    private String name;
    private String email;
    private String password;
    private String role;
}
