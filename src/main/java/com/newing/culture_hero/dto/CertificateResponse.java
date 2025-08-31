package com.newing.culture_hero.dto;

import com.newing.culture_hero.entity.Certificate;

import java.time.LocalDateTime;
import java.util.UUID;

public class CertificateResponse {
    private UUID id;
    private String status;
    private LocalDateTime requestedAt;
    private String username;

    public CertificateResponse(Certificate cert) {
        this.id = cert.getId();
        this.status = cert.getStatus().name();
        this.requestedAt = cert.getRequestedAt();
        this.username = cert.getUser().getUsername();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
