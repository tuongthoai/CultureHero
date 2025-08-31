package com.newing.culture_hero.controller;

import com.newing.culture_hero.entity.Streak;
import com.newing.culture_hero.service.StreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/streaks")
public class StreakController {
    private final StreakService streakService;

    @Autowired
    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Streak> getUserStreak(@PathVariable UUID userId) {
        Streak streak = streakService.getUserStreak(userId);
        return ResponseEntity.ok(streak);
    }

    @PostMapping("/{userId}/increment")
    public ResponseEntity<Streak> incrementStreak(@PathVariable UUID userId) {
        return ResponseEntity.ok(streakService.incrementStreak(userId));
    }

}
