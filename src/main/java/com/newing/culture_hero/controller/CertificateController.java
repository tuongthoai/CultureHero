package com.newing.culture_hero.controller;

import com.newing.culture_hero.dto.CertificateResponse;
import com.newing.culture_hero.entity.Certificate;
import com.newing.culture_hero.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PreAuthorize("hasRole('CLIENT_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<CertificateResponse>> getAllCertificates() {
        List<CertificateResponse> responses = certificateService.getAllCertificates()
                .stream()
                .map(CertificateResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('PARTICIPANT')")
    @PostMapping("/{userId}/request")
    public ResponseEntity<Certificate> requestCertificate(@PathVariable UUID userId) {
        return ResponseEntity.ok(certificateService.requestCertificate(userId));
    }

    @PreAuthorize("hasRole('CLIENT_ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Certificate> approveCertificate(@PathVariable UUID id) {
        return ResponseEntity.ok(certificateService.approveCertificate(id));
    }

    @PreAuthorize("hasRole('CLIENT_ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Certificate> rejectCertificate(@PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(certificateService.rejectCertificate(id, reason));
    }
}
