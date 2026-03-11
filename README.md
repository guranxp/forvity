# Forvity

A web application where members of sports clubs can sign up for weekly activities. Built to support multiple clubs in the same installation.

## Tech Stack

- Java 21, Spring Boot, Spring Security, Spring Data JPA
- H2 (local), PostgreSQL (production)
- Maven, Docker
- Micrometer + Prometheus

## Quick Start

```bash
mvn spring-boot:run
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup and conventions.
See [OPERATIONS.md](OPERATIONS.md) for deployment, configuration, and admin setup.

## API

REST API versioned under `/api/v1/...`

HTTP test files are in the `/http` directory (IntelliJ HTTP Client).

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/login` | System admin login |
| GET | `/api/v1/system/roles` | List system admins |
| POST | `/api/v1/system/roles` | Create SUPERADMIN |
| DELETE | `/api/v1/system/roles/{id}` | Revoke SUPERADMIN |
| POST | `/api/v1/clubs` | Create club |
| POST | `/api/v1/clubs/{slug}/login` | Club member login |
| POST | `/api/v1/clubs/{slug}/members` | Register club member |

## Observability

- Health: `GET /actuator/health`
- Metrics: `GET /actuator/prometheus`