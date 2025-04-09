package com.example.projectgreenie.service;

import com.example.projectgreenie.model.Challenge;
import com.example.projectgreenie.model.ProofSubmission;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.ChallengeRepository;
import com.example.projectgreenie.repository.ProofSubmissionRepository;
import com.example.projectgreenie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProofSubmissionService {

    @Autowired
    private ProofSubmissionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    public List<ProofSubmission> getAllSubmissions() {
        return repository.findAll();
    }

    public Optional<ProofSubmission> getSubmissionById(String proofID) {
        return repository.findById(proofID);
    }

    public ProofSubmission updateStatus(String proofID, String status) {
        Optional<ProofSubmission> submission = repository.findById(proofID);
        if (submission.isPresent()) {
            ProofSubmission updatedSubmission = submission.get();
            updatedSubmission.setStatus(status);
            return repository.save(updatedSubmission);
        }
        return null;
    }

    public boolean deleteProofSubmission(String proofID) {
        Optional<ProofSubmission> proofOpt = repository.findById(proofID);

        if (proofOpt.isPresent()) {
            ProofSubmission proof = proofOpt.get();

            // 🧠 Only deduct points if this proof was previously marked as Verified
            if ("Verified".equalsIgnoreCase(proof.getStatus())) {
                Optional<User> userOpt = userRepository.findById(proof.getUserId());
                Optional<Challenge> challengeOpt = challengeRepository.findByChallengeId(
                        Integer.parseInt(proof.getChallengeID())
                );

                if (userOpt.isPresent() && challengeOpt.isPresent()) {
                    User user = userOpt.get();
                    Challenge challenge = challengeOpt.get();

                    int currentPoints = user.getPointsCount();
                    int deducted = challenge.getPoints();
                    int updated = Math.max(0, currentPoints - deducted);

                    user.setPointsCount(updated);
                    userRepository.save(user);
                }
            }

            // 🔥 Perform actual deletion
            repository.deleteById(proofID);
            return true;
        }

        return false;
    }

    // ⏫ Add this helper method later if needed for verification
}
