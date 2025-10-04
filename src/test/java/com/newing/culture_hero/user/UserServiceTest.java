package com.newing.culture_hero.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testCreateUser_Success() {
        UUID companyId = UUID.randomUUID();
        String username = "testuser";
        String password = "password123";
        Role role = Role.PARTICIPANT;

        User user = userService.createUser(companyId, username, password, role);

        assertNotNull(user.getId());
        assertEquals(companyId, user.getCompanyId());
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testPasswordEncoding() {
        UUID companyId = UUID.randomUUID();
        String rawPassword = "password123";

        User user = userService.createUser(companyId, "testuser", rawPassword, Role.PARTICIPANT);

        assertNotEquals(rawPassword, user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$12$"));
        assertEquals(60, user.getPassword().length());
        assertTrue(passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    @Test
    void testFindByUsername() {
        UUID companyId = UUID.randomUUID();
        String username = "findme";
        userService.createUser(companyId, username, "password123", Role.CLIENT_ADMIN);

        User foundUser = userService.findByUsername(username);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
        assertEquals(Role.CLIENT_ADMIN, foundUser.getRole());
    }

    @Test
    void testFindByUsername_NotFound() {
        User foundUser = userService.findByUsername("nonexistent");

        assertNull(foundUser);
    }

    @Test
    void testMultipleUsersWithDifferentCompanies() {
        UUID company1 = UUID.randomUUID();
        UUID company2 = UUID.randomUUID();

        User user1 = userService.createUser(company1, "user1", "password123", Role.PARTICIPANT);
        User user2 = userService.createUser(company2, "user2", "password123", Role.CLIENT_ADMIN);

        assertEquals(company1, user1.getCompanyId());
        assertEquals(company2, user2.getCompanyId());
        assertNotEquals(user1.getCompanyId(), user2.getCompanyId());
    }

    @Test
    void testAllRoles() {
        UUID companyId = UUID.randomUUID();

        User consultant = userService.createUser(companyId, "consultant", "password123", Role.CONSULTANT_ADMIN);
        User client = userService.createUser(companyId, "client", "password123", Role.CLIENT_ADMIN);
        User participant = userService.createUser(companyId, "participant", "password123", Role.PARTICIPANT);

        assertEquals(Role.CONSULTANT_ADMIN, consultant.getRole());
        assertEquals(Role.CLIENT_ADMIN, client.getRole());
        assertEquals(Role.PARTICIPANT, participant.getRole());
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        UUID companyId = UUID.randomUUID();
        String username = "duplicateuser";

        // Create first user
        userService.createUser(companyId, username, "password123", Role.PARTICIPANT);

        // Attempt to create second user with same username should throw exception
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.createUser(companyId, username, "password456", Role.CLIENT_ADMIN);
        });
    }
}
