package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.ProofSubmission;
import com.example.projectgreenie.service.ProofSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/proof")
@CrossOrigin(origins = "https://test.greenie.dizzpy.dev")
public class AdminProofController {

    @Autowired
    private ProofSubmissionService service;

    // Get all proof submissions
    @GetMapping("/all")
    public List<ProofSubmission> getAllSubmissions() {
        return service.getAllSubmissions();
    }

    // Get a specific proof submission by ID
    @GetMapping("/{proofID}")
    public Optional<ProofSubmission> getSubmissionById(@PathVariable String proofID) {
        return service.getSubmissionById(proofID);
    }

    // Update proof submission status
    @PutMapping("/{proofID}/status")
    public ProofSubmission updateStatus(@PathVariable String proofID, @RequestParam String status) {
        return service.updateStatus(proofID, status);
    }

    // DELETE Proof Submission by ID
    @DeleteMapping("/{proofID}")
    public ResponseEntity<String> deleteProofSubmission(@PathVariable String proofID) {
        boolean deleted = service.deleteProofSubmission(proofID);

        if (deleted) {
            return ResponseEntity.ok("Proof Submission with ID " + proofID + " has been deleted.");
        } else {
            return ResponseEntity.status(404).body("Proof Submission not found.");
        }
    }
}