package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.SavedPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SavedPostRepository extends MongoRepository<SavedPost, String> {
    boolean existsByPostIdAndUserId(String postId, String userId);

    List<SavedPost> findByUserId(String userId);

    void deleteByPostIdAndUserId(String postId, String userId);
}

