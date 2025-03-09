package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.repository.FeedPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedPostService {

    private final FeedPostRepository feedPostRepository;

    public FeedPostService(FeedPostRepository feedPostRepository) {
        this.feedPostRepository = feedPostRepository;
    }

    public List<PostResponseDTO> getAllPosts() {
        return feedPostRepository.findAll().stream().map(post -> {

            // Hardcoded user details (replace with real user data later)
            String fullName = "Janudi Dilakna";
            String username = "djdmeegoda";
            String profileImage = "https://example.com/profile.jpg";

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
                    .timestamp(post.getTimestamp())
                    .build();
        }).collect(Collectors.toList());
    }

}
