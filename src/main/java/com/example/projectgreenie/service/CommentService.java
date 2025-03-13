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
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();

        // Create and save the comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setComment(commentText);

        // Save comment
        Comment savedComment = commentRepository.save(comment);

        // Update post by adding the commentId to its commentIds array
        addCommentToPost(postId, savedComment.getId());

        // Create a response DTO with user details
        CommentResponseDTO response = new CommentResponseDTO();
        response.setCommentId(savedComment.getId());
        response.setPostId(savedComment.getPostId());
        response.setComment(savedComment.getComment());
        response.setUserId(savedComment.getUserId());
        response.setUser(user);

        return response;
    }

    private void addCommentToPost(String postId, String commentId) {
        Query query = new Query(Criteria.where("postId").is(postId)); // Find post by custom postId
        Update update = new Update().push("commentIds", commentId); // Push comment ID to commentIds array
        mongoTemplate.updateFirst(query, update, "feedPost"); // Update feedPost collection
    }

    public List<CommentResponseDTO> getCommentsByPostId(String postId) {
        // Find all comments where postId matches
        Query query = new Query(Criteria.where("postId").is(postId));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        // Convert Comment objects to CommentResponseDTO list
        return comments.stream().map(comment -> {
            CommentResponseDTO response = new CommentResponseDTO();
            response.setCommentId(comment.getId());
            response.setPostId(comment.getPostId());
            response.setComment(comment.getComment());
            response.setUserId(comment.getUserId());
//            response.setTimestamp(comment.getTimestamp());

            // Fetch user details
            Optional<User> userOpt = userService.getUserById(comment.getUserId());
            userOpt.ifPresent(response::setUser);

            return response;
        }).collect(Collectors.toList());

        }
    }
