# Forvity — CLAUDE.md

## Project Description
Forvity is a web application where members of sports clubs can sign up for weekly activities.
The application is built to support multiple clubs in the same installation.

## Tech Stack
- Java 21
- Spring Boot
- Spring Security (form-based login with email/password)
- Spring Data JPA
- H2 (in-memory, for local development and integration tests)
- PostgreSQL (production)
- Maven
- Docker (for deployment)

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
- DTOs for all communication via API (do not expose entities directly)
- Exceptions handled with `@ControllerAdvice`
- **Lombok** on JPA entities — use `@Getter`/`@Setter` instead of manual boilerplate
- **Java records** for DTOs — immutable, no Lombok needed
- **Design by Contract**:
  - Controller layer: Bean Validation (`@NotBlank`, `@Email`, etc.) on record DTOs with `@Valid` — validates incoming HTTP requests
  - Service layer: Spring `Assert` for preconditions and business logic invariants

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

**`Member`** — system-level identity and credentials:
- `id` (UUID)
- `email` (unique system-wide)
- `username` (unique system-wide)
- `password` (hashed)

**`SystemRole`** — system-wide roles, separate from club roles:
- `id` (UUID)
- `memberId`
- `role` (enum: SUPERADMIN)

**`Club`** — a sports club:
- `id` (UUID)
- `name`

**`Membership`** — links a member to a club:
- `id` (UUID)
- `memberId`
- `clubId`

**`MembershipRole`** — a role a member holds within a club:
- `id` (UUID)
- `membershipId`
- `role` (enum: CLUB_ADMIN, TEAM_ADMIN, MEMBER)
- A member can hold multiple roles within the same club

### Audit Fields (all tables)
Every entity extends `AuditableEntity` which provides:
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)
- `deletedAt` (LocalDateTime) — soft delete tombstone, null means active

### Notes
- `SUPERADMIN` is system-level only, never connected to a club
- A member can belong to multiple clubs, with different roles in each
- Soft deletes are used everywhere — hard deletes only during scheduled cleanup

## Deployment
- Local: run directly with `mvn spring-boot:run` against H2
- Production: Docker container against PostgreSQL
- CI/CD: GitHub Actions — build + tests on push to main
- Cloud: Render/Fly.io (free tier)
