package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.LoginResponseDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://test.greenie.dizzpy.dev")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        boolean authenticated = userService.authenticate(email, password);
        if (authenticated) {
            String token = jwtUtil.generateToken(email);
            
            // Get user data
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("userId", userOpt.get().getId());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}