# LithoApp Docker Compose DEV

This compose environment is for local development-style integration of the LithoApp frontend, API gateway, backend services, PostgreSQL databases, Keycloak, and MinIO.

DEV does not set `SPRING_PROFILES_ACTIVE=prod`. It uses each service's default Spring configuration with container-specific environment overrides only where needed for networking, database URLs, Keycloak JWT validation, service URLs, and MinIO.

Keycloak imports the existing realm file from `shared/docker/keycloak/realm/medical-platform-realm.json` using this mount:

```text
../keycloak/realm/medical-platform-realm.json:/opt/keycloak/data/import/medical-platform-realm.json:ro
```

The `.dockerignore` files inside each service folder are reference ignore files. Because the build context is the project root (`../../..`), classic Docker builds read only a root-context `.dockerignore`; adjacent service-folder `.dockerignore` files are not silently relied on unless your Docker/BuildKit workflow supports Dockerfile-specific ignore behavior.

## Commands

```bash
docker compose -f shared/docker/dev/docker-compose.yml up -d --build
docker compose -f shared/docker/dev/docker-compose.yml down
docker compose -f shared/docker/dev/docker-compose.yml ps
docker compose -f shared/docker/dev/docker-compose.yml logs -f api-gateway
```
