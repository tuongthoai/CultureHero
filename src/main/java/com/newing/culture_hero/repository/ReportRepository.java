package com.newing.culture_hero.repository;

import com.newing.culture_hero.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends CrudRepository<User, UUID> {
    //số lượng người dùng có vai trò là "PARTICIPANT"
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'PARTICIPANT'")
    long countParticipants();

    //số lượng người dùng có tiến trình (progress) khác null
    @Query("SELECT COUNT(DISTINCT p.user.id) FROM Progress p")
    long countParticipantsWithProgress();

    //tổng số điểm kinh nghiệm (xp) đã hoàn thành bởi tất cả người dùng
    @Query("SELECT AVG(p.xp) FROM Progress p")
    Double averageCompletedXp();

    //số lượng người dùng có streak (chuỗi đăng nhập liên tiếp) >= 3 ngày
    @Query("SELECT COUNT(s) FROM Streak s WHERE s.count >= 3")
    long countActiveStreaks();

    //số lượng chứng chỉ (certificate) đã được phê duyệt (status = "APPROVED")
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.status = 'APPROVED'")
    long countApprovedCertificates();

    //tổng số chứng chỉ (certificate) đã được phát hành
    @Query("SELECT COUNT(c) FROM Certificate c")
    long countAllCertificates();
}
