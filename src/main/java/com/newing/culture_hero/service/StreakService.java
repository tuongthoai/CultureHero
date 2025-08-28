package com.newing.culture_hero.service;

import com.newing.culture_hero.entity.Streak;

import java.util.UUID;

public interface StreakService {
    Streak getUserStreak(UUID userId);

    Streak incrementStreak(UUID userId);
}
