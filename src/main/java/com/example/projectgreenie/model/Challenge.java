package com.example.projectgreenie.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "challenges")
public class Challenge {

    @Id
    private String id; // MongoDB Object ID

    @Indexed(unique = true)
    private int challengeId; // Custom Challenge ID

    private String challengeName;
    private int points;
    private String description;
    private String photoUrl;
    private String status; // e.g., pending, active

    // Constructors
    public Challenge() {
        this.status = "pending"; // default
    }

    public Challenge(int challengeId, String challengeName, int points, String description, String photoUrl) {
        this.challengeId = challengeId;
        this.challengeName = challengeName;
        this.points = points;
        this.description = description;
        this.photoUrl = photoUrl;
        this.status = "pending";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
