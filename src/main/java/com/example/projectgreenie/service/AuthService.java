package com.example.projectgreenie.service;

import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HttpServletRequest request;

    public String getLoggedInUserId() {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                return userRepository.findByEmail(email)
                    .map(user -> user.getId())
                    .orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
