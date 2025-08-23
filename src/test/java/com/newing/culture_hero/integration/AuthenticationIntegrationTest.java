package com.newing.culture_hero.integration;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newing.culture_hero.user.Role;
import com.newing.culture_hero.user.User;
import com.newing.culture_hero.user.UserRepository;
import com.newing.culture_hero.user.UserService;
import com.newing.culture_hero.user.dto.UserCreateRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
@Transactional
public class AuthenticationIntegrationTest {

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                        .withDatabaseName("culturehero_test")
                        .withUsername("test")
                        .withPassword("test");

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
        }

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
        void testCreateUser_Success() throws Exception {
                UserCreateRequest request = new UserCreateRequest();
                request.setCompanyId(companyId1.toString());
                request.setUsername("newuser");
                request.setPassword("password123");
                request.setRole(Role.PARTICIPANT);

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.username").value("newuser"))
                                .andExpect(jsonPath("$.role").value("PARTICIPANT"))
                                .andExpect(jsonPath("$.companyId").value(companyId1.toString()))
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        void testCreateUser_ValidationFailure() throws Exception {
                UserCreateRequest request = new UserCreateRequest();
                request.setCompanyId(companyId1.toString());
                request.setUsername("ab");
                request.setPassword("123");
                request.setRole(Role.PARTICIPANT);

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testBasicAuthentication_Success() throws Exception {
                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("consultant_admin", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("consultant_admin"))
                                .andExpect(jsonPath("$.role").value("CONSULTANT_ADMIN"));
        }

        @Test
        void testBasicAuthentication_Failure() throws Exception {
                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("consultant_admin", "wrongpassword")))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void testAccessWithoutAuthentication() throws Exception {
                mockMvc.perform(get("/api/v1/users/me"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void testTenancyIsolation_DifferentCompanies() throws Exception {
                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("client_admin1", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.companyId").value(companyId1.toString()));

                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("client_admin2", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.companyId").value(companyId2.toString()));
        }

        @Test
        void testPasswordEncryption() {
                User user = userRepository.findByUsername("consultant_admin").orElseThrow();

                assert !user.getPassword().equals("password123");
                assert user.getPassword().startsWith("$2a$12$");
                assert user.getPassword().length() == 60;
        }

        @Test
        void testMultipleRoles() throws Exception {
                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("consultant_admin", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role").value("CONSULTANT_ADMIN"));

                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("client_admin1", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role").value("CLIENT_ADMIN"));

                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("participant1", "password123")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role").value("PARTICIPANT"));
        }

        @Test
        @WithMockUser(username = "consultant_admin", roles = { "CONSULTANT_ADMIN" })
        void testConsultantAdminAccess() throws Exception {
                mockMvc.perform(get("/api/v1/users/me"))
                                .andExpect(status().isOk());
        }

        @Test
        void testUserNotFound() throws Exception {
                mockMvc.perform(get("/api/v1/users/me")
                                .with(httpBasic("nonexistent", "password123")))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void testDuplicateUsername() throws Exception {
                UserCreateRequest request = new UserCreateRequest();
                request.setCompanyId(companyId1.toString());
                request.setUsername("consultant_admin");
                request.setPassword("password123");
                request.setRole(Role.PARTICIPANT);

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }
}
