package com.newing.culture_hero.service;

import com.newing.culture_hero.entity.Progress;

import java.util.Optional;
import java.util.UUID;

public interface ProgressService {
    Progress createProgressForUser(UUID userId);

    Progress getByUserId(UUID userId);

    Optional<Progress> getProgressByUserId(UUID userId);

    Progress addXp(UUID userId, int amount);

    Progress levelUp(UUID userId);

    Progress increaseStreak(UUID userId);

    Progress resetStreak(UUID userId);
}
