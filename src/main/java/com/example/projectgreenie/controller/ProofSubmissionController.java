package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.model.ProofSubmission;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.ChallengeRepository;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.repository.ProofSubmissionRepository;
import com.example.projectgreenie.repository.UserRepository;
import com.example.projectgreenie.service.OpenRouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proof")
@Slf4j
public class ProofSubmissionController {

    private final ProofSubmissionRepository repository;
    private final OpenRouterService aiService;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;




    @Autowired
    private FeedPostRepository feedPostRepository;

    public ProofSubmissionController(
            ProofSubmissionRepository repository,
            OpenRouterService aiService,
            UserRepository userRepository,
            ChallengeRepository challengeRepository
    ) {
        this.repository = repository;
        this.aiService = aiService;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitProof(@RequestBody ProofSubmission proof) {
        try {
            proof.setSubmittedAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

            if (proof.getImageUrl() == null || proof.getImageUrl().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing image URL");
            }
            if (proof.getDescription() == null || proof.getDescription().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing description");
            }

            // Call AI service
            String aiResult = aiService.checkImage(proof.getImageUrl(), proof.getDescription());
            String[] split = aiResult.split("\\|", 2);
            String status = split.length > 0 ? split[0].trim() : "Issue";
            String reason = split.length > 1 ? split[1].trim() : "No explanation.";

            proof.setStatus(status);
            proof.setAiResponse(reason);

            // Save proof to DB
            ProofSubmission saved = repository.save(proof);

            // ✅ If proof is verified, create a FeedPost
            if ("Verified".equalsIgnoreCase(status)) {
                String postId = generateUniquePostId();

                FeedPost newPost = FeedPost.builder()
                        .id(UUID.randomUUID().toString())
                        .postId(postId)
                        .userId(proof.getUserId())
                        .username(proof.getUsername()) // ✅ Save the username
                        .content(proof.getDescription())
                        .image(proof.getImageUrl())
                        .timestamp(LocalDateTime.now())
                        .likes(0)
                        .commentIds(new ArrayList<>())
                        .build();

                feedPostRepository.save(newPost);
            }

            // ➕ Add points if Verified
            if ("Verified".equalsIgnoreCase(status)) {
                Optional<User> userOpt = userRepository.findById(proof.getUserId());
                Optional<Challenge> challengeOpt = challengeRepository.findByChallengeId(
                        Integer.parseInt(proof.getChallengeID())
                );

                if (userOpt.isPresent() && challengeOpt.isPresent()) {
                    User user = userOpt.get();
                    Challenge challenge = challengeOpt.get();

                    user.setPointsCount(user.getPointsCount() + challenge.getPoints());
                    userRepository.save(user);
                }
            }

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Error in proof submission: ", e);
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }


    // ❌ Delete Proof (with point deduction)
    @DeleteMapping("/{proofID}")
    public ResponseEntity<?> deleteProof(@PathVariable String proofID) {
        Optional<ProofSubmission> proofOpt = repository.findById(proofID);
        if (proofOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Proof not found");
        }

        ProofSubmission proof = proofOpt.get();

        // ➖ Remove points if it was Verified
        if ("Verified".equalsIgnoreCase(proof.getStatus())) {
            Optional<User> userOpt = userRepository.findById(proof.getUserId());
            Optional<Challenge> challengeOpt = challengeRepository.findByChallengeId(
                    Integer.parseInt(proof.getChallengeID())
            );

            if (userOpt.isPresent() && challengeOpt.isPresent()) {
                User user = userOpt.get();
                Challenge challenge = challengeOpt.get();

                int updatedPoints = user.getPointsCount() - challenge.getPoints();
                user.setPointsCount(Math.max(updatedPoints, 0)); // Prevent negative values
                userRepository.save(user);
            }
        }

        repository.deleteById(proofID);
        return ResponseEntity.ok("Proof deleted and points adjusted if necessary.");
    }

    @GetMapping("/all")
    public List<ProofSubmission> getAllSubmissions() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProofSubmission> getSubmissionById(@PathVariable String id) {
        Optional<ProofSubmission> proof = repository.findById(id);
        return proof.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔁 Generate Unique Post ID
    private String generateUniquePostId() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseId = "POST-" + dateStr + "-";

        List<FeedPost> todaysPosts = feedPostRepository.findByPostIdStartingWith(baseId);
        int nextNum = 1;

        if (!todaysPosts.isEmpty()) {
            Set<Integer> usedNums = todaysPosts.stream()
                    .map(post -> {
                        try {
                            return Integer.parseInt(post.getPostId().substring(post.getPostId().lastIndexOf("-") + 1));
                        } catch (Exception e) {
                            return 0;
                        }
                    }).collect(Collectors.toSet());

            while (usedNums.contains(nextNum)) {
                nextNum++;
            }
        }

        return String.format("%s%03d", baseId, nextNum);
    }
}
