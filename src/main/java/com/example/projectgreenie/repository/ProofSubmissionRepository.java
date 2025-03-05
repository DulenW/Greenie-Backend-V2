package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.ProofSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofSubmissionRepository extends MongoRepository<ProofSubmission, String> {
    ProofSubmission findByProofID(String proofId);
}