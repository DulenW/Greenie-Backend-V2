package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.CommentResponseDTO;
import com.example.projectgreenie.dto.CommentUserDTO;
import com.example.projectgreenie.model.Comment;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.CommentRepository;
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
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, MongoTemplate mongoTemplate) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
    }

    public CommentResponseDTO createComment(String postId, String userId, String commentText) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .comment(commentText)
                .commentId("CMT-" + UUID.randomUUID().toString().substring(0, 8))
                .timestamp(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        addCommentToPost(postId, savedComment.getCommentId());

        return CommentResponseDTO.builder()
                .commentId(savedComment.getCommentId())
                .postId(savedComment.getPostId())
                .comment(savedComment.getComment())
                .userId(savedComment.getUserId())
                .user(CommentUserDTO.builder()
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .profileImage(user.getProfileImgUrl())
                        .build())
                .timestamp(savedComment.getTimestamp())
                .build();
    }

    public List<CommentResponseDTO> getCommentsByPostId(String postId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        return comments.stream().map(comment -> {
            Optional<User> userOpt = userService.getUserById(comment.getUserId());

            CommentUserDTO userDto = userOpt.map(user -> CommentUserDTO.builder()
                    .fullName(user.getFullName())
                    .username(user.getUsername())
                    .profileImage(user.getProfileImgUrl())
                    .build()).orElse(null);

            return CommentResponseDTO.builder()
                    .commentId(comment.getCommentId())
                    .postId(comment.getPostId())
                    .comment(comment.getComment())
                    .userId(comment.getUserId())
                    .user(userDto)
                    .timestamp(comment.getTimestamp())
                    .build();
        }).collect(Collectors.toList());
    }

    public void deleteComment(String postId, String commentId) {
        Optional<Comment> commentOpt = commentRepository.findByCommentId(commentId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }

        Comment comment = commentOpt.get();

        if (!comment.getPostId().equals(postId)) {
            throw new RuntimeException("This comment does not belong to the specified post");
        }

        commentRepository.deleteByCommentId(commentId);

        Query query = new Query(Criteria.where("postId").is(postId));
        Update update = new Update().pull("commentIds", commentId);
        mongoTemplate.updateFirst(query, update, "feedPost");
    }

    private void addCommentToPost(String postId, String commentId) {
        Query query = new Query(Criteria.where("postId").is(postId));
        Update update = new Update().push("commentIds", commentId);
        mongoTemplate.updateFirst(query, update, "feedPost");
    }
}
