package com.example.projectgreenie.service;

import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.repository.UserRepository;
import com.example.projectgreenie.dto.PasswordResetRequestDTO;
import com.example.projectgreenie.dto.SetNewPasswordDTO;
import com.example.projectgreenie.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

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
     * Sends a password reset link via email.
     */
    public String sendPasswordResetLink(PasswordResetRequestDTO requestDTO) {
        Optional<User> userOpt = userRepository.findByEmail(requestDTO.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("No account found with this email.");
        }

        User user = userOpt.get();
        String resetToken = jwtUtil.generateToken(user.getEmail()); // Generate JWT token

        // TODO: Implement actual email sending with resetToken
        System.out.println("Password reset link: http://localhost:3000/reset-password?token=" + resetToken);

        return "Password reset link sent!";
    }

    /**
     * Validates the reset token and updates the password.
     */
    public String resetPassword(SetNewPasswordDTO requestDTO) {
        String email = jwtUtil.extractEmail(requestDTO.getToken());

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid token or user not found.");
        }

        User user = userOpt.get();

        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        return "Password has been changed successfully!";
    }
}
