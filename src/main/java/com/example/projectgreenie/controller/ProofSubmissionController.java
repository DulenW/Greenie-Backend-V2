// === ProofSubmissionController.java ===
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

            if (proof.getImageUrl() == null || proof.getImageUrl().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing image URL");
            }
            if (proof.getDescription() == null || proof.getDescription().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing description");
            }

            // Call AI service to check the image
            String aiResult = aiService.checkImage(proof.getImageUrl(), proof.getDescription());

            String[] split = aiResult.split("\\|", 2);
            String status = split.length > 0 ? split[0].trim() : "Issue";
            String reason = split.length > 1 ? split[1].trim() : "No explanation.";

            proof.setStatus(status);
            proof.setAiResponse(reason);

            ProofSubmission saved = repository.save(proof);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
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
}
