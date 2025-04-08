package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.LoginResponseDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.UserService;
import com.example.projectgreenie.dto.PasswordResetRequestDTO;
import com.example.projectgreenie.dto.SetNewPasswordDTO;
import com.example.projectgreenie.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://test.greenie.dizzpy.dev") // or "*" for local testing
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, AuthService authService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    /**
     * User Registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * User Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        boolean authenticated = userService.authenticate(email, password);
        if (authenticated) {
            String token = jwtUtil.generateToken(email);
            
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

    /**
     * Request Password Reset (Send Reset Email / Print Token)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO) {
        String response = authService.sendPasswordResetLink(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Set New Password
     */
    @PostMapping("/set-new-password")
    public ResponseEntity<String> setNewPassword(@RequestBody SetNewPasswordDTO requestDTO) {
        String response = authService.resetPassword(requestDTO);
        return ResponseEntity.ok(response);
    }
}
