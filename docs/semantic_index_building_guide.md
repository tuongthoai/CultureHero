# Semantic Domain Map — Authoring Checklist & Quality Guide

> **Purpose:** This document is a reusable, step-by-step guide for building and maintaining
> high-quality project index files (Semantic Domain Maps) optimized for LLM consumption.
> Reference this document at the start of any conversation where you need an index built or updated.
>
> **How to use:** Tell the LLM:
> *"Read the guide at `docs/semantic_index_building_guide.md` and follow it to build/update the project index for this project."*

---

## Phase 0: Pre-Flight — Understand the Target

Before writing a single line, gather answers to these questions.

### Checklist

- [ ] **Identify the project type** (e.g., Spring Boot backend, React frontend, monorepo, microservices).
- [ ] **Identify the primary language(s)** and frameworks in use.
- [ ] **Identify the architectural pattern** (Layered, Hexagonal, Feature-Sliced Design, MVC, Clean Architecture, etc.).
- [ ] **Identify the build system** (Maven, Gradle, npm, pnpm, etc.) and its config file location.
- [ ] **Identify the deployment target** (Docker, K8s, Vercel, bare metal, etc.) if known.
- [ ] **Locate existing documentation** (`/docs`, `README.md`, Swagger/OpenAPI specs, Postman collections).
- [ ] **Confirm the index output location** with the user (e.g., `/docs/project-index.md`).

---

## Phase 1: Information Gathering — What to Scan

> [!IMPORTANT]
> Do NOT dump a raw `tree` output. Instead, scan with intent. Focus on understanding
> *boundaries*, *responsibilities*, and *data flow*.

### 1.1 Structural Scan (Breadth-First)

- [ ] List **top-level directories** only (depth 1-2). Identify which are source, test, config, docs, infra.
- [ ] Identify **entry points**: `main()` classes, `App.tsx`, route definitions, `@SpringBootApplication`.
- [ ] Identify **configuration files**: `application.yml`, `.env`, `vite.config.ts`, `SecurityConfig.java`, etc.
- [ ] Identify **build/dependency files**: `pom.xml`, `build.gradle`, `package.json`.

### 1.2 Domain Scan (Depth-First, Per Module)

For **each major module/feature folder**, gather the following:

- [ ] **Responsibility** — What business domain does this module own? (1-2 sentences max)
- [ ] **Core Entities / Models** — What are the primary data objects? (class names only)
- [ ] **Entry Points** — What controllers, pages, or API routes expose this module? Include base route paths.
- [ ] **Key Services / Hooks** — What are the primary business logic classes or React hooks?
- [ ] **Repository / Data Access** — What repositories or data-fetching mechanisms are used?
- [ ] **External Dependencies** — Does this module call external APIs, message queues, or other microservices?
- [ ] **Cross-Module Dependencies** — Which *other internal modules* does this one depend on?

### 1.3 Convention Scan

- [ ] **Naming conventions** — Are there patterns for file/class naming? (e.g., `*Controller`, `*Service`, `use*Hook`)
- [ ] **Error handling pattern** — Is there a global handler (`@ControllerAdvice`, Error Boundary)?
- [ ] **Response wrapper** — Is there a standard API response DTO (e.g., `ApiResponse<T>`)?
- [ ] **Authentication pattern** — JWT, Session, OAuth2? Where is it configured?
- [ ] **DTO usage** — Are DTOs used for request/response? Where do they live?
- [ ] **Testing pattern** — Unit test framework? Where do tests live? Any naming conventions?
- [ ] **Logging pattern** — SLF4J, Winston, console? Any structured logging?

---

## Phase 2: Writing the Index — Structure & Standards

### 2.1 Required Sections

Every Semantic Domain Map **MUST** contain these sections, in this order:

| #  | Section                        | Purpose                                                                 |
|----|--------------------------------|-------------------------------------------------------------------------|
| 1  | **System Overview**            | Project name, tech stack, 1-2 sentence purpose, architectural pattern.  |
| 2  | **Quick Reference**            | Table of key file paths the LLM will need most often.                   |
| 3  | **Core Conventions & Rules**   | Coding rules, patterns, and constraints the LLM must follow.            |
| 4  | **Domain Map (Module Index)**  | Per-module breakdown with responsibilities and key components.          |
| 5  | **Data Flow & Integration**    | How modules communicate, API contracts, event flows.                    |
| 6  | **Environment & Configuration**| Environment variables, profiles, feature flags.                         |
| 7  | **Known Gotchas & Decisions**  | Architectural decisions, tech debt, and common pitfalls.                |

### 2.2 Writing Rules

> [!WARNING]
> Violating these rules will degrade the index quality and make it less useful for LLM navigation.

1. **Be terse.** Each module description should be 3-8 bullet points. No prose paragraphs.
2. **Use exact names.** Always use the actual class/file name (e.g., `JwtAuthenticationFilter.java`), never vague descriptions like "the auth filter."
3. **Include route paths.** For any controller or page, always include the base route (e.g., `/api/v1/participants`).
4. **Never list every file.** Only list files that represent *architectural boundaries* or *key decision points*. If a module has 20 DTOs, say "DTOs in `dto/` package" — don't list all 20.
5. **State constraints as imperatives.** Write rules as commands: "Controllers MUST use DTOs" not "Controllers use DTOs."
6. **Use consistent formatting.** Every module section must follow the same sub-heading structure (see template below).
7. **Mark uncertainty.** If you are unsure about a module's responsibility, prefix with `[UNVERIFIED]` so the user can confirm.

### 2.3 Module Section Template

Use this exact template for every module in Section 4 (Domain Map):

```markdown
### X.X Module Name (`path/to/module`)
- **Responsibility:** [1-2 sentences describing what this module owns]
- **Core Entities:** `EntityA`, `EntityB`
- **Entry Points:**
  - `ControllerName.java` → `BASE_ROUTE`
- **Key Services:** `ServiceA.java`, `ServiceB.java`
- **Data Access:** `RepositoryA.java` (Spring Data JPA / custom query)
- **Cross-Module Deps:** Depends on `SecurityModule` for auth context.
- **Notes:** [Any special behavior, caveats, or planned changes]
```

---

## Phase 3: Quality Validation — The "5-Question Test"

After the index is written, validate it by answering these 5 questions using *only* the index file. If you cannot answer any of them, the index is incomplete.

### Checklist

- [ ] **Q1: "Where do I start?"** — Given a feature name (e.g., "participant registration"), can you identify the exact controller and service file to open first?
- [ ] **Q2: "What are the rules?"** — Can you determine the project's error handling, DTO, and authentication patterns without reading any source code?
- [ ] **Q3: "What depends on what?"** — Can you trace the dependency chain between two modules (e.g., "How does the Auth module relate to the Participant module")?
- [ ] **Q4: "Where is the config?"** — Can you find the relevant configuration file for a given concern (database, security, external API keys)?
- [ ] **Q5: "What should I avoid?"** — Are there documented gotchas, tech debt items, or anti-patterns that prevent you from making a bad decision?

> [!TIP]
> If the index passes all 5 questions, it is high-quality and ready for use.
> If it fails on even one, go back to Phase 2 and fill the gap.

---

## Phase 4: Maintenance — Keeping the Index Alive

An outdated index is worse than no index. Follow these rules to keep it current.

### When to Update

| Trigger Event                              | Action Required                                        |
|--------------------------------------------|--------------------------------------------------------|
| New module/feature folder created           | Add a new module section to the Domain Map.            |
| Module deleted or merged                    | Remove or consolidate the section.                     |
| New controller or route added               | Update the module's Entry Points.                      |
| Architectural pattern changed               | Update System Overview and Core Conventions.           |
| New environment variable introduced         | Update Environment & Configuration section.            |
| Major refactoring completed                 | Re-run the full Phase 1 scan on affected modules.      |
| New "gotcha" or tech debt discovered        | Add to Known Gotchas & Decisions.                      |

