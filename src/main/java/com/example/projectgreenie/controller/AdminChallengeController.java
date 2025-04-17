package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/challenges")
@CrossOrigin(origins = "https://test.greenie.dizzpy.dev")
public class AdminChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/create")
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        challenge.setStatus("active"); // Admin created = active
        Challenge newChallenge = challengeService.addChallenge(challenge);
        return ResponseEntity.ok(newChallenge);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("challengeId") String challengeId) {
        Challenge challenge = challengeService.getChallengeById(Integer.parseInt(challengeId));
        return ResponseEntity.ok(challenge);
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable String challengeId, @RequestBody Challenge updatedChallenge) {
        Challenge challenge = challengeService.updateChallenge(Integer.parseInt(challengeId), updatedChallenge);
        return ResponseEntity.ok(challenge);
    }

    // ✅ Updated route
    @DeleteMapping("/delete/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable String challengeId) {
        boolean deleted = challengeService.deleteChallenge(Integer.parseInt(challengeId));
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ✅ Approve a pending challenge
    @PutMapping("/approve/{challengeId}")
    public ResponseEntity<Challenge> approveChallenge(@PathVariable String challengeId) {
        Challenge approvedChallenge = challengeService.approveChallenge(Integer.parseInt(challengeId));
        return ResponseEntity.ok(approvedChallenge);
    }

    // ✅ Filter challenges by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Challenge>> getChallengesByStatus(@PathVariable String status) {
        List<Challenge> challenges = challengeService.getChallengesByStatus(status);
        return ResponseEntity.ok(challenges);
    }
}
