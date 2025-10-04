package com.newing.culture_hero.security;

import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authorizationService")
public class AuthorizationService {

    private final UserService userService;

    @Autowired
    public AuthorizationService(UserService userService) {
        this.userService = userService;
    }

    public boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }

    public boolean isConsultantAdmin() {
        return hasRole("CONSULTANT_ADMIN");
    }

    public boolean isClientAdmin() {
        return hasRole("CLIENT_ADMIN");
    }

    public boolean isParticipant() {
        return hasRole("PARTICIPANT");
    }

    public boolean canAccessCompany(UUID companyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // Consultant admins can access all companies
        if (isConsultantAdmin()) {
            return true;
        }

        // Other users can only access their own company
        User user = userService.findByUsername(auth.getName());
        return user != null && user.getCompanyId().equals(companyId);
    }

    public boolean canManageUsers() {
        return isConsultantAdmin() || isClientAdmin();
    }

    public boolean canManageAllUsers() {
        return isConsultantAdmin();
    }

    public boolean isOwnerOrAdmin(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // User can access their own data
        if (auth.getName().equals(username)) {
            return true;
        }

        // Admins can access other users' data
        return isConsultantAdmin() || isClientAdmin();
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return userService.findByUsername(auth.getName());
    }
}
