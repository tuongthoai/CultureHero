package com.newing.culture_hero.security;

import com.newing.culture_hero.security.dto.LoginRequest;
import com.newing.culture_hero.security.dto.LoginResponse;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserService;
import com.newing.culture_hero.user.userToken.TokenType;
import com.newing.culture_hero.user.userToken.UserToken;
import com.newing.culture_hero.user.userToken.UserTokenService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;
    private UserToken userToken;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
            UserService userService,
            PasswordEncoder passwordEncoder, UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Get user details
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            userTokenService.deleteTokensByUserAndType(user.getId(), TokenType.ACCESS);
            // For now, we'll use a simple token (session-based)
            // Later we can implement JWT
            UserToken access = userTokenService.createAccessToken(user);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LoginResponse response = new LoginResponse(
                    access.getToken(),
                    user.getUsername(),
                    user.getRole(),
                    user.getCompanyId().toString(),
                    access.getCreatedAt().format(fmt), // issuedAt
                    access.getExpiresAt().format(fmt) // expiresAt
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }
        if (token != null) {
            // Xoá token trong DB (idempotent: không lỗi nếu token không tồn tại)
            userTokenService.deleteToken(token);
            System.out.println("Token deleted: " + token);
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return ResponseEntity.ok("Authentication is valid");
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }

    private String extractBearerToken(String header) {
        if (header == null)
            return null;
        String h = header.trim();
        if (h.length() >= 7 && h.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return h.substring(7).trim();
        }
        return null;
    }
}
