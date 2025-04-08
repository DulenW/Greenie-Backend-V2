package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.dto.UserDTO;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedPostService {

    private final FeedPostRepository feedPostRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public FeedPostService(FeedPostRepository feedPostRepository, RestTemplate restTemplate) {
        this.feedPostRepository = feedPostRepository;
        this.restTemplate = restTemplate;
    }

    public List<PostResponseDTO> getAllPosts() {
        String userApiUrl = "http://localhost:8080/api/users/";

        // Fetch and sort posts by timestamp DESC (newest first)
        List<FeedPost> posts = feedPostRepository.findAll().stream()
                .sorted(Comparator.comparing(FeedPost::getTimestamp).reversed())
                .collect(Collectors.toList());

        return posts.stream().map(post -> {
            String fullName = "Unknown";
            String username = "anonymous";
            String profileImage = "https://via.placeholder.com/150";

            try {
                System.out.println("🔎 Fetching user for post: " + post.getPostId() + " → userId: " + post.getUserId());

                ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                        userApiUrl + post.getUserId(),
                        UserDTO.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    UserDTO user = response.getBody();
                    fullName = user.getFullName() != null ? user.getFullName().trim() : fullName;
                    username = user.getUsername() != null && !user.getUsername().trim().isEmpty()
                            ? user.getUsername().trim()
                            : user.getEmail(); // fallback to email

                    profileImage = user.getProfileImage() != null ? user.getProfileImage() : profileImage;
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to fetch user for userId: " + post.getUserId() + " → " + e.getMessage());
            }

            return PostResponseDTO.builder()
                    .postId(post.getPostId())
                    .userId(post.getUserId())
                    .fullName(fullName)
                    .username(username)
                    .profileImage(profileImage)
                    .content(post.getContent())
                    .image(post.getImage())
                    .likes(post.getLikes())
                    .commentIds(post.getCommentIds())
                    .commentCount(post.getCommentIds() != null ? post.getCommentIds().size() : 0)
                    .build();
        }).collect(Collectors.toList());
    }
}
