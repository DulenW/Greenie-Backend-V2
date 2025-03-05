package com.example.projectgreenie.service;

import com.example.projectgreenie.model.ProofSubmission;
import com.example.projectgreenie.repository.ProofSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProofSubmissionService {

    @Autowired
    private ProofSubmissionRepository repository;

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
        if (repository.existsById(proofID)) {
            repository.deleteById(proofID);
            return true;
        }
        return false;
    }
}