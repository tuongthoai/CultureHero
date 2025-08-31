package com.newing.culture_hero.controller;

import com.newing.culture_hero.dto.ReportResponse;
import com.newing.culture_hero.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/participation")
    public ResponseEntity<ReportResponse> getParticipationRate() {
        return ResponseEntity.ok(reportService.getParticipationRate());
    }

    @GetMapping("/completion")
    public ResponseEntity<ReportResponse> getCompletionRate() {
        return ResponseEntity.ok(reportService.getCompletionRate());
    }

    @GetMapping("/streaks")
    public ResponseEntity<ReportResponse> getStreakRate() {
        return ResponseEntity.ok(reportService.getStreakRate());
    }

    @GetMapping("/certificates")
    public ResponseEntity<ReportResponse> getCertificateRate() {
        return ResponseEntity.ok(reportService.getCertificateRate());
    }

    @GetMapping("/leaderboards")
    public ResponseEntity<List<ReportResponse>> getLeaderboardStats() {
        return ResponseEntity.ok(reportService.getLeaderboardStats());
    }

}
