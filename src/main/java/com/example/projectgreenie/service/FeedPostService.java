package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.dto.UserDTO;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        List<FeedPost> posts = feedPostRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());

        return posts.stream()
                .map(this::convertToPostResponseDTO)
                .collect(Collectors.toList());
    }

    public PostResponseDTO convertToPostResponseDTO(FeedPost post) {
        String userApiUrl = "http://localhost:8080/api/users/";

        String fullName = "Unknown";
        String username = "Unknown";
        String profileImage = "";

        try {
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                    userApiUrl + post.getUserId(),
                    UserDTO.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                UserDTO user = response.getBody();
                fullName = user.getFullName();
                username = user.getUsername();
                profileImage = user.getProfileImage();
            }
        } catch (Exception e) {
            System.out.println("Error fetching user details for userId: " + post.getUserId());
        }

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .fullName(fullName)
                .username(username)
                .profileImage(profileImage)
                .description(post.getContent())
                .image(post.getImage())
                .likes(
                        post.getReactions() != null
                                ? post.getReactions().values().stream().mapToInt(List::size).sum()
                                : 0
                )
                .commentIds(post.getCommentIds())
                .reactions(post.getReactions()) // ✅ send reactions to frontend
                .build();

    }
}
