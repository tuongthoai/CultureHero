package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.entity.Progress;
import com.newing.culture_hero.repository.ProgressRepository;
import com.newing.culture_hero.service.ProgressService;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;
    private final UserService userService;
    @Autowired
    public ProgressServiceImpl( ProgressRepository progressRepository,UserService userService) {
        this.progressRepository = progressRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Progress createProgressForUser(UUID userId) {
        User user = userService.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        Progress progress = new Progress(user);
        return progressRepository.save(progress);
    }

    @Override
    public Progress getByUserId(UUID userId) {
        return progressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for user: " + userId));
    }

    @Override
    public Optional<Progress> getProgressByUserId(UUID userId) {
        return progressRepository.findByUserId(userId);
    }

    @Override
    public Progress addXp(UUID userId, int amount) {
        Progress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for user: " + userId));
        progress.setXp(progress.getXp() + amount);
        progress.setUpdatedAt(LocalDateTime.now());
        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public Progress levelUp(UUID userId) {
        Progress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for user: " + userId));
        progress.setLevel(progress.getLevel() + 1);
        progress.setUpdatedAt(LocalDateTime.now());
        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public Progress increaseStreak(UUID userId) {
        Progress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for user: " + userId));

        LocalDate today = LocalDate.now();
        if (progress.getLastStreakDate() == null || !progress.getLastStreakDate().toLocalDate().equals(today)) {
            progress.setStreak(progress.getStreak() + 1);
            progress.setLastStreakDate(LocalDateTime.now());
        }
        progress.setUpdatedAt(LocalDateTime.now());
        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public Progress resetStreak(UUID userId) {
        Progress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for user: " + userId));
        progress.setStreak(0);
        progress.setUpdatedAt(LocalDateTime.now());
        return progressRepository.save(progress);
    }
}
