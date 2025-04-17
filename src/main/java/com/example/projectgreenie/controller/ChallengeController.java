package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/create")
    public ResponseEntity<Challenge> addChallenge(@RequestBody Challenge challenge) {
        Challenge newChallenge = challengeService.addChallenge(challenge);
        return ResponseEntity.ok(newChallenge);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("challengeId") int challengeId) {
        Challenge challenge = challengeService.getChallengeById(challengeId);
        return ResponseEntity.ok(challenge);
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable int challengeId, @RequestBody Challenge updatedChallenge) {
        Challenge challenge = challengeService.updateChallenge(challengeId, updatedChallenge);
        return challenge != null ? ResponseEntity.ok(challenge) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable int challengeId) {
        boolean deleted = challengeService.deleteChallenge(challengeId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ✅ Get challenges by status (e.g., pending, active)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Challenge>> getChallengesByStatus(@PathVariable String status) {
        List<Challenge> challenges = challengeService.getChallengesByStatus(status);
        return ResponseEntity.ok(challenges);
    }
}
