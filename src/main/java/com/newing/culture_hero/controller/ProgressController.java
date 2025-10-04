package com.newing.culture_hero.controller;

import com.newing.culture_hero.entity.Progress;
import com.newing.culture_hero.service.ProgressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;

    @Autowired
    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<Progress> createProgressForUser(@PathVariable UUID userId) {
        Progress progress = progressService.createProgressForUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN','PARTICIPANT')")
    @GetMapping("/{userId}")
    public ResponseEntity<Progress> getProgress(@PathVariable UUID userId) {
        Optional<Progress> progress = progressService.getProgressByUserId(userId);
        return progress.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // (Admin) → cộng XP cho user
    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @PostMapping("/{userId}/add-xp")
    public ResponseEntity<Progress> addXp(@PathVariable UUID userId,
            @RequestParam int amount) {
        Progress progress = progressService.addXp(userId, amount);
        return ResponseEntity.ok(progress);
    }

    // (Admin) → ép thăng cấp cho user
    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @PostMapping("/{userId}/level-up")
    public ResponseEntity<Progress> levelUp(@PathVariable UUID userId) {
        Progress progress = progressService.levelUp(userId);
        return ResponseEntity.ok(progress);
    }

    // (Participant, System) → tăng streak khi user login
    @PreAuthorize("hasRole('PARTICIPANT')")
    @PostMapping("/{userId}/streaks/increase")
    public ResponseEntity<Progress> increaseStreak(@PathVariable UUID userId) {
        Progress progress = progressService.increaseStreak(userId);
        return ResponseEntity.ok(progress);
    }

    // (Admin) → reset streak
    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @PostMapping("/{userId}/streaks/reset")
    public ResponseEntity<Progress> resetStreak(@PathVariable UUID userId) {
        Progress progress = progressService.resetStreak(userId);
        return ResponseEntity.ok(progress);
    }

}