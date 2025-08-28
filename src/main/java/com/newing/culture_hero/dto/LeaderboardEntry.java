package com.newing.culture_hero.dto;

import java.util.UUID;

public class LeaderboardEntry {
    private UUID userId;
    private String username;
    private UUID companyId;
    private Long totalXp;

    public LeaderboardEntry(UUID userId, String username, UUID companyId, Long totalXp) {
        this.userId = userId;
        this.username = username;
        this.companyId = companyId;
        this.totalXp = totalXp;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public Long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Long totalXp) {
        this.totalXp = totalXp;
    }
}
