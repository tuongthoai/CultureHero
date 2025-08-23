package com.newing.culture_hero.security;

import com.newing.culture_hero.user.Role;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setCompanyId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(Role.CLIENT_ADMIN);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());
        userRepository.save(testUser);
    }

    @Test
    void testLoadUserByUsername_Success() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT_ADMIN")));
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistent"));
    }

    @Test
    void testLoadUserByUsername_PasswordMatches() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertTrue(passwordEncoder.matches("password123", userDetails.getPassword()));
        assertFalse(passwordEncoder.matches("wrongpassword", userDetails.getPassword()));
    }

    @Test
    void testLoadUserByUsername_DifferentRoles() {
        User consultantUser = new User();
        consultantUser.setId(UUID.randomUUID());
        consultantUser.setCompanyId(UUID.randomUUID());
        consultantUser.setUsername("consultant");
        consultantUser.setPassword(passwordEncoder.encode("password123"));
        consultantUser.setRole(Role.CONSULTANT_ADMIN);
        consultantUser.setCreatedAt(Instant.now());
        consultantUser.setUpdatedAt(Instant.now());
        userRepository.save(consultantUser);

        User participantUser = new User();
        participantUser.setId(UUID.randomUUID());
        participantUser.setCompanyId(UUID.randomUUID());
        participantUser.setUsername("participant");
        participantUser.setPassword(passwordEncoder.encode("password123"));
        participantUser.setRole(Role.PARTICIPANT);
        participantUser.setCreatedAt(Instant.now());
        participantUser.setUpdatedAt(Instant.now());
        userRepository.save(participantUser);

        UserDetails consultantDetails = userDetailsService.loadUserByUsername("consultant");
        UserDetails participantDetails = userDetailsService.loadUserByUsername("participant");

        assertTrue(consultantDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CONSULTANT_ADMIN")));
        assertTrue(participantDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTICIPANT")));
    }
}
