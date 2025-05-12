package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.LoginResponseDTO;
import com.example.projectgreenie.dto.OtpRequestDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.AuthService;
import com.example.projectgreenie.service.UserService;
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
     * OTP-based: Send OTP to Email
     */
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String response = authService.sendOtpToEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * OTP-based: Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@RequestBody OtpRequestDTO dto) {
        boolean isValid = authService.verifyOtp(dto.getEmail(), dto.getOtp());
        return ResponseEntity.ok(isValid);
    }

    /**
     * OTP-based: Reset Password using OTP
     */
    @PostMapping("/update-password-with-otp")
    public ResponseEntity<String> updatePasswordWithOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        String message = authService.updatePasswordWithOtp(email, otp, newPassword, confirmPassword);
        return ResponseEntity.ok(message);
    }
}
