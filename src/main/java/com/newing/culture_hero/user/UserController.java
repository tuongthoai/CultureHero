package com.newing.culture_hero.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newing.culture_hero.user.dto.UserCreateRequest;
import com.newing.culture_hero.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UUID companyId = UUID.fromString(request.getCompanyId());
            User user = userService.createUser(companyId, request.getUsername(), request.getPassword(), request.getRole());
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
