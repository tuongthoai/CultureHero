package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.dto.LeaderboardEntry;
import com.newing.culture_hero.repository.LeaderBoardRepository;
import com.newing.culture_hero.service.LeaderboardService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {
    private final LeaderBoardRepository leaderBoardRepository;

    public LeaderboardServiceImpl(LeaderBoardRepository leaderBoardRepository) {
        this.leaderBoardRepository = leaderBoardRepository;
    }

    @Override
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        return leaderBoardRepository.getGlobalLeaderboard();
    }

    @Override
    public List<LeaderboardEntry> getCompanyLeaderboard(UUID companyId) {
        return leaderBoardRepository.getCompanyLeaderboard(companyId);
    }
}
