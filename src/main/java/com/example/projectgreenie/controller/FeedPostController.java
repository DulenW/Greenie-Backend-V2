package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.PostResponseDTO;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.service.AuthService;
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
import java.util.Base64;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import com.example.projectgreenie.dto.CreatePostRequest;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "https://test.greenie.dizzpy.dev"})
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


    // Like a post
    @PutMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable("postId") String postId) {
        FeedPost post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        feedPostRepository.save(post);
        return ResponseEntity.ok("Post liked successfully");
    }


    //Get All Posts API
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(feedPostService.getAllPosts());
    }

}
