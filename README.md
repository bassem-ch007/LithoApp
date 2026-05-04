# LithoApp

Web-based microservices platform for the metabolic follow-up and ureteral-stent
management of urolithiasis (kidney-stone) patients. The system tracks patient
episodes, biology and stone-composition analyses, and JJ-stent (drainage)
removal deadlines, and notifies the medical team about overdue or completed
items.

Built with **Spring Boot 3 / Java 21** for the backend, **Angular** for the
frontend, **Keycloak** for identity, **PostgreSQL** per service, **MinIO** for
PDF storage, and an **API Gateway** in front of everything.

---

## Table of contents

1. [Architecture overview](#architecture-overview)
2. [Service map](#service-map)
3. [Local setup](#local-setup)
4. [Test users](#test-users)
5. [Workflow testing](#workflow-testing)
6. [Notification testing](#notification-testing)
7. [Service communication](#service-communication)
8. [Known limitations](#known-limitations)
9. [Future improvements](#future-improvements)

---

## Architecture overview

```
                ┌──────────────────────┐
                │   Angular frontend   │  http://localhost:4200
                └──────────┬───────────┘
                           │  JWT (Bearer)
                ┌──────────▼───────────┐
                │      API Gateway     │  http://localhost:9090
                │   (Spring Cloud GW)  │
                └──────────┬───────────┘
                           │
   ┌──────────┬────────────┼──────────────┬─────────────┐
   │          │            │              │             │
┌──▼──┐  ┌────▼────┐  ┌────▼────┐  ┌─────▼─────┐  ┌────▼─────┐
│Pat. │  │Episode  │  │Analysis │  │ Drainage  │  │Notif.    │
│8086 │  │8085     │  │8082     │  │ 8084      │  │8087      │
└──┬──┘  └────┬────┘  └────┬────┘  └─────┬─────┘  └────┬─────┘
   │          │            │              │             │
   PG         PG           PG, MinIO      PG            PG

                ┌──────────────────────┐
                │       Keycloak        │  http://localhost:8080
                │  (realm: medical-     │
                │   platform)           │
                └──────────────────────┘
```

All inter-service calls go through Feign and forward the originating user's JWT
(see `FeignAuthHeaderPropagator` in each service). RBAC is enforced **per
endpoint** with `@PreAuthorize` against Keycloak realm roles
(`UROLOGUE`, `BIOLOGIST`, `ADMIN`).

## Service map

| Service              | Port | DB             | Responsibility                                    |
|----------------------|------|----------------|---------------------------------------------------|
| `api-gateway`        | 9090 | —              | Single entry point, JWT validation, CORS, routing |
| `patient-service`    | 8086 | `lithoapp_patients` | Patient registry, search                     |
| `episode-service`    | 8085 | `episode_db`   | Stone episodes, status machine                    |
| `analysis-service`   | 8082 | `bilan_db`     | Stone & metabolic analyses, PDF upload (MinIO)    |
| `drainage-service`   | 8084 | `drainage_db`  | JJ stent placement / removal, overdue reminders   |
| `notification-service` | 8087 | `notification_db` | In-app notifications, channel dispatch       |
| `keycloak`           | 8080 | `keycloak`     | OIDC, roles, users                                |
| `minio`              | 9000 / 9001 | —       | Object storage for analysis PDFs                  |
| `frontend`           | 4200 | —              | Angular SPA                                       |

## Local setup

### Prerequisites

- Docker Desktop (Compose v2)
- JDK 21 (only if you want to run a service outside Docker)
- Node 20+ and npm (only if you want `ng serve` for the frontend)

### Start everything with Docker Compose

```bash
cd shared/docker
docker compose up -d --build
```

First startup takes a few minutes (database init + Keycloak realm import +
service health checks). Watch progress with:

```bash
docker compose ps
docker compose logs -f api-gateway
```

When `api-gateway` becomes `healthy`, the stack is ready:

- Frontend: <http://localhost:4200>
- Gateway:  <http://localhost:9090>
- Keycloak admin: <http://localhost:8080>  (`admin` / `admin`)
- MinIO console: <http://localhost:9001>   (`minioadmin` / `minioadmin`)

### Resetting state

```bash
cd shared/docker
docker compose down -v          # ⚠ removes all DB volumes too
```

### Running a single backend service from your IDE

Each service has its own `pom.xml`. To run `episode-service` against the
already-running Compose stack:

```bash
cd services/episode-service
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5435/episode_db \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/medical-platform \
KEYCLOAK_JWK_SET_URI=http://localhost:8080/realms/medical-platform/protocol/openid-connect/certs \
PATIENT_SERVICE_BASE_URL=http://localhost:8086 \
./mvnw spring-boot:run
```

> The DB ports listed in `docker-compose.yml` are container-internal; expose
> them via `ports:` blocks if you need to reach them from your host.

### Frontend dev server (hot reload)

```bash
cd frontend/frontLitho
npm install
npm start              # ng serve, port 4200
```

Update `src/environments/environment.ts` if you point the frontend at a
non-default gateway URL.

## Test users

The bundled Keycloak realm (`shared/docker/keycloak/realm/medical-platform-realm.json`)
seeds users for each role. Check that file for the exact usernames and
passwords for your local environment.

Roles enforced across the platform:

- `UROLOGUE` — full clinical write access (patients, episodes, drainages,
  analysis requests).
- `BIOLOGIST` — fills metabolic / stone analysis results.
- `ADMIN` — administrative read-only endpoints + cross-user notifications.

Users without any of those realm roles are routed to `/pending-approval` by
the frontend (see `AuthService.redirectByRole`).

## Workflow testing

The `postman/` directory contains a ready-made collection
(`LithoApp_RBAC_Workflow.postman_collection.json`) that walks through:

1. Login as urologue → create patient → create episode.
2. Create an analysis request → biologist fills it in → urologue reads result.
3. Create a drainage → biologist tries write op (should be 403).
4. Mark drainage removed → notification produced for the urologue.

See `postman/README_Postman_Testing.md` for step-by-step usage.

## Notification testing

End-to-end flow:

1. Start the stack and log in as a **urologue**.
2. Create a patient → episode → drainage with a near-future
   `plannedRemovalDate` (e.g. yesterday) so the daily scan picks it up.
3. The drainage scheduler emits `DRAINAGE_OVERDUE` events that
   `notification-service` ingests via `POST /notifications/events`.
4. Reload the urologue's session → the bell icon shows an unread badge, and
   the `/notifications` page lists the message.
5. Click the notification → the SPA navigates to the linked entity
   (`routeFor` in `notifications.component.ts`) and the item is auto-marked
   as read.

To test the analysis-completion notification, log in as a biologist, fill an
analysis request, and complete it — the originating urologue receives an
`ANALYSIS_COMPLETED` notification.

Failure modes to expect:

- Notification publish failures are **best-effort**: the originating domain
  action still commits. Look for `WARN Failed to publish notification event …`
  in the producer service logs.
- Channel dispatch failures inside `notification-service` are isolated per
  channel — a broken email channel will not block the in-app channel.

## Service communication

- All cross-service HTTP calls use **Feign**.
- `FeignAuthHeaderPropagator` (in each service) copies the inbound JWT into
  outbound calls so RBAC is consistent end-to-end.
- 404 from a downstream service is translated into `Optional.empty()` by the
  Feign client wrappers (e.g. `FeignPatientServiceClient`); other Feign
  failures are wrapped into `*ServiceUnavailableException` and surfaced as
  HTTP 503 to the caller.
- The notification fan-out endpoint (`POST /notifications/events`) is
  intentionally `permitAll` inside notification-service; the gateway forces
  authentication on the way in. See the TODO in `SecurityConfig.java` —
  Keycloak service accounts are the planned long-term solution.

## Known limitations

- **No service-to-service identity yet.** Internal Feign calls reuse the
  end-user's JWT. A compromised end-user token can therefore reach the
  internal notification ingestion endpoint. Tracked by a TODO in
  `notification-service/SecurityConfig.java`.
- **Email and SMS channels are stubs.** `EmailNotificationChannelHandler`
  and `SmsNotificationChannelHandler` log the would-be delivery but do not
  call a real provider.
- **No automated test suite is wired into CI yet.** Maven tests exist per
  service but are not enforced through a pipeline.
- **`KEYCLOAK_ISSUER_URI` deliberately points at `http://localhost:8080`** in
  Compose. This matches the issuer claim Keycloak signs into JWTs (because
  `KC_HOSTNAME=localhost`). Changing the hostname requires updating
  Keycloak as well — do not "fix" this to `http://keycloak:8080` without
  reconfiguring Keycloak.
- **PDF upload size and AV scanning** rely on Spring multipart limits and
  basic content-type / magic-byte checks (`PdfUploadValidator`). No virus
  scanner is wired in.

## Future improvements

- Replace JWT-passthrough with **Keycloak service accounts** (`client_credentials`
  grant) for inter-service calls and for the notification scheduler.
- Replace synchronous Feign delivery to `notification-service` with **Kafka**
  (the consumer skeleton already exists in `NotificationKafkaConsumer`).
- Wire real **email / SMS providers** behind the existing channel handlers.
- Add integration tests covering the cross-service workflows currently
  exercised manually via Postman.
- Add a **CI pipeline** that runs `mvn verify` per service plus
  `npm run lint && npm run build` for the frontend.
- Restrict `/notifications/events` at the **gateway level** so it can only be
  reached from inside the cluster (e.g. via a header injected by an internal
  ingress) — this gives defense in depth before the service-account work
  lands.
