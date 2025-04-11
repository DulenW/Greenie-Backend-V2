package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {
    Optional<Challenge> findByChallengeId(int challengeId);
    Optional<Challenge> findTopByOrderByChallengeIdDesc();

    // ✅ Find challenges by status
    List<Challenge> findByStatus(String status);
}
