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
- DTOs for all communication via API (do not expose entities directly)
- Exceptions handled with `@ControllerAdvice`

## Deployment
- Local: run directly with `mvn spring-boot:run` against H2
- Production: Docker container against PostgreSQL
- CI/CD: GitHub Actions — build + tests on push to main
- Cloud: Render/Fly.io (free tier)
