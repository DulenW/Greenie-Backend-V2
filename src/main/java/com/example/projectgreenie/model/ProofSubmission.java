package com.example.projectgreenie.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "proofsubmission")
public class ProofSubmission {
    @Id
    private String proofID;

    private String challengeID;
    private String challengeName;
    private String userId;
    private String username;
    private String status; // Pending, Approved, Rejected
    private String imageUrl;
    private String description;
    private LocalDateTime submittedAt;
    private String aiResponse; // New Field to store AI response

    // Constructors
    public ProofSubmission() {}

    public ProofSubmission(String proofID, String challengeID,
                           String challengeName, String userId, String username,
                           String status, String imageUrl, String description,
                           LocalDateTime submittedAt, String aiResponse) {
        this.proofID = proofID;

        this.challengeID = challengeID;
        this.challengeName = challengeName;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.imageUrl = imageUrl;
        this.description = description;
        this.submittedAt = submittedAt;
        this.aiResponse = aiResponse;
    }

    // Getters and Setters
    public String getProofID() { return proofID; }
    public void setProofID(String proofID) { this.proofID = proofID; }


    public String getChallengeID() { return challengeID; }
    public void setChallengeID(String challengeID) { this.challengeID = challengeID; }

    public String getChallengeName() { return challengeName; }
    public void setChallengeName(String challengeName) { this.challengeName = challengeName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
}
