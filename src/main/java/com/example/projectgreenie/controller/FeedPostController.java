package com.example.projectgreenie.controller;

import com.example.projectgreenie.Dto.PostResponseDTO;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.service.AuthService;
import com.example.projectgreenie.service.FeedPostService;
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

@CrossOrigin(origins = {"http://localhost:5173", "https://test.greenie.dizzpy.dev"})
@RestController
@RequestMapping("/api/posts")
public class FeedPostController {

    @Autowired
    private FeedPostRepository feedPostRepository;

    @Autowired
    private AuthService authService; // ✅ Inject AuthService to get logged-in user

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("content") String content) {

        try {
            // ✅ Get actual logged-in user ID from JWT
            String userId = authService.getLoggedInUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // ✅ Validate file size
            if (imageFile.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size must not exceed 5MB");
            }

            // ✅ Validate image format
            if (!isValidImageFormat(imageFile)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid image format. Only JPG, PNG, and JPEG are allowed.");
            }

            // ✅ Compress and encode image to Base64
            String base64Image = compressAndConvertToBase64(imageFile);

            // ✅ Create a new post
            FeedPost post = new FeedPost();
            post.setPostId(UUID.randomUUID().toString());
            post.setUserId(userId); // ✅ Set actual logged-in user ID
            post.setContent(content);
            post.setImage(base64Image);
//          post.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(postTimestampLong), ZoneOffset.UTC));
            // ✅ Save to database
            feedPostRepository.save(post);

            return ResponseEntity.ok("Post created successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving post: " + e.getMessage());
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
    private final FeedPostService feedPostService;

    public FeedPostController(FeedPostService feedPostService) {
        this.feedPostService = feedPostService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(feedPostService.getAllPosts());
    }

}
