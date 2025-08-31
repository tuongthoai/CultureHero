package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.entity.Streak;
import com.newing.culture_hero.repository.StreakRepository;
import com.newing.culture_hero.service.StreakService;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class StreakServiceImpl implements StreakService {
    private final StreakRepository streakRepository;
    private final UserService userService;

    @Autowired
    public StreakServiceImpl(StreakRepository streakRepository, UserService userService) {
        this.streakRepository = streakRepository;
        this.userService = userService;
    }

    @Override
    public Streak getUserStreak(UUID userId) {
        User user = userService.findById(userId);
        return streakRepository.findByUser(user)
                .orElse(new Streak(user));
    }

    @Override
    @Transactional
    public Streak incrementStreak(UUID userId) {
        User user = userService.findById(userId);
        Streak streak = streakRepository.findByUser(user)
                .orElse(new Streak(user));
        LocalDate today = LocalDate.now();
        if (streak.getLastLoginDate().equals(today.minusDays(1))) {
            // login ngày liên tiếp
            streak.setCount(streak.getCount() + 1);
        } else if (!streak.getLastLoginDate().equals(today)) {
            // login sau khi bỏ streak
            streak.setCount(1);
        }
        streak.setLastLoginDate(today);
        return streakRepository.save(streak);
    }
}
