package com.newing.culture_hero.service;

import com.newing.culture_hero.entity.Certificate;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    List<Certificate> getAllCertificates();

    List<Certificate> getCertificatesByUser(UUID userId);

    Certificate requestCertificate(UUID userId);

    Certificate approveCertificate(UUID certificateId);

    Certificate rejectCertificate(UUID certificateId, String reason);
}
