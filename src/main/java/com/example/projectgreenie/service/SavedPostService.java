package com.example.projectgreenie.service;

import com.example.projectgreenie.model.SavedPost;
import com.example.projectgreenie.repository.SavedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SavedPostService {

    @Autowired
    private SavedPostRepository savedPostRepository;

    // ✅ Save a post with custom savePostId
    public void savePost(String postId, String userId) {
        if (!savedPostRepository.existsByPostIdAndUserId(postId, userId)) {
            SavedPost saved = SavedPost.builder()
                    .savePostId(UUID.randomUUID().toString())  // ✅ Assign custom ID
                    .postId(postId)
                    .userId(userId)
                    .build();
            savedPostRepository.save(saved);
        }
    }

    public void unsavePost(String postId, String userId) {
        savedPostRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public List<String> getSavedPostIds(String userId) {
        return savedPostRepository.findByUserId(userId)
                .stream()
                .map(SavedPost::getPostId)
                .collect(Collectors.toList());
    }
}
