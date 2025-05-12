package com.example.projectgreenie.service;

import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.UserRepository;
import com.example.projectgreenie.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // In-memory OTP storage
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    /**
     * Retrieves the logged-in user's ID from the JWT token.
     */
    public String getLoggedInUserId() {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                return userRepository.findByEmail(email)
                        .map(User::getId)
                        .orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends an OTP to the user's email for password reset.
     */
    public String sendOtpToEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("No user found with this email.");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
        return "OTP sent successfully!";
    }

    /**
     * Verifies the OTP.
     */
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

    /**
     * Updates password if OTP is verified.
     */
    public String updatePasswordWithOtp(String email, String otp, String newPassword, String confirmPassword) {
        if (!verifyOtp(email, otp)) {
            throw new RuntimeException("Invalid OTP.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email); // Invalidate OTP after use
        return "Password successfully updated.";
    }
}
