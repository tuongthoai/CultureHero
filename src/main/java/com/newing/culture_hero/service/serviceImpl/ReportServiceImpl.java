package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.dto.ReportResponse;
import com.newing.culture_hero.repository.ReportRepository;
import com.newing.culture_hero.service.ReportService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    @Override
    public ReportResponse getParticipationRate() {
        long total= reportRepository.countParticipants();
        long active = reportRepository.countParticipantsWithProgress();
        double rate = total > 0 ? (active * 100.0 / total) : 0;
        return new ReportResponse("Participation Rate", rate);
    }

    @Override
    public ReportResponse getCompletionRate() {
        Double avpXp=reportRepository.averageCompletedXp();
        return new ReportResponse("Average Completed XP", avpXp != null ? avpXp : 0.0);
    }

    @Override
    public ReportResponse getStreakRate() {
        long total = reportRepository.countParticipants();
        long streaks=reportRepository.countActiveStreaks();
        double rate = total > 0 ? (streaks * 100.0 / total) : 0;
        return new ReportResponse("Streak  Retention Rate", rate);
    }

    //đây là phương thức lấy tỷ lệ chứng chỉ
    @Override
    public ReportResponse getCertificateRate() {
        long total = reportRepository.countParticipants();
        long approved = reportRepository.countApprovedCertificates();
        double rate = total > 0 ? (approved * 100.0 / total) : 0;
        return new ReportResponse("Certificate Approval Rate", rate);
    }
    //đây là phương thức lấy bảng xếp hạng
    @Override
    public List<ReportResponse> getLeaderboardStats() {
        return List.of(
                new ReportResponse("Top 1 XP", 500),
                new ReportResponse("Average XP", 120),
                new ReportResponse("Max XP", 900)
        );
    }
}