### How to Request an Update

Use this prompt template when asking the LLM to update an existing index:

```
Read the index building guide at `docs/semantic_index_building_guide.md`.
Then read the current project index at `docs/project-index.md`.
I have just completed the following changes: [describe changes].
Please update ONLY the affected sections of the project index.
Do NOT rewrite unchanged sections.
```

### Periodic Full Review

- [ ] Schedule a **full index review** every 2-4 weeks or after every major milestone/sprint.
- [ ] During review, re-run the **5-Question Test** (Phase 3) to validate completeness.

---

## Appendix A: Anti-Patterns to Avoid

| ❌ Anti-Pattern                              | ✅ Do This Instead                                      |
|----------------------------------------------|--------------------------------------------------------|
| Dumping raw `tree` output                    | Write semantic descriptions of each module.            |
| Listing every single file                    | List only architectural boundaries and key components. |
| Writing long prose paragraphs                | Use terse bullet points (3-8 per module).              |
| Using vague names ("the service layer")      | Use exact class names (`ParticipantService.java`).     |
| Mixing project docs with LLM context         | Keep the Semantic Domain Map separate and focused.     |
| Updating only when you remember              | Tie updates to specific trigger events (see table).    |
| One giant monolithic index for a monorepo    | Use one root index + per-module `README.md` files.     |

---

## Appendix B: Full Example Template

```markdown
# Project Context & Architecture Index

## 1. System Overview
- **Project:** [Name]
- **Tech Stack:** [Language, Framework, DB, etc.]
- **Purpose:** [1-2 sentences]
- **Architecture:** [Pattern name]
- **Source Root:** `[path]`

## 2. Quick Reference
| Concern               | File Path                                  |
|-----------------------|--------------------------------------------|
| App Entry Point       | `src/main/java/.../Application.java`       |
| Security Config       | `src/main/java/.../SecurityConfig.java`    |
| Global Error Handler  | `src/main/java/.../GlobalExceptionHandler` |
| API Response Wrapper  | `src/main/java/.../ApiResponse.java`       |
| DB Config             | `src/main/resources/application.yml`       |

## 3. Core Conventions & Rules
- Controllers MUST delegate to Services. Direct Repository access is FORBIDDEN.
- All API responses MUST be wrapped in `ApiResponse<T>`.
- Request/Response payloads MUST use DTOs. Never expose JPA Entities.
- Authentication uses JWT via `JwtAuthenticationFilter`.
- Tests MUST follow `*Test.java` naming and live in mirrored package structure.

## 4. Domain Map

### 4.1 Security & Auth (`src/.../security`)
- **Responsibility:** JWT lifecycle, request filtering, user authentication.
- **Core Entities:** `User`, `Role`
- **Entry Points:** `AuthController.java` → `/api/v1/auth`
- **Key Services:** `AuthService.java`, `CustomUserDetailsService.java`
- **Data Access:** `UserRepository.java`
- **Notes:** Token refresh is handled via `/auth/refresh`.

### 4.2 [Next Module...]
[Follow the same template]

## 5. Data Flow & Integration
- Frontend → `AuthController` → `AuthService` → `UserRepository` → PostgreSQL
- JWT issued on login, validated by `JwtAuthenticationFilter` on every request.

## 6. Environment & Configuration
| Variable              | Purpose                  | Default        |
|-----------------------|--------------------------|----------------|
| `DB_URL`              | PostgreSQL connection    | `localhost:5432`|
| `JWT_SECRET`          | Token signing key        | (none)         |

## 7. Known Gotchas & Decisions
- **ADR-001:** Chose JWT over sessions for stateless scalability.
- **Tech Debt:** [Description of known debt]
- **Gotcha:** [Common pitfall]
```
