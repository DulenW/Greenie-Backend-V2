package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.ProfileUpdateDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> {
                    // Remove sensitive information
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Remove passwords from response
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/points")
    public ResponseEntity<?> getUserPoints(@PathVariable String userId) {
        return userService.getUserPoints(userId)
                .map(points -> ResponseEntity.ok(Map.of("points", points)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserProfile(
            @RequestParam("userData") String userDataJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication) {

        try {
            // Update the user profile using the service
            User updatedUser = userService.updateUserProfile(userDataJson, profileImage, authentication);

            // Remove sensitive information
            updatedUser.setPassword(null);

            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }
}