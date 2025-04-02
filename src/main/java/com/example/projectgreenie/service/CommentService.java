package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.CommentResponseDTO;

import com.example.projectgreenie.model.Comment;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.CommentRepository;
import com.example.projectgreenie.repository.FeedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate; // Inject MongoTemplate

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, MongoTemplate mongoTemplate) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate; // Assign MongoTemplate
    }

    public CommentResponseDTO createComment(String postId, String userId, String commentText) {
        // Fetch user details
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();

        // Create and save the comment
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .comment(commentText)
                .commentId("CMT-" + UUID.randomUUID().toString().substring(0, 8)) // Ensure commentId is set
                .timestamp(LocalDateTime.now()) // Ensure timestamp is set
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Debugging: Check if values are set
        System.out.println("Saved Comment ID: " + savedComment.getCommentId());
        System.out.println("Saved Comment Post ID: " + savedComment.getPostId());
        System.out.println("Saved Comment Text: " + savedComment.getComment());
        System.out.println("Saved Comment User ID: " + savedComment.getUserId());
        System.out.println("Saved Comment Timestamp: " + savedComment.getTimestamp());

        // Update post with new commentId
        addCommentToPost(postId, savedComment.getCommentId());

        // Return response correctly
        return CommentResponseDTO.builder()
                .commentId(savedComment.getCommentId())
                .postId(savedComment.getPostId())
                .comment(savedComment.getComment())
                .userId(savedComment.getUserId())
                .user(user)
                .timestamp(savedComment.getTimestamp())
                .build();
    }


    private void addCommentToPost(String postId, String commentId) {
        Query query = new Query(Criteria.where("postId").is(postId)); // Find post by custom postId
        Update update = new Update().push("commentIds", commentId); // Push comment ID to commentIds array
        mongoTemplate.updateFirst(query, update, "feedPost"); // Update feedPost collection
    }

    public List<CommentResponseDTO> getCommentsByPostId(String postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        return comments.stream().map(comment -> {
            CommentResponseDTO response = new CommentResponseDTO();
            response.setCommentId(comment.getCommentId()); // Use custom commentId
            response.setPostId(comment.getPostId());
            response.setComment(comment.getComment());
            response.setUserId(comment.getUserId());
//            response.setTimestamp(comment.getTimestamp());

            // Fetch user details
            userService.getUserById(comment.getUserId()).ifPresent(response::setUser);

            return response;
        }).collect(Collectors.toList());
    }


    public void deleteComment(String postId, String commentId) {
        Optional<Comment> commentOpt = commentRepository.findByCommentId(commentId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }

        Comment comment = commentOpt.get();

        // Ensure the comment belongs to the correct post
        if (!comment.getPostId().equals(postId)) {
            throw new RuntimeException("This comment does not belong to the specified post");
        }

        // Delete the comment
        commentRepository.deleteByCommentId(commentId);

        // Optionally: Remove commentId from the feedPost's commentIds array
        Query query = new Query(Criteria.where("postId").is(postId)); // Find the post by postId
        Update update = new Update().pull("commentIds", commentId); // Remove commentId from the array
        mongoTemplate.updateFirst(query, update, "feedPost"); // Update feedPost collection
    }


}
