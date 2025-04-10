package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.CommentResponseDTO;
import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.dto.UserDTO;
import com.example.projectgreenie.dto.FeedUpdateMessage;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.service.AuthService;
import com.example.projectgreenie.service.CommentService;
import com.example.projectgreenie.service.FeedPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;

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

        List<FeedPost> todaysPosts = feedPostRepository.findByPostIdStartingWith(baseId);

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
            String postId = generateUniquePostId();

            while (feedPostRepository.existsByPostId(postId)) {
                postId = generateUniquePostId();
            }

            log.info("Creating post with ID: {} for user: {}", postId, userId);

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

            PostResponseDTO postResponseDTO = feedPostService.convertToPostResponseDTO(savedPost);
            FeedUpdateMessage updateMessage = new FeedUpdateMessage("post", postResponseDTO);
            messagingTemplate.convertAndSend("/topic/feed", updateMessage);

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

    @PutMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId);

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        FeedPost post = postOpt.get();
        post.setLikes(post.getLikes() + 1);
        feedPostRepository.save(post);

        return ResponseEntity.ok("Post liked successfully");
    }

    @PutMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId);

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        FeedPost post = postOpt.get();

        if (post.getLikes() > 0) {
            post.setLikes(post.getLikes() - 1);
            feedPostRepository.save(post);
            return ResponseEntity.ok("Post unliked successfully");
        }

        return ResponseEntity.ok("Post already has 0 likes");
    }

    @GetMapping("/{postId}/likes/all")
    public ResponseEntity<?> getLikeCount(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId);

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        int likeCount = postOpt.get().getLikes();
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(feedPostService.getAllPosts());
    }

    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FeedPostController(CommentService commentService, SimpMessagingTemplate messagingTemplate) {
        this.commentService = commentService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/{postId}/comments/create")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable String postId,
            @RequestHeader("userId") String userId,
            @RequestBody String commentText) {

        CommentResponseDTO newComment = commentService.createComment(postId, userId, commentText);
        return ResponseEntity.ok(newComment);
    }

    @GetMapping("/{postId}/comments/all")
    public List<CommentResponseDTO> getComments(@PathVariable String postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<?> getCommentCount(@PathVariable("postId") String postId) {
        Optional<FeedPost> postOpt = feedPostRepository.findByPostId(postId);

        if (postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        int commentCount = postOpt.get().getCommentIds().size();
        return ResponseEntity.ok(commentCount);
    }

    @DeleteMapping("/{postId}/{commentId}/comments/delete")
    public ResponseEntity<?> deleteComment(@PathVariable("postId") String postId,
                                           @PathVariable("commentId") String commentId) {
        try {
            commentService.deleteComment(postId, commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/user-details/{userId}")
    public ResponseEntity<?> getUserDetailsByUserId(@PathVariable String userId) {
        try {
            String userApiUrl = "http://localhost:8080/api/users/" + userId;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(userApiUrl, UserDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch user details: " + e.getMessage());
        }
    }
}
