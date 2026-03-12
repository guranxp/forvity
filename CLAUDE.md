# Forvity — CLAUDE.md

## Project Description
Forvity is a web application where members of sports clubs can sign up for weekly activities.
The application is built to support multiple clubs in the same installation.

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security (form-based login with email/password)
- Spring Data JPA
- H2 (in-memory, for local development and integration tests)
- PostgreSQL (production)
- Maven
- Docker (for deployment)

### Frontend
- React + TypeScript
- Vite (build tool)
- Tailwind CSS v4
- TanStack Query (data fetching)
- React Router (navigation)
- Located in `frontend/` directory
- Built by Maven via `frontend-maven-plugin` and served as static files from Spring Boot
- Dev server: `cd frontend && npm run dev` (proxies `/api` to `localhost:8080`)

## Architecture
Layered architecture with a DDD-inspired package structure — packaged by domain, not by layer.

### Package Structure
```
com.forvity.app
├── member
│   ├── MemberController
│   ├── MemberService
│   ├── MemberRepository
│   └── Member
├── activity
│   ├── ActivityController
│   ├── ActivityService
│   ├── ActivityRepository
│   └── Activity
└── registration
    ├── RegistrationController
    ├── RegistrationService
    ├── RegistrationRepository
    └── Registration
```

### Layers
```
Controller → Service → Repository → Database
```

- **Controller**: REST endpoints, JSON in/out, no business logic
- **Service**: All business logic lives here
- **Repository**: Spring Data JPA, no logic
- **Model**: JPA entities, no outward dependencies

## API
- REST API with JSON
- Versioning: `/api/v1/...`
- HTTP test files (IntelliJ HTTP Client) are created for each endpoint

### Authentication
- System admins (ROOT, SUPERADMIN) log in via: `POST /api/v1/login`
- Club members log in via: `POST /api/v1/clubs/{slug}/login`
- Club slug identifies which club's account to authenticate against (e.g. `fc-stockholm`)
- Spring Security form-based login with email + password

## Test Strategy
- **Unit tests**: Service layer with JUnit 5 + Mockito — focus on business logic
- **Integration tests**: Key flows with `@SpringBootTest` against H2
- No 100% coverage — test what has business value
- Tests are written alongside the code, not after

## Ways of Working
- Trunk Based Development — push frequently to main
- Small, reviewable commits — one domain or one layer per PR
- Feature flags in `application.properties` for unfinished functionality:
  ```properties
  feature.registration.enabled=true
  feature.admin.enabled=false
  ```
- Code must always be deployable

