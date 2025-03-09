package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.LeaderboardDTO;
import com.example.projectgreenie.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LeaderboardController {
    
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardDTO>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }
}
