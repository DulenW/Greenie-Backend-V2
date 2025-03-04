package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")  // Allow frontend access
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    // Add a new challenge
    @PostMapping
    public ResponseEntity<Challenge> addChallenge(@RequestBody Challenge challenge) {
        Challenge newChallenge = challengeService.addChallenge(challenge);
        return ResponseEntity.ok(newChallenge);
    }

    // Get all challenges
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    // Get challenge by ID
    @GetMapping("/{challengeId}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("challengeId") int challengeId) {
        Challenge challenge = challengeService.getChallengeById(challengeId);
        return ResponseEntity.ok(challenge);
    }

    // Update challenge
    @PutMapping("/{challengeId}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable int challengeId, @RequestBody Challenge updatedChallenge) {
        Challenge challenge = challengeService.updateChallenge(challengeId, updatedChallenge);
        return challenge != null ? ResponseEntity.ok(challenge) : ResponseEntity.notFound().build();
    }

    // Delete challenge
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable int challengeId) {
        boolean deleted = challengeService.deleteChallenge(challengeId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

