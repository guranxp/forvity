# Contributing to Forvity

## Prerequisites

- Java 21
- Maven

## Run locally

```bash
mvn spring-boot:run
```

Starts with an H2 in-memory database. No additional setup required.

## Architecture

Layered architecture with a DDD-inspired package structure — packaged by domain, not by layer.

```
com.forvity.app
├── member
├── club
├── membership
├── activity
└── registration
```

### Layers

```
Controller → Service → Repository → Database
```

- **Controller** — REST endpoints, JSON in/out, no business logic
- **Service** — all business logic lives here
- **Repository** — Spring Data JPA, no logic
- **Model** — JPA entities, no outward dependencies

## Code Conventions

- English only — comments, class names, methods
- `final` on all local variables and method parameters
- Static imports preferred (e.g. `hasText(...)` not `Assert.hasText(...)`)
- Tell Don't Ask — tell objects to do things rather than querying state externally
- `Optional` instead of null returns; never return or pass `null`
- Streams for collection processing
- No setters — construct via constructor or builder
- Lombok `@Getter` + constructor on JPA entities; Java records for DTOs
- Bean Validation (`@NotBlank`, `@Email`, etc.) on DTOs with `@Valid` in controllers
- Spring `Assert` for service preconditions and business invariants
- Exceptions handled via `@ControllerAdvice`
- `@Slf4j` + `StructuredArguments.kv(...)` for structured logging
- Micrometer counters/timers defined in service constructors via `MeterRegistry`

## Testing

- **Unit tests** (`*Test.java`) — JUnit 5 + Mockito, no Spring context, run with `mvn test`
- **Integration tests** (`*IT.java`) — `@SpringBootTest` against H2, run with `mvn failsafe:integration-test`
- Test method naming: `shouldXxxWhenYyy()` — camelCase, no underscores
- Use `SimpleMeterRegistry` in unit tests (do not mock `MeterRegistry`)
- No 100% coverage target — test what has business value

## Ways of Working

- Trunk Based Development — push frequently to main
- Small, focused commits — one domain or one layer per commit
- Feature flags in `application.properties` for unfinished functionality:
  ```properties
  feature.registration.enabled=true
  feature.admin.enabled=false
  ```
- Code must always be deployable

## CI

GitHub Actions runs unit tests first, then integration tests on every push to main.