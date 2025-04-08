package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {
    Optional<Challenge> findByChallengeId(int challengeId);

    // ✅ New method to find the highest challengeId
    Optional<Challenge> findTopByOrderByChallengeIdDesc();
}

