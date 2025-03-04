package com.example.projectgreenie.service;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    // Auto-generate Challenge ID
    private int generateChallengeId() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.isEmpty() ? 1 : challenges.size() + 1;
    }

    public Challenge addChallenge(Challenge challenge) {
        challenge.setChallengeId(generateChallengeId());
        Challenge savedChallenge = challengeRepository.save(challenge);
        System.out.println("Challenge saved: " + savedChallenge);  // Debugging log
        return savedChallenge;
    }


    // Get all challenges
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    // Get challenge by ID
    public Challenge getChallengeById(int challengeId) {
        return challengeRepository.findByChallengeId(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    // Update challenge
    public Challenge updateChallenge(int challengeId, Challenge updatedChallenge) {
        Optional<Challenge> existingChallenge = challengeRepository.findByChallengeId(challengeId);
        if (existingChallenge.isPresent()) {
            Challenge challenge = existingChallenge.get();
            challenge.setChallengeName(updatedChallenge.getChallengeName());
            challenge.setPoints(updatedChallenge.getPoints());
            challenge.setDescription(updatedChallenge.getDescription());
            challenge.setPhotoUrl(updatedChallenge.getPhotoUrl());
            return challengeRepository.save(challenge);
        }
        return null;
    }

    // Delete challenge
    public boolean deleteChallenge(int challengeId) {
        Optional<Challenge> challenge = challengeRepository.findByChallengeId(challengeId);
        if (challenge.isPresent()) {
            challengeRepository.delete(challenge.get());
            return true;
        }
        return false;
    }
}
