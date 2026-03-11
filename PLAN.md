# Forvity — Feature Plan

## First Deployment Flow

1. Deploy with `APP_BOOTSTRAP_ADMIN_EMAIL` and `APP_BOOTSTRAP_ADMIN_PASSWORD` set — ROOT account is auto-created
2. Log in as ROOT via `POST /api/v1/login` — verify it works
3. Create a personal SUPERADMIN account — so day-to-day admin doesn't rely on the bootstrap account

## System Admin Capabilities

What a SUPERADMIN/ROOT can do after setup:

1. **Log in** — `POST /api/v1/login` ✅ done
2. **Manage system admins** — create, revoke, reinstate SUPERADMINs
3. **Manage clubs** — create, view, deactivate/reactivate, mark for deletion
4. **Manage club admins** — create a club admin account for any club, mark for deletion

System settings are a future feature — out of scope for v1.

## Future Features (out of scope for now)

- **Email notifications** — welcome email when a SUPERADMIN or club admin account is created, password reset flow
- **System settings** — configurable system-wide settings via API
- **Scheduled cleanup** — hard delete of entities marked for deletion

## What's Already Built

- `SystemAccount`, `SystemRole`, `SystemRoleType`
- `BootstrapService` — creates ROOT account on startup
- `SystemLoginController` — `POST /api/v1/login`
- `ClubController` — `POST /api/v1/clubs` (create club, SUPERADMIN only) ✅ done
- Staged deletion of old `member/SystemRole` and `member/SystemRoleType` (to be committed)

## System Admin Management Rules

- A system admin account requires only **email + password** — no personal name stored
- Revoking a SUPERADMIN soft-deletes their `SystemRole` — the `SystemAccount` is marked for deletion
- Everything a revoked SUPERADMIN created (clubs, promotions) remains untouched
- A revoked SUPERADMIN can be **reinstated** — soft delete is reactivated
- Self-revocation is **blocked** — a SUPERADMIN cannot revoke themselves
- Revoking the **last SUPERADMIN** is blocked
- Only ROOT can revoke a SUPERADMIN

## Club Management Rules

- A club requires only **name** and **slug** — no contact info in v1
- A club can be in one of three states:
  - **Active** — fully operational
  - **Deactivated** — club admins can still log in and access data, members cannot log in, no new activity
  - **Marked for deletion** — soft delete flag, data is retained, no automatic cleanup in v1
- Deactivate/reactivate is reversible
- Marking for deletion is a separate action from deactivating — also reversible until a future cleanup acts on it
- **Code note:** Club entity needs a `deactivatedAt` field added — `deletedAt` (from AuditableEntity) covers "marked for deletion", but deactivation is a separate state

## Club Admin Management Rules

- A SUPERADMIN can create a club admin account for any club (email + password + role `CLUB_ADMIN`)
- A SUPERADMIN can mark a club admin for deletion
- A CLUB_ADMIN can also create new club admins, promote existing members to club admin, and mark club admins for deletion within their own club
- A member can hold multiple roles — e.g. `CLUB_ADMIN` + `TEAM_ADMIN`
- Self-revocation is **blocked** — a club admin cannot remove their own admin role
- Removing the **last CLUB_ADMIN** in a club is blocked
- Revocation is a soft delete — can be reinstated
- **Code note:** `POST /api/v1/clubs/{slug}/members` already exists for member registration — creating a club admin uses the same endpoint but accepts an optional `role` parameter (defaults to `MEMBER`)

## Next Steps (in order)

1. ~~Commit the staged cleanup (remove old SystemRole/SystemRoleType from member package)~~ ✅ done
2. System admin management:
   - `POST /api/v1/system/roles` ✅ done
   - `DELETE /api/v1/system/roles/{id}` ✅ done
   - `GET /api/v1/system/roles`
3. Manage clubs — `GET /api/v1/clubs`, deactivate/reactivate, mark for deletion — requires adding `deactivatedAt` to Club entity
4. Manage club admins — extend `POST /api/v1/clubs/{slug}/members` to accept a role, `DELETE /api/v1/clubs/{slug}/members/{id}`, `PATCH /api/v1/clubs/{slug}/members/{id}/role`