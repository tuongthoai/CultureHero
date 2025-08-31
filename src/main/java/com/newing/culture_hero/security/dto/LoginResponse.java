package com.newing.culture_hero.security.dto;

import com.newing.culture_hero.user.Role;

public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private Role role;
    private String companyId;
    private String issuedAt;
    private String expiresAt;

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LoginResponse(String accessToken, String username, Role role, String companyId, String issuedAt,
            String expiresAt) {
        this.accessToken = accessToken;
        this.username = username;
        this.role = role;
        this.companyId = companyId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

}
