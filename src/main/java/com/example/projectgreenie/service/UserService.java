package com.example.projectgreenie.service;


import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Encrypts passwords

    public User registerUser(User user) { // Return type changed to your model User
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // Generate a unique user ID
        user.setId(UUID.randomUUID().toString());

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values for profile fields
        user.setUsername(""); // Empty username by default
        user.setBio(""); // Empty bio
        user.setPostList(List.of()); // Empty post list
        user.setProfileImgUrl("https://yourcdn.com/profiles/default.jpg"); // Default profile image
        user.setPointsCount(0); // Initial points count
        user.setBadgesList(List.of()); // Empty badge list
        user.setJoinedChallenges(List.of()); // Empty challenge list

        return userRepository.save(user);
    }

    public boolean authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return passwordEncoder.matches(password, userOptional.get().getPassword());
        }
        return false;
    }
}