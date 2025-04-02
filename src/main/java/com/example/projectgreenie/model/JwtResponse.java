package com.example.projectgreenie.model;

public class JwtResponse {
    private String token;
    private String role;
    private String id;

    public JwtResponse(String token, String role, String id) {
        this.token = token;
        this.role = role;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
