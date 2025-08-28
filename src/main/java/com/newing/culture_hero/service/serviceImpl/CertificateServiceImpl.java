package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.entity.Certificate;
import com.newing.culture_hero.entity.CertificateStatus;
import com.newing.culture_hero.repository.CertificateRepository;
import com.newing.culture_hero.service.CertificateService;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final UserService userService;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository, UserService userService) {
        this.certificateRepository = certificateRepository;
        this.userService = userService;
    }
    @Override
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @Override
    public List<Certificate> getCertificatesByUser(UUID userId) {
        return certificateRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Certificate requestCertificate(UUID userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Certificate certificate = new Certificate(user, CertificateStatus.REQUESTED);
        return certificateRepository.save(certificate);
    }

    @Override
    @Transactional
    public Certificate approveCertificate(UUID certificateId) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));
        cert.setStatus(CertificateStatus.APPROVED);
        cert.setApprovedAt(LocalDateTime.now());
        return certificateRepository.save(cert);
    }

    @Override
    @Transactional
    public Certificate rejectCertificate(UUID certificateId, String reason) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));
        cert.setStatus(CertificateStatus.REJECTED);
        cert.setRejectedAt(LocalDateTime.now());
        cert.setReason(reason);
        return certificateRepository.save(cert);
    }
}
