package com.newing.culture_hero.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newing.culture_hero.security.dto.LoginRequest;
import com.newing.culture_hero.user.Role;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserRepository;
import com.newing.culture_hero.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
@Transactional
public class AuthenticationEnhancedIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("culturehero_test")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private UUID companyId1;
    private UUID companyId2;
    private User consultantAdmin;
    private User clientAdmin1;
    private User clientAdmin2;
    private User participant1;
    private User participant2;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        companyId1 = UUID.randomUUID();
        companyId2 = UUID.randomUUID();

        consultantAdmin = userService.createUser(companyId1, "consultant_admin", "password123",
                Role.CONSULTANT_ADMIN);
        clientAdmin1 = userService.createUser(companyId1, "client_admin1", "password123", Role.CLIENT_ADMIN);
        clientAdmin2 = userService.createUser(companyId2, "client_admin2", "password123", Role.CLIENT_ADMIN);
        participant1 = userService.createUser(companyId1, "participant1", "password123", Role.PARTICIPANT);
        participant2 = userService.createUser(companyId2, "participant2", "password123", Role.PARTICIPANT);
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("consultant_admin");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("consultant_admin"))
                .andExpect(jsonPath("$.role").value("CONSULTANT_ADMIN"))
                .andExpect(jsonPath("$.companyId").value(companyId1.toString()))
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("consultant_admin");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(httpBasic("consultant_admin", "password123")))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    void testValidateAuthentication_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate")
                        .with(httpBasic("consultant_admin", "password123")))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication is valid"));
    }

    @Test
    void testValidateAuthentication_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not authenticated"));
    }

    @Test
    void testGetAllUsers_ConsultantAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(httpBasic("consultant_admin", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)); // All 5 users
    }

    @Test
    void testGetAllUsers_ClientAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(httpBasic("client_admin1", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // Only users from company 1
    }

    @Test
    void testGetAllUsers_Participant_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(httpBasic("participant1", "password123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById_ConsultantAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + participant2.getId())
                        .with(httpBasic("consultant_admin", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("participant2"));
    }

    @Test
    void testGetUserById_ClientAdmin_SameCompany() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + participant1.getId())
                        .with(httpBasic("client_admin1", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("participant1"));
    }

    @Test
    void testGetUserById_ClientAdmin_DifferentCompany_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + participant2.getId())
                        .with(httpBasic("client_admin1", "password123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById_Self() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + participant1.getId())
                        .with(httpBasic("participant1", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("participant1"));
    }
}
