# Operations Guide

## Requirements

- Docker
- PostgreSQL (production)

## Environment Variables

| Variable | Description | Required |
|---|---|---|
| `APP_BOOTSTRAP_ADMIN_EMAIL` | Email for the first SUPERADMIN | Yes (first run) |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | Password for the first SUPERADMIN | Yes (first run) |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | Yes (production) |
| `SPRING_DATASOURCE_USERNAME` | DB username | Yes (production) |
| `SPRING_DATASOURCE_PASSWORD` | DB password | Yes (production) |

## Bootstrap — First Admin

On startup, the application checks if any SUPERADMIN exists. If not, it creates one using the bootstrap variables above. This runs only once — once a SUPERADMIN exists the values are ignored.

Change the password via the API after first login.

To add additional SUPERADMINs, an existing SUPERADMIN calls:

```
POST /api/v1/system/roles
{ "memberId": "...", "role": "SUPERADMIN" }
```

To revoke a SUPERADMIN role, call:

```
DELETE /api/v1/system/roles/{roleId}
```

## Deployment

Production runs as a Docker container. Build and run:

```bash
docker build -t forvity .
docker run -p 8080:8080 \
  -e APP_BOOTSTRAP_ADMIN_EMAIL=admin@example.com \
  -e APP_BOOTSTRAP_ADMIN_PASSWORD=changeme \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/forvity \
  -e SPRING_DATASOURCE_USERNAME=forvity \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  forvity
```

CI/CD deploys automatically to Render/Fly.io on push to main via GitHub Actions.

## Observability

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Application health |
| `GET /actuator/prometheus` | Prometheus metrics |

Logs are emitted as structured JSON in production (Spring profile `prod`).