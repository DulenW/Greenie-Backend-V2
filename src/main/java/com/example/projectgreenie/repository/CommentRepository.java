package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);

    Optional<Comment> findByCommentId(String commentId);  // Custom method to find by commentId
    void deleteByCommentId(String commentId);  // Custom method to delete by commentId
}