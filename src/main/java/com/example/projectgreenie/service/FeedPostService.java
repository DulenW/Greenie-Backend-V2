package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.CommentResponseDTO;
import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.Dto.UserDTO;
import com.example.projectgreenie.model.FeedPost;
//import com.example.projectgreenie.repository.CommentRepository;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedPostService {

    private final FeedPostRepository feedPostRepository;
    private final RestTemplate restTemplate;

    public FeedPostService(FeedPostRepository feedPostRepository, RestTemplate restTemplate) {
        this.feedPostRepository = feedPostRepository;
        this.restTemplate = restTemplate;
    }

    public List<PostResponseDTO> getAllPosts() {
        List<FeedPost> posts = feedPostRepository.findAll();
        String userApiUrl = "http://localhost:8080/api/users/"; // Change to actual backend URL

        return posts.stream().map(post -> {
            String fullName = "Unknown";
            String username = "Unknown";
            String profileImage = "";

            // Fetch user details using userId
            try {
                ResponseEntity<UserDTO> response = restTemplate.getForEntity(userApiUrl + post.getUserId(), UserDTO.class);
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
                    .likes(post.getLikes())
                    .commentIds(post.getCommentIds())
//                    .timestamp(post.getTimestamp())
                    .build();
        }).collect(Collectors.toList());
    }

}
