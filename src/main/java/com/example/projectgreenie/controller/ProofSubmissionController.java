package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.ProofSubmission;
import com.example.projectgreenie.repository.ProofSubmissionRepository;
import com.example.projectgreenie.service.OpenRouterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proof")
public class ProofSubmissionController {
    private final ProofSubmissionRepository repository;
    private final OpenRouterService aiService;

    public ProofSubmissionController(ProofSubmissionRepository repository, OpenRouterService aiService) {
        this.repository = repository;
        this.aiService = aiService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitProof(@RequestBody ProofSubmission proof) {
        try {
            proof.setSubmittedAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

            // Check if imageUrl or description is missing
            if (proof.getImageUrl() == null || proof.getImageUrl().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing image URL");
            }
            if (proof.getDescription() == null || proof.getDescription().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing description");
            }

            // Get AI response
            String aiResponse = aiService.checkImage(proof.getImageUrl(), proof.getDescription());
            proof.setAiResponse(aiResponse);

            // Determine status based on AI response
            if (aiResponse.toLowerCase().contains("fake") || aiResponse.toLowerCase().contains("ai-generated")) {
                proof.setStatus("Issue");
            } else if (aiResponse.toLowerCase().contains("real") || aiResponse.toLowerCase().contains("genuine")) {
                proof.setStatus("Verified");
            } else {
                proof.setStatus("Issue");
            }

            ProofSubmission savedProof = repository.save(proof);
            return ResponseEntity.ok(savedProof);
        } catch (Exception e) {
            e.printStackTrace(); // Log it to console
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }


    // Get all Submissions
    @GetMapping("/all")
    public List<ProofSubmission> getAllSubmissions() {
        return repository.findAll();
    }

    // Get Submission by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProofSubmission> getSubmissionById(@PathVariable String id) {
        Optional<ProofSubmission> proof = repository.findById(id);
        return proof.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
