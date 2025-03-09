package com.example.projectgreenie.service;

import org.springframework.security.oauth2.jwt.Jwt;  // Use the Spring Security Jwt class
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getSubject(); // ✅ Assuming subject (sub) is userId
        }
        return null;
    }
}
