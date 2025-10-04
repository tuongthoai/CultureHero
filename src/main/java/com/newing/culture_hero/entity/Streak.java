package com.newing.culture_hero.entity;

import com.newing.culture_hero.user.User;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "streaks")
public class Streak {
    @Id private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "last_login_date", nullable = false)
    private LocalDate lastLoginDate;

    public Streak() {}

    public Streak(User user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.count = 0;
        this.lastLoginDate = LocalDate.now().minusDays(1);
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
