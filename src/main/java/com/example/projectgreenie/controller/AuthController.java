package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.User;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.UserService;
import com.example.projectgreenie.repository.UserRepository;

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
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }
            User user = optionalUser.get();

            String token = jwtUtil.generateToken(email);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("_id", user.getId());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        boolean authenticated = userService.authenticate(email, password);
        if (authenticated) {
            Optional<User> optionalAdmin = userRepository.findByEmail(email);
            if (optionalAdmin.isEmpty() || !"ADMIN".equals(optionalAdmin.get().getRole())) {
                return ResponseEntity.status(403).body("Unauthorized");
            }
            User admin = optionalAdmin.get();

            String token = jwtUtil.generateToken(email);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", "ADMIN");
            response.put("_id", admin.getId());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
