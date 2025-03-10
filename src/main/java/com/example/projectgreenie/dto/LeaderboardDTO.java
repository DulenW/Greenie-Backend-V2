package com.example.projectgreenie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardDTO {
    private int rank;
    private String userId;
    private String fullName;
    private String username;
    private int pointsCount;
    private int challengesCompleted;
    private int badgesCount;
    private int leaderboardScore;
    private String profileImgUrl;
}
