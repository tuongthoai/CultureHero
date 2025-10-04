package com.newing.culture_hero.controller;

import com.newing.culture_hero.dto.LeaderboardEntry;
import com.newing.culture_hero.service.LeaderboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntry>> getGlobalLeaderboard() {
        List<LeaderboardEntry> leaderboard = leaderboardService.getGlobalLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<LeaderboardEntry>> getCompanyLeaderboard(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(leaderboardService.getCompanyLeaderboard(companyId));
    }
}
