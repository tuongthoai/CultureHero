package com.newing.culture_hero.service;

import com.newing.culture_hero.dto.LeaderboardEntry;

import java.util.List;
import java.util.UUID;

public interface LeaderboardService {
    List<LeaderboardEntry> getGlobalLeaderboard();

    List<LeaderboardEntry> getCompanyLeaderboard(UUID companyId);
}
