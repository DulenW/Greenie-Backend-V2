package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.CommentResponseDTO;
import com.example.projectgreenie.model.Comment;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public CommentResponseDTO createComment(String postId, String userId, String commentText) {
        // Fetch user details
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();

        // Create and save the comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setComment(commentText);
//        comment.setTimestamp(LocalDateTime.now()); // Using LocalDateTime for timestamp

        // Save comment
        Comment savedComment = commentRepository.save(comment);

        // Create a response DTO with user details
        CommentResponseDTO response = new CommentResponseDTO();
        response.setCommentId(savedComment.getId());
        response.setPostId(savedComment.getPostId());
        response.setComment(savedComment.getComment());
        response.setUserId(savedComment.getUserId());
        response.setUser(user);  // Add user details to response
//        response.setTimestamp(savedComment.getTimestamp());  // Use LocalDateTime directly

        return response;
    }
}
