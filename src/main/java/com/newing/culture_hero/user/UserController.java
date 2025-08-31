package com.newing.culture_hero.user;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newing.culture_hero.security.AuthorizationService;
import com.newing.culture_hero.user.dto.UserCreateRequest;
import com.newing.culture_hero.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Autowired
    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UUID companyId = UUID.fromString(request.getCompanyId());
            User user = userService.createUser(companyId, request.getUsername(), request.getPassword(),
                    request.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping
    @PreAuthorize("@authorizationService.canManageUsers()")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) UUID companyId) {
        List<User> users;

        if (authorizationService.isConsultantAdmin()) {
            // Consultant admins can see all users or filter by company
            users = (companyId != null) ? userService.findByCompanyId(companyId) : userService.findAll();
        } else {
            // Client admins can only see users from their company
            User currentUser = authorizationService.getCurrentUser();
            users = userService.findByCompanyId(currentUser.getCompanyId());
        }

        List<UserResponse> response = users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user can access this data
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Users can access their own data
        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.ok(toResponse(user));
        }

        // Admins can access other users' data
        if (!authorizationService.canManageUsers()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Additional company-level access check for non-consultant admins
        if (!authorizationService.isConsultantAdmin() && !authorizationService.canAccessCompany(user.getCompanyId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setCompanyId(user.getCompanyId().toString());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt().toString());
        response.setUpdatedAt(user.getUpdatedAt().toString());
        return response;
    }
}
