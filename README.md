# Forvity

A web application where members of sports clubs can sign up for weekly activities. Built to support multiple clubs in the same installation.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security (form-based login with email/password)
- Spring Data JPA
- H2 (in-memory, local development)
- PostgreSQL (production)
- Maven
- Docker

## Getting Started

### Prerequisites

- Java 21
- Maven

### Run locally

```bash
mvn spring-boot:run
```

The application starts with an H2 in-memory database. No additional setup required.

## Project Structure

Packaged by domain (DDD-inspired):

```
com.forvity.app
├── member
├── activity
└── registration
```

## API

REST API versioned under `/api/v1/...`

## Deployment

Production runs as a Docker container against PostgreSQL, deployed to Render/Fly.io via GitHub Actions.
