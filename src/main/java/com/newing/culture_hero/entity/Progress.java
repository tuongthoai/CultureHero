package com.newing.culture_hero.entity;

import com.newing.culture_hero.user.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "progress")
public class Progress {
    @Id private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "xp", nullable = false)
    private int xp;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "streak", nullable = false)
    private int streak;

    @Column(name = "last_streak_date")
    private LocalDateTime lastStreakDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Progress() {}

    public Progress(
            UUID id,
            User user,
            int xp,
            int level,
            int streak,
            LocalDateTime lastStreakDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.xp = xp;
        this.level = level;
        this.streak = streak;
        this.lastStreakDate = lastStreakDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Progress(User user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.xp = 0;
        this.level = 1;
        this.streak = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public LocalDateTime getLastStreakDate() {
        return lastStreakDate;
    }

    public void setLastStreakDate(LocalDateTime lastStreakDate) {
        this.lastStreakDate = lastStreakDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