## Code Conventions
- English only — comments, class names, and methods
- camelCase package names: `com.forvity.app`
- camelCase method names — no underscores anywhere
- Static imports preferred over qualified calls (e.g. `hasText(...)` not `Assert.hasText(...)`
- Prefer immutability — make classes and fields immutable where possible
- Always declare local variables and method parameters as `final`
- Follow the Tell Don't Ask principle — tell objects to do things rather than asking for state and acting externally
- Use `Optional` instead of null returns
- Use streams for collection processing
- Never return or pass `null`
- Avoid setters — create instances via constructors with parameters (default) or builder when there are many parameters
- JPA entities use `@NoArgsConstructor(access = PROTECTED)` for JPA + a package-friendly constructor for normal use
- Use `@Getter` only on entities — no `@Setter`
- DTOs for all communication via API (do not expose entities directly)
- Exceptions handled with `@ControllerAdvice`
- **Lombok** on JPA entities — use `@Getter`/`@Setter` instead of manual boilerplate
- **Java records** for DTOs — immutable, no Lombok needed
- **Design by Contract**:
  - Controller layer: Bean Validation (`@NotBlank`, `@Email`, etc.) on record DTOs with `@Valid` — validates incoming HTTP requests
  - Service layer: Spring `Assert` for preconditions and business logic invariants

## Metrics
- Micrometer with Prometheus registry for metrics
- Actuator endpoints exposed: `/actuator/health`, `/actuator/prometheus`
- Define metrics (counters, timers) in the service constructor via `MeterRegistry`
- Use `SimpleMeterRegistry` in unit tests — no mocking of `MeterRegistry`

## Logging
- Use `@Slf4j` (Lombok) on all classes that log
- Use `StructuredArguments.kv(...)` from logstash-logback-encoder for structured key-value logging
- Static import `kv`: `import static net.logstash.logback.argument.StructuredArguments.kv`
- Plain text format locally, JSON (LogstashEncoder) in production (`prod` Spring profile)

## Test Conventions
- Unit tests: `*Test.java` — run by Maven Surefire (`mvn test`)
- Integration tests: `*IT.java` — run by Maven Failsafe (`mvn failsafe:integration-test`)
- Test method naming: `shouldXxxWhenYyy()` in camelCase, no underscores
- Unit tests use JUnit 5 + Mockito, no Spring context
- Integration tests use `@SpringBootTest` against H2

## Domain Model

### Multi-tenancy
- The system is multi-club — clubs are fully isolated from each other
- Deleting a club cascades and removes all its data (memberships, teams, activities, registrations)
- No data is ever shared between clubs

### Entities

**`Member`** — an account scoped to exactly one club:
- `id` (UUID)
- `clubId` (the club this account belongs to)
- `email` (unique per club)
- `username` (unique per club)
- `password` (hashed)
- `role` (enum: `CLUB_ADMIN`, `TEAM_ADMIN`, `MEMBER`)

**`SystemAccount`** — a system-level account with no club connection (ROOT, SUPERADMIN):
- `id` (UUID)
- `email` (unique system-wide)
- `username` (unique system-wide)
- `password` (hashed)

**`SystemRole`** — system-wide role attached to a SystemAccount:
- `id` (UUID)
- `systemAccountId`
- `role` (enum: `ROOT`, `SUPERADMIN`)

**`Club`** — a sports club:
- `id` (UUID)
- `name`
- `slug` (unique, URL-friendly identifier, e.g. `fc-stockholm`)

### Audit Fields (all tables)
Every entity extends `AuditableEntity` which provides:
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)
- `deletedAt` (LocalDateTime) — soft delete tombstone, null means active

### Notes
- Accounts are fully isolated per club — a real person registers separately for each club they join
- A real person who is also a system admin has a separate system account with no club connection
- There is no global identity — the same email address can be registered in multiple clubs independently
- `Membership` and `MembershipRole` as separate entities are not needed — the `Member` entity IS the club membership
- A member can hold multiple roles in their club via `@ElementCollection` on `Member` (stored in `member_roles` table)
- Default role on registration is `MEMBER`
- Soft deletes are used everywhere — hard deletes only during scheduled cleanup

## System Administration

### System Role Hierarchy
- `ROOT` — bootstrap only, cannot be promoted to via API, cannot be revoked by anyone
- `SUPERADMIN` — promoted by ROOT or any SUPERADMIN; can be revoked unless it's the last one or self-revocation
- Spring Security treats ROOT as having all SUPERADMIN authorities
- `SystemRoleType` enum: `ROOT`, `SUPERADMIN`

### SUPERADMIN Bootstrap
- The ROOT account is created on startup via `@EventListener(ApplicationReadyEvent)`
- Reads `app.bootstrap.admin.email` and `app.bootstrap.admin.password` from properties/env vars
- Only runs if no ROOT exists in the DB — safe to keep configured permanently
- Env var equivalents (Spring relaxed binding): `APP_BOOTSTRAP_ADMIN_EMAIL`, `APP_BOOTSTRAP_ADMIN_PASSWORD`
- Additional SUPERADMINs are created via `POST /api/v1/system/roles` by ROOT or an authenticated SUPERADMIN
- Revoking a system role is a soft delete on the `SystemRole` entity

## Deployment
- Local: run directly with `mvn spring-boot:run` against H2
- Production: Docker container against PostgreSQL
- CI/CD: GitHub Actions — build + tests on push to main
- Cloud: Render/Fly.io (free tier)
