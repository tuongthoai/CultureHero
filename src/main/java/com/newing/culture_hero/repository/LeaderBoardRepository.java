package com.newing.culture_hero.repository;

import com.newing.culture_hero.dto.LeaderboardEntry;
import com.newing.culture_hero.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaderBoardRepository extends CrudRepository<User, UUID> {
    @Query("""
                SELECT new com.newing.culture_hero.dto.LeaderboardEntry(
                    u.id, u.username, u.companyId, SUM(p.xp)
                )
                FROM Progress p
                JOIN p.user u
                GROUP BY u.id, u.username, u.companyId
                ORDER BY SUM(p.xp) DESC
            """)
    List<LeaderboardEntry> getGlobalLeaderboard();

    @Query("""
                SELECT new com.newing.culture_hero.dto.LeaderboardEntry(
                    u.id, u.username, u.companyId, SUM(p.xp)
                )
                FROM Progress p
                JOIN p.user u
                WHERE u.companyId = :companyId
                GROUP BY u.id, u.username, u.companyId
                ORDER BY SUM(p.xp) DESC
            """)
    List<LeaderboardEntry> getCompanyLeaderboard(UUID companyId);
}
