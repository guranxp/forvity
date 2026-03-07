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
| POST | `/api/v1/members` | Register a new member |

## Observability

- Health: `GET /actuator/health`
- Metrics: `GET /actuator/prometheus`