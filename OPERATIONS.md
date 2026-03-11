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

The bootstrap account is a **technical account** intended only for initial setup. Before deploying for the first time:

1. Create a dedicated email address to use as the bootstrap account (e.g. `admin@yourdomain.com`)
2. Set `APP_BOOTSTRAP_ADMIN_EMAIL` and `APP_BOOTSTRAP_ADMIN_PASSWORD` to those credentials before or during deployment
3. On first startup the bootstrap account is created automatically

After the system is running, create a real personal admin account:

1. Log in as the bootstrap admin using the credentials you configured above
2. Create a personal system account and promote it to SUPERADMIN: `POST /api/v1/system/roles`
3. Log in with the personal account and verify access
4. The bootstrap account can remain as a fallback — use a strong password and store it securely

To add additional SUPERADMINs, an existing SUPERADMIN calls:

```
POST /api/v1/system/roles
{ "email": "...", "password": "...", "role": "SUPERADMIN" }
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

## Trust Model and Access Hierarchy

The effective trust hierarchy is:

```
Infrastructure maintainer (env vars, DB access, deployments)
  └── ROOT (bootstrap account — created from env vars)
        └── SUPERADMIN (promoted via API by ROOT or another SUPERADMIN)
              └── CLUB_ADMIN (assigned per club by SUPERADMIN or ROOT)
                    └── MEMBER
```

**Role rules:**
- `ROOT` — created by bootstrap only, cannot be promoted to via API, cannot be revoked by anyone
- `SUPERADMIN` — promoted by ROOT or any SUPERADMIN; can be revoked unless it's the last one or self
- Infrastructure maintainers (those with env var and DB access) sit above all application-level roles — this is intentional and unavoidable

**Implication:** anyone with access to the deployment infrastructure (env vars, database, CI/CD pipeline) has effective control over all accounts. Restrict and audit infrastructure access accordingly.

## Maintenance and Downtime

Downtime is handled at the platform level — not in the application code.

**Render / Fly.io:** both platforms have a built-in maintenance mode toggle that intercepts all traffic and serves a maintenance page. Use this for planned downtime (deployments, DB migrations, etc.).

A custom maintenance page can be configured in the platform dashboard. No application changes are needed.

## Observability

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Application health |
| `GET /actuator/prometheus` | Prometheus metrics |

Logs are emitted as structured JSON in production (Spring profile `prod`).