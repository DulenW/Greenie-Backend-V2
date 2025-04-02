package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.CommentResponseDTO;
import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.model.Comment;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.service.AuthService;
import com.example.projectgreenie.service.CommentService;
import com.example.projectgreenie.service.FeedPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;

import com.example.projectgreenie.dto.CreatePostRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts")
@Slf4j
public class FeedPostController {

    @Autowired
    private FeedPostRepository feedPostRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private FeedPostService feedPostService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private String generateUniquePostId() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseId = "POST-" + dateStr + "-";

        // Get all posts for today
        List<FeedPost> todaysPosts = feedPostRepository.findByPostIdStartingWith(baseId);

        // Find next available number
        int nextNum = 1;
        if (!todaysPosts.isEmpty()) {
            Set<Integer> usedNums = todaysPosts.stream()
                    .map(post -> Integer.parseInt(post.getPostId().substring(post.getPostId().lastIndexOf("-") + 1)))
                    .collect(Collectors.toSet());

            while (usedNums.contains(nextNum)) {
                nextNum++;
            }
        }

        return String.format("%s%03d", baseId, nextNum);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("userId") String userId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam("content") String content) {

        try {
            // Generate unique post ID
            String postId = generateUniquePostId();

            // Verify uniqueness (double-check)
            while (feedPostRepository.existsByPostId(postId)) {
                postId = generateUniquePostId();
            }

            log.info("Creating post with ID: {} for user: {}", postId, userId);

            // Process image if provided
            String base64Image = null;
            if (image != null && !image.isEmpty()) {
                if (image.getSize() > MAX_FILE_SIZE) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "File size must not exceed 5MB"));
                }
                base64Image = compressAndConvertToBase64(image);
            }

            FeedPost post = FeedPost.builder()
                    .id(UUID.randomUUID().toString())
                    .postId(postId)
                    .userId(userId)
                    .content(content)
                    .image(base64Image)
                    .timestamp(LocalDateTime.now())
                    .likes(0)
                    .commentIds(new ArrayList<>())
                    .build();

            FeedPost savedPost = feedPostRepository.save(post);

            return ResponseEntity.ok(Map.of(
                    "postId", savedPost.getPostId(),
                    "content", savedPost.getContent(),
                    "imageUrl", savedPost.getImage(),
                    "createdAt", savedPost.getTimestamp()
            ));

        } catch (Exception e) {
            log.error("Error creating post: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error creating post: " + e.getMessage()));
        }
    }

    private boolean isValidImageFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"));
    }

    private String compressAndConvertToBase64(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int targetWidth = 600;
        int targetHeight = (originalImage.getHeight() * targetWidth) / originalImage.getWidth();

        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage compressedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        compressedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpg", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }


    // API for Like a post
    @PutMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId); // Find by custom postId

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        FeedPost post = postOpt.get();
        post.setLikes(post.getLikes() + 1);
        feedPostRepository.save(post);

        return ResponseEntity.ok("Post liked successfully");
    }

    // API for unlike
    @PutMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId); // Find by custom postId

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        FeedPost post = postOpt.get();

        // Ensure like count does not go below zero
        if (post.getLikes() > 0) {
            post.setLikes(post.getLikes() - 1);
            feedPostRepository.save(post);
            return ResponseEntity.ok("Post unliked successfully");
        }

        return ResponseEntity.ok("Post already has 0 likes");
    }

    // API for get all like count
    @GetMapping("/{postId}/likes/all")
    public ResponseEntity<?> getLikeCount(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId); // Find by custom postId

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        int likeCount = postOpt.get().getLikes(); // Get like count
        return ResponseEntity.ok(likeCount); // Return like count
    }


    // API for Get All Posts API
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(feedPostService.getAllPosts());
    }


    // Comments APIs
    //Create Comment
    private final CommentService commentService;

    @Autowired
    public FeedPostController(CommentService commentService) {
        this.commentService = commentService;
    }

    // API for creating a comment
    @PostMapping("/{postId}/comments/create")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable String postId,
            @RequestHeader("userId") String userId, // Get logged-in user ID from the header
            @RequestBody String commentText) {  // The comment body

        // Call the service to create the comment
        CommentResponseDTO newComment = commentService.createComment(postId, userId, commentText);
        return ResponseEntity.ok(newComment);  // Return the CommentResponseDTO
    }

    // API for getting all comments
    @GetMapping("/{postId}/comments/all")
    public List<CommentResponseDTO> getComments(@PathVariable String postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // API for getting comments count
    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<?> getCommentCount(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId); // Find by custom postId

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        int commentCount = postOpt.get().getCommentIds().size(); // Get comment count
        return ResponseEntity.ok(commentCount); // Return count
    }

    // API for delete comment
    @DeleteMapping("/{postId}/{commentId}/comments/delete")
    public ResponseEntity<?> deleteComment(@PathVariable("postId") String postId,
                                           @PathVariable("commentId") String commentId) {
        try {
            commentService.deleteComment(postId, commentId); // Pass both postId and commentId
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


}