package com.newing.culture_hero.service;

import com.newing.culture_hero.dto.ReportResponse;

import java.util.List;

public interface ReportService {
    //Phương thức để lấy các báo cáo khác nhau
    ReportResponse getParticipationRate();

    //phương thức lấy tỷ lệ hoàn thành
    ReportResponse getCompletionRate();

    //phương thức lấy điểm kinh nghiệm trung bình
    ReportResponse getStreakRate();

    //phương thức lấy tỷ lệ chứng chỉ
    ReportResponse getCertificateRate();

    //phương thức lấy bảng xếp hạng
    List<ReportResponse> getLeaderboardStats();
}
