package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.LeaderboardDTO;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    private static final int CHALLENGE_WEIGHT = 10;
    private static final int POINTS_WEIGHT = 2;
    private static final int BADGE_WEIGHT = 5;
    private static final int BADGE_VALUE = 15;

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<LeaderboardDTO> getLeaderboard() {
        List<User> users = userRepository.findAll();

        // Calculate scores and sort
        List<LeaderboardDTO> leaderboard = users.stream()
                .map(user -> LeaderboardDTO.builder()
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .username("@" + (user.getUsername().isEmpty() ? user.getId() : user.getUsername()))
                        .pointsCount(user.getPointsCount())
                        .challengesCompleted(user.getJoinedChallenges() != null ? user.getJoinedChallenges().size() : 0)
                        .badgesCount(user.getBadgesList() != null ? user.getBadgesList().size() : 0)
                        .profileImgUrl(user.getProfileImgUrl())
                        .leaderboardScore(calculateScore(user))
                        .build())
                .sorted((a, b) -> Integer.compare(b.getLeaderboardScore(), a.getLeaderboardScore()))
                .collect(Collectors.toList());

        // Assign ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        return leaderboard;
    }

    private int calculateScore(User user) {
        int challengesScore = (user.getJoinedChallenges() != null ? user.getJoinedChallenges().size() : 0) * CHALLENGE_WEIGHT;
        int pointsScore = user.getPointsCount() * POINTS_WEIGHT;
        int badgesScore = (user.getBadgesList() != null ? user.getBadgesList().size() : 0) * BADGE_WEIGHT * BADGE_VALUE;

        return challengesScore + pointsScore + badgesScore;
    }
}
