# CultureHero Backend — Project Context & Architecture Index

> **Last updated:** 2026-04-21
> **Authoritative index for LLM navigation.** Maintained per `docs/semantic_index_building_guide.md`.

---

## 1. System Overview

- **Project:** CultureHero Backend
- **Tech Stack:** Java 21, Spring Boot 3.4.9, Spring Security, Spring Data JPA (Hibernate), PostgreSQL 16, Flyway 10.17, JJWT 0.12, Lombok, Gradle
- **Purpose:** REST API backend for the Culture Hero Hub gamification platform. Handles authentication (JWT), user/company management, gamified tasks, XP/level progress, streaks, certificates, leaderboards, and analytics reports. Replaced a legacy Supabase backend.
- **Architecture:** Layered (Controller → Service → Repository) with one feature-module exception (`user/` package). Method-level authorization via `@PreAuthorize`.
- **Source Root:** `src/main/java/com/newing/culture_hero`
- **Base Package:** `com.newing.culture_hero`

---

## 2. Quick Reference

| Concern                | File Path                                                                 |
|------------------------|---------------------------------------------------------------------------|
| App Entry Point        | `src/main/java/.../CultureHeroApplication.java`                          |
| Security Config        | `src/main/java/.../security/SecurityConfig.java`                         |
| JWT Utility            | `src/main/java/.../security/JwtUtil.java`                                |
| Auth Controller        | `src/main/java/.../security/AuthController.java`                         |
| Authorization Service  | `src/main/java/.../security/AuthorizationService.java`                   |
| Global Error Handler   | `src/main/java/.../user/GlobalExceptionHandler.java`                     |
| User Entity            | `src/main/java/.../user/User.java`                                       |
| Role Enum              | `src/main/java/.../user/Role.java`                                       |
| Build Config           | `build.gradle`                                                           |
| App Properties         | `src/main/resources/application.properties`                              |
| Flyway Migrations      | `src/main/resources/db/migration/V1__*.sql` through `V9__*.sql`          |
| Docker Compose         | `docker-compose.yml`                                                     |
| DB Init Script         | `init-db/01-init.sql`                                                    |
| Index Building Guide   | `docs/semantic_index_building_guide.md`                                  |

> **Path shorthand:** `...` = `com/newing/culture_hero` throughout this document.

---

## 3. Core Conventions & Rules

### 3.1 Layered Architecture
- Controllers MUST delegate ALL business logic to Service interfaces. Direct Repository access from Controllers is FORBIDDEN.
- Service interfaces live in `service/`. Implementations live in `service/serviceImpl/` and MUST be named `*ServiceImpl.java`.
- The `user/` package is the ONE exception: it is a feature-module containing its own Controller, Service, Repository, DTOs, and sub-modules inline.

### 3.2 DTO Pattern
- Request/Response payloads MUST use DTOs. Never expose JPA Entities directly in API responses (except where legacy code has not yet been migrated — see Gotchas).
- DTOs for the generic layer live in `dto/`.
- DTOs for feature-modules live within the module's own `dto/` sub-package (e.g., `user/dto/`, `security/dto/`).

### 3.3 Authentication & Authorization
- Authentication uses JWT tokens issued by `AuthController` and validated by `JwtUtil`.
- Token persistence is managed by `UserTokenService` / `UserTokenServiceImpl` (in `user/userToken/`).
- Authorization is method-level via `@PreAuthorize` annotations on controller methods.
- `AuthorizationService` provides helper methods (`hasRole()`, `canAccessCompany()`) for programmatic checks.
- Three roles exist: `CONSULTANT_ADMIN`, `CLIENT_ADMIN`, `PARTICIPANT` (defined in `user/Role.java`).

### 3.4 Security Config
- CORS is configured for `localhost:5173` (Vite dev) and `localhost:3000`.
- CSRF is disabled (stateless JWT API).
- **[UNVERIFIED] All API endpoints are currently `.permitAll()` in `SecurityConfig`.** This appears to be a development/testing override. Production MUST restrict endpoints to authenticated users.
- HTTP Basic auth is also enabled as a fallback (`realmName: CultureHero`).

### 3.5 Error Handling
- Global exception handling via `@RestControllerAdvice` in `GlobalExceptionHandler.java` (located in `user/` package despite being global).
- Handles `MethodArgumentNotValidException` → 400 and `DataIntegrityViolationException` → 409.
- `EnrichableException` exists in `exception/` as a custom enrichable error chain — usage is limited.

### 3.6 Database & Migrations
- DDL is managed exclusively by Flyway (`spring.jpa.hibernate.ddl-auto=none`).
- Migrations MUST follow naming: `V{N}__{description}.sql` in `src/main/resources/db/migration/`.
- PostgreSQL 16 via Docker Compose.

### 3.7 Testing
- JUnit 5 + Spring Boot Test + Spring Security Test.
- Testcontainers (PostgreSQL) + H2 for integration tests.
- Tests live in mirrored package structure under `src/test/java/`.
- Integration tests live in `src/test/java/.../integration/`.

### 3.8 Naming Conventions
- Controllers: `*Controller.java`
- Services: `*Service.java` (interface), `*ServiceImpl.java` (implementation)
- Repositories: `*Repository.java`
- Entities: singular noun, e.g., `Task.java`, `Company.java`
- DTOs: `*Request.java`, `*Response.java`, `*Entry.java`
- Enums: descriptive name, e.g., `TaskStatus.java`, `CertificateStatus.java`

---

## 4. Domain Map

### 4.1 Security & Auth (`security/`)
- **Responsibility:** JWT lifecycle (generation, validation, expiration), request authentication, CORS/security filter chain, and authorization helpers.
- **Core Entities:** Depends on `User`, `Role` (from `user/` module)
- **Entry Points:**
  - `AuthController.java` → `/api/v1/auth` (`POST /login`)
- **Key Services:** `JwtUtil.java`, `CustomUserDetailsService.java`, `AuthorizationService.java`
- **Data Access:** Indirect — delegates to `UserRepository` via `CustomUserDetailsService`.
- **DTOs:** `security/dto/LoginRequest.java`, `security/dto/LoginResponse.java`
- **Cross-Module Deps:** Depends on `user/` module for `User`, `UserService`, `UserTokenService`.
- **Notes:** Token refresh is handled via `UserTokenService`. `JwtUtil` reads `jwt.secret` and `jwt.expiration` from properties (defaults: `mySecretKey`, `86400000`).

### 4.2 User Management (`user/`)
- **Responsibility:** User CRUD, user registration, role management, current-user lookup. Feature-module that encapsulates its own full stack.
- **Core Entities:** `User.java` (table: `users`), `Role.java` (enum: `CONSULTANT_ADMIN`, `CLIENT_ADMIN`, `PARTICIPANT`)
- **Entry Points:**
  - `UserController.java` → `/api/v1/users` (`POST /`, `GET /me`, `GET /{id}`, `GET ?companyId=`, `GET /all`)
- **Key Services:** `UserService.java` (concrete class, not interface-driven)
- **Data Access:** `UserRepository.java` (Spring Data JPA, custom: `findByUsername`, `findByCompanyId`)
- **DTOs:** `user/dto/UserCreateRequest.java`, `user/dto/UserResponse.java`
- **Sub-Module — UserToken (`user/userToken/`):**
  - Manages opaque access/refresh tokens persisted in DB.
  - `UserToken.java` (entity, table: `user_tokens`), `TokenType.java` (enum: `ACCESS`, `REFRESH`)
  - `UserTokenService.java` (interface) → `UserTokenServiceImpl.java`
  - `UserTokenRepository.java`
- **Cross-Module Deps:** None outbound. This is a foundational module depended on by `security/` and most other modules.
- **Notes:** `GlobalExceptionHandler.java` lives in this package despite being application-wide. `UserService` is a concrete `@Service` class, NOT interface-driven (inconsistent with other services). `UsernameAlreadyExistsException` is a custom exception for duplicate usernames.

### 4.3 Company Management (`controller/CompanyController` + `service/CompanyService` + `entity/Company`)
- **Responsibility:** CRUD operations for client organizations (companies).
- **Core Entities:** `Company.java` (table: `companies`) — fields: `id`, `code`, `name`, `email`, `phone`, `address`, `status`, timestamps.
- **Entry Points:**
  - `CompanyController.java` → `/api/v1/companies` (`GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`)
- **Key Services:** `CompanyService.java` → `CompanyServiceImpl.java`
- **Data Access:** `CompanyRepository.java` (custom: `findByCode`)
- **Cross-Module Deps:** None.
- **Notes:** `@PreAuthorize("hasAnyRole('CONSULTANT_ADMIN')")` on list endpoint. Company entity is returned directly (no DTO wrapper — see Gotchas).

### 4.4 Task Management (`controller/TaskController` + `service/TaskService` + `entity/Task`)
- **Responsibility:** CRUD for gamified tasks that users can complete for XP and coins.
- **Core Entities:** `Task.java` (table: `tasks`), `TaskDifficulty.java` (enum: `EASY`, `MEDIUM`, `HARD`), `TaskStatus.java` (enum: `ACTIVE`, `INACTIVE`)
- **Entry Points:**
  - `TaskController.java` → `api/v1/tasks` (`POST /`, `GET /`, `GET /{id}`, `GET /status/{status}`, `PUT /{id}`, `DELETE /{id}`)
- **Key Services:** `TaskService.java` → `TaskServiceImpl.java`
- **Data Access:** `TaskRepository.java` (custom: `findByStatus`)
- **Cross-Module Deps:** None.
- **Notes:** `@PreAuthorize("hasAnyRole('CLIENT_ADMIN')")` on create. Route path is missing leading `/` (uses `api/v1/tasks` instead of `/api/v1/tasks`).

### 4.5 Progress & XP (`controller/ProgressController` + `service/ProgressService` + `entity/Progress`)
- **Responsibility:** Tracks user XP, level, and streak data via a one-to-one relationship with User.
- **Core Entities:** `Progress.java` (table: `progress`) — fields: `id`, `user` (OneToOne), `xp`, `level`, `streak`, `lastStreakDate`, timestamps.
- **Entry Points:**
  - `ProgressController.java` → `api/v1/progress` (`POST /{userId}`, `GET /{userId}`, `POST /{userId}/xp`, `POST /{userId}/level-up`, `POST /{userId}/streak/increase`, `POST /{userId}/streak/reset`)
- **Key Services:** `ProgressService.java` → `ProgressServiceImpl.java`
- **Data Access:** `ProgressRepository.java`
- **Cross-Module Deps:** Depends on `user/` module (`User` entity via JPA relationship).
- **Notes:** Route path missing leading `/`. `@PreAuthorize` on create only.

### 4.6 Streak Tracking (`controller/StreakController` + `service/StreakService` + `entity/Streak`)
- **Responsibility:** Tracks consecutive login days per user (separate from the `streak` field in `Progress`).
- **Core Entities:** `Streak.java` (table: `streaks`) — fields: `id`, `user` (OneToOne, LAZY), `count`, `lastLoginDate`.
- **Entry Points:**
  - `StreakController.java` → `/api/v1/streaks` (`GET /{userId}`, `POST /{userId}/increment`)
- **Key Services:** `StreakService.java` → `StreakServiceImpl.java`
- **Data Access:** `StreakRepository.java`
- **Cross-Module Deps:** Depends on `user/` module (`User` entity).
- **Notes:** There is a potential data duplication between `Streak` entity and the `streak` field in `Progress` entity.

### 4.7 Certificate Management (`controller/CertificateController` + `service/CertificateService` + `entity/Certificate`)
- **Responsibility:** Manages certificate request/approval/rejection workflows for users.
- **Core Entities:** `Certificate.java` (table: `certificates`), `CertificateStatus.java` (enum: `REQUESTED`, `APPROVED`, `REJECTED`)
- **Entry Points:**
  - `CertificateController.java` → `/api/v1/certificates` (`GET /all`, `GET /user/{userId}`, `POST /request/{userId}`, `PUT /{id}/approve`, `PUT /{id}/reject`)
- **Key Services:** `CertificateService.java` → `CertificateServiceImpl.java`
- **Data Access:** `CertificateRepository.java`
- **DTOs:** `dto/CertificateResponse.java`
- **Cross-Module Deps:** Depends on `user/` module (`User` entity via ManyToOne).
- **Notes:** `@PreAuthorize("hasRole('CLIENT_ADMIN')")` on list endpoint.

### 4.8 Leaderboard (`controller/LeaderboardController` + `service/LeaderboardService`)
- **Responsibility:** Provides global and per-company leaderboard rankings based on user progress.
- **Core Entities:** None of its own. Reads from `Progress` data.
- **Entry Points:**
  - `LeaderboardController.java` → `/api/v1/leaderboard` (`GET /global`, `GET /company/{companyId}`)
- **Key Services:** `LeaderboardService.java` → `LeaderboardServiceImpl.java`
- **Data Access:** `LeaderBoardRepository.java` (note: inconsistent casing in filename — `LeaderBoard` vs `Leaderboard`)
- **DTOs:** `dto/LeaderboardEntry.java`
- **Cross-Module Deps:** Depends on `Progress`, `User` data.
- **Notes:** No authorization annotations on endpoints.

### 4.9 Reports & Analytics (`controller/ReportController` + `service/ReportService`)
- **Responsibility:** Provides analytical endpoints for participation rates, completion rates, streak stats, certificate stats, and leaderboard summaries.
- **Core Entities:** None of its own. Aggregates across domains.
- **Entry Points:**
  - `ReportController.java` → `/api/v1/reports` (`GET /participation`, `GET /completion`, `GET /streak`, `GET /certificate`, `GET /leaderboard-stats`)
- **Key Services:** `ReportService.java` → `ReportServiceImpl.java`
- **Data Access:** `ReportRepository.java`
- **DTOs:** `dto/ReportResponse.java`
- **Cross-Module Deps:** Cross-cuts all domain data (users, progress, tasks, certificates, streaks).
- **Notes:** No authorization annotations on endpoints.

### 4.10 Configuration (`config/`)
- **Responsibility:** Application-level configuration beans and data seeding.
- **Key Files:** `DataSeeder.java` — currently an empty class (placeholder).
- **Notes:** CORS config and security beans are in `security/SecurityConfig.java`, not here.

### 4.11 Exception Handling (`exception/`)
- **Responsibility:** Custom exception types.
- **Key Files:** `EnrichableException.java` — a custom `RuntimeException` with enrichable error context chain.
- **Notes:** The primary global handler (`GlobalExceptionHandler.java`) is in `user/` package, not here.

---

## 5. Data Flow & Integration

### 5.1 Authentication Flow
```
Frontend (Axios) → POST /api/v1/auth/login
  → AuthController.login()
    → AuthenticationManager.authenticate() (Spring Security)
      → CustomUserDetailsService.loadUserByUsername()
        → UserRepository.findByUsername()
    → UserTokenService.createAccessToken() → persists to user_tokens table
    → Returns LoginResponse { accessToken, username, role, companyId, issuedAt, expiresAt }
```

### 5.2 Typical CRUD Flow (e.g., Tasks)
```
Frontend → GET /api/v1/tasks
  → TaskController.findAll()
    → TaskService.findAll()
      → TaskRepository.findAll() (Spring Data JPA)
    → Returns List<Task> (entity directly — no DTO mapping)
```

### 5.3 Module Dependency Graph
```
┌─────────────────┐
│   security/     │──depends-on──▶ user/ (User, UserService, UserTokenService)
└─────────────────┘
┌─────────────────┐
│   controller/*  │──delegates──▶ service/* ──delegates──▶ repository/*
└─────────────────┘
┌─────────────────┐
│   Leaderboard   │──reads──▶ Progress, User
│   Report        │──reads──▶ All domains (cross-cutting)
└─────────────────┘
```

### 5.4 Frontend Integration
- Frontend is a React (Vite) app running on `localhost:5173`.
- All API calls use Axios with base URL targeting `localhost:8080` (Spring Boot default).
- Auth token is stored client-side and sent as `Authorization: Bearer <token>` header.

---

## 6. Environment & Configuration

### 6.1 Application Properties (`application.properties`)

| Property                             | Purpose                        | Default / Value           |
|--------------------------------------|--------------------------------|---------------------------|
| `spring.datasource.url`             | PostgreSQL connection string   | `jdbc:postgresql://localhost:5432/culturehero` |
| `spring.datasource.username`        | DB username                    | `postgres`                |
| `spring.datasource.password`        | DB password                    | `postgres`                |
| `spring.jpa.hibernate.ddl-auto`     | Hibernate DDL strategy         | `none` (Flyway manages)  |
| `spring.flyway.enabled`             | Enable Flyway migrations       | `true`                    |
| `spring.flyway.locations`           | Migration file location        | `classpath:db/migration`  |
| `jwt.secret`                         | JWT signing key                | `mySecretKey` (CHANGE IN PROD) |
| `jwt.expiration`                     | Token expiration (ms)          | `86400000` (24 hours)     |

### 6.2 Docker Compose (`docker-compose.yml`)

| Service    | Image             | Exposed Port | Notes                         |
|------------|--------------------|-------------|-------------------------------|
| `postgres` | `postgres:16.10`   | `5432`      | DB: `culturehero`, auto-init via `init-db/` |

### 6.3 Flyway Migrations (9 total)

| Version | Description                       |
|---------|-----------------------------------|
| V1      | Create `users` table              |
| V2      | Create `companies` table          |
| V3      | Fix/Create `user_tokens` table    |
| V4      | Fix/Create `companies` table      |
| V5      | Create `tasks` table              |
| V6      | Create `progress` table           |
| V7      | Fix `user_tokens` table (v2)      |
| V8      | Create `certificates` table       |
| V9      | Create `streaks` table            |

---

## 7. Known Gotchas & Decisions

### Architectural Decisions
- **ADR-001:** Chose JWT over sessions for stateless scalability. Tokens are also persisted in DB (`user_tokens` table) to support server-side revocation.
- **ADR-002:** Chose layered architecture over feature-sliced for most modules, with `user/` being the exception as a proof-of-concept for feature-based packaging.
- **ADR-003:** Migrated from Supabase to Spring Boot. Frontend was refactored to use Axios REST calls instead of Supabase client SDK.

### Tech Debt
- **TD-001: SecurityConfig permitAll().** All API endpoints are currently set to `.permitAll()` in `SecurityConfig.java` (lines 27–51). This MUST be restricted before production. `@PreAuthorize` annotations exist on some controller methods but are bypassed by the filter chain.
- **TD-002: Entity exposure.** Several controllers (`CompanyController`, `TaskController`, `ProgressController`, `StreakController`) return JPA entities directly in responses instead of using DTOs. This leaks internal schema details and breaks if lazy-loading fields are accessed outside a session.
- **TD-003: Inconsistent module structure.** `GlobalExceptionHandler` is in the `user/` package but applies globally. Should be moved to `exception/` or a top-level `config/` package.
- **TD-004: `UserService` is not interface-driven.** Unlike all other services, `UserService` is a concrete class with no interface, breaking the established pattern.
- **TD-005: Route path inconsistency.** `TaskController` and `ProgressController` use `api/v1/...` (missing leading `/`). While Spring resolves this, it is inconsistent with other controllers that use `/api/v1/...`.
- **TD-006: Duplicate streak data.** Both `Progress.streak` field and the standalone `Streak` entity track streak information, creating potential data inconsistency.
- **TD-007: `DataSeeder.java` is empty.** The config class exists but has no implementation. Either implement or remove.
- **TD-008: `LeaderBoardRepository` naming.** Uses `LeaderBoard` (PascalCase `B`) while the service and controller use `Leaderboard` (lowercase `b`).

### Gotchas
- **JWT Secret:** The default JWT secret is `mySecretKey` (hardcoded fallback in `JwtUtil`). This MUST be overridden via `jwt.secret` property in production.
- **Hibernate Lazy Init:** `Hibernate6Module` is configured in `SecurityConfig` (not a typical location). It disables `USE_TRANSIENT_ANNOTATION`. Removing this bean will cause `LazyInitializationException` on entities with `LAZY` fetch (e.g., `Certificate.user`, `Streak.user`).
- **CORS Origins:** Only `localhost:5173` and `localhost:3000` are allowed. Update for production/staging domains.
- **Password Encoding:** BCrypt with strength 12 (`BCryptPasswordEncoder(12)`). This is intentionally slow for security; do not reduce.
