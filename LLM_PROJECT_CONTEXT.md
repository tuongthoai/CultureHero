# CultureHero Backend - Project Context & Index

This file is intended to serve as a high-level memory index to accelerate LLM investigations into the codebase. It outlines the architecture, domain entities, and key design patterns of the CultureHero Spring Boot application.

## 1. Project Overview
CultureHero is a Spring Boot application acting as the backend REST API for the Culture Hero Hub platform. It replaced a legacy Supabase backend. It handles user authentication (via JWT), gamification elements (tasks, progress, streaks), and core entity management (companies, certificates, reports).

## 2. Technology Stack
- **Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Gradle (`build.gradle` located in the root)
- **Authentication:** Spring Security with JWT Tokens
- **Database Access:** Spring Data JPA (Hibernate)

## 3. Core Architecture
The codebase strictly adheres to a standard layered Spring Boot architecture under `src/main/java/com/newing/culture_hero`:

- `config/`: Application configuration components (e.g., `DataSeeder`).
- `controller/`: REST API endpoints handling HTTP requests.
- `service/` & `serviceImpl/`: Business logic interfaces and their respective implementations.
- `repository/`: Spring Data JPA interfaces for database persistence.
- `entity/`: JPA entities representing database tables.
- `dto/`: Data Transfer Objects (Requests/Responses) for separating database models from API payloads.
- `security/`: Spring Security configurations, JWT generation/validation (`JwtUtil`), and custom authorization services.
- `exception/`: Global and custom exception handling (`GlobalExceptionHandler`).
- `user/`: The user module encapsulates User entities, roles, controllers, and token management separately from the main generic packages.

## 4. Key Domain Entities
- **User (`user/User.java`)**: The central user entity that manages roles (Admin, Master Admin, Participant) and authentication details.
- **Company (`entity/Company.java`)**: Represents client organizations.
- **Progress (`entity/Progress.java`)**: Tracks a user's XP, level, and streaks. Has a One-to-One mapping with the `User`.
- **Streak (`entity/Streak.java`)**: Tracks consecutive days of activity or logins.
- **Task (`entity/Task.java`)**: Represents gamified activities that users can complete for XP and coins. Includes enums `TaskDifficulty` and `TaskStatus`.
- **Certificate (`entity/Certificate.java`)**: Achievements or credentials awarded to users. Includes `CertificateStatus`.

## 5. API Modules Overview
Controllers map to the following primary functional domains:
- **Authentication & User Management**: `AuthController`, `UserController`
- **Gamification & Engagement**: `ProgressController`, `StreakController`, `TaskController`, `LeaderboardController`
- **Core Business Entities**: `CompanyController`, `CertificateController`
- **Analytics & Exports**: `ReportController`

## 6. Important Design Patterns
- **Interface-Driven Services**: All business logic goes through interfaces (e.g., `TaskService`) before implementation (`TaskServiceImpl`) ensuring loose coupling and ease of testing.
- **DTO Pattern**: Entities are strictly isolated from the API layer using DTOs (e.g., `CertificateResponse`, `LeaderboardEntry`, `LoginRequest`).
- **Feature-Module Exception (User)**: While the architecture is predominantly layer-based, the `user` domain is encapsulated feature-first (containing its own Controller, Service, Repository, and DTOs within the `user` package).

## 7. Migration Notes
The application was migrated from a Supabase backend to this Spring Boot API. As such, the frontend (Culture Hero Hub) uses Axios calls to interface with these controllers, relying on standard REST conventions and JWTs provided by the `/auth` endpoints.
