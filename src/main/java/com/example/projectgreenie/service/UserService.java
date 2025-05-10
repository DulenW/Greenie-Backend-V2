package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.ProfileUpdateDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload.directory:uploads/profiles}")
    private String uploadDirectory;

    @Autowired
    private ObjectMapper objectMapper;

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
        // Use a relative URL for the default profile image
        user.setProfileImgUrl("/uploads/profiles/default.jpg"); // Default profile image
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

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Integer> getUserPoints(String userId) {
        return userRepository.findById(userId)
                .map(User::getPointsCount);
    }

    public boolean updateUserPoints(String userId, int newPoints) {
        return getUserById(userId).map(user -> {
            user.setPointsCount(newPoints);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public User updateUserProfile(String userDataJson, MultipartFile profileImage, MultipartFile coverImage, Authentication authentication) throws IOException {
        ProfileUpdateDTO profileUpdateDTO = objectMapper.readValue(userDataJson, ProfileUpdateDTO.class);

        // Get authenticated user (by email or username)
        String identity = authentication.getName();
        Optional<User> userOpt = getUserByEmail(identity);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(identity);
        }

        User user = userOpt.orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Username validation
        if (profileUpdateDTO.getUsername() != null && !profileUpdateDTO.getUsername().equals(user.getUsername())) {
            if (profileUpdateDTO.getUsername().contains(" ")) {
                throw new IllegalArgumentException("Username cannot contain spaces");
            }
            if (!profileUpdateDTO.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
                throw new IllegalArgumentException("Username must be 3-20 characters (letters, numbers, underscores)");
            }
            boolean usernameExists = userRepository.findAll().stream()
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .anyMatch(existingUser -> profileUpdateDTO.getUsername().equalsIgnoreCase(existingUser.getUsername()));
            if (usernameExists) {
                throw new IllegalArgumentException("Username is already taken");
            }
        }

        // Update fields
        if (profileUpdateDTO.getFullName() != null && !profileUpdateDTO.getFullName().trim().isEmpty()) {
            user.setFullName(profileUpdateDTO.getFullName());
        }

        if (profileUpdateDTO.getUsername() != null && !profileUpdateDTO.getUsername().trim().isEmpty()) {
            user.setUsername(profileUpdateDTO.getUsername());
        }

        if (profileUpdateDTO.getBio() != null) {
            user.setBio(profileUpdateDTO.getBio());
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String base64Image = encodeImageToBase64(profileImage);
            user.setProfileImgUrl("data:" + profileImage.getContentType() + ";base64," + base64Image);
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            String base64Cover = encodeImageToBase64(coverImage);
            String base64CoverUrl = "data:" + coverImage.getContentType() + ";base64," + base64Cover;
            user.setCoverImgUrl(base64CoverUrl); // ← this line must be here
        }


        return userRepository.save(user);
    }



    /**
     * Saves a profile image to the server and returns the URL path to access it.
     *
     * @param file The MultipartFile containing the image data
     * @param userId The ID of the user who owns this profile image
     * @return A relative URL path to the saved image
     * @throws IOException If there is an error saving the file
     */
    private String saveProfileImage(MultipartFile file, String userId) throws IOException {
        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique filename for the image
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueId = UUID.randomUUID().toString();
        String fileName = uniqueId + "-" + userId + "_" + System.currentTimeMillis() + fileExtension;

        // Save the file to the server
        Path targetLocation = Paths.get(uploadDirectory).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return a relative URL path instead of absolute URL
        return "http://localhost:8080/uploads/profiles/" + fileName; // use env variable in production
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return ".jpg";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return ".jpg";
        }
        return fileName.substring(lastDotIndex);
    }

    private String encodeImageToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

}