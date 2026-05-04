# LithoApp Docker Compose PROD

This compose environment is production-like Docker Compose for building and running LithoApp services with the production Spring profile.

PROD sets `SPRING_PROFILES_ACTIVE=prod` for the backend microservices and API gateway. It uses `shared/docker/prod/.env.prod` for ports, database names, credentials, service URLs, Keycloak URLs, MinIO settings, and other environment values extracted from the existing project configuration.

Keycloak imports the existing realm file from `shared/docker/keycloak/realm/medical-platform-realm.json` using this mount:

```text
../keycloak/realm/medical-platform-realm.json:/opt/keycloak/data/import/medical-platform-realm.json:ro
```

This compose file is production-like and useful for validation, packaging, and local deployment rehearsal. It is not a replacement for final k3s manifests.

The `.dockerignore` files inside each service folder are reference ignore files. Because the build context is the project root (`../../..`), classic Docker builds read only a root-context `.dockerignore`; adjacent service-folder `.dockerignore` files are not silently relied on unless your Docker/BuildKit workflow supports Dockerfile-specific ignore behavior.

Review `extracted-prod-env-report.md` before using this outside the current local machine. The current Angular production environment and Keycloak realm contain host/redirect assumptions that may need coordinated changes in future k3s manifests.

## Commands

```bash
docker compose --env-file shared/docker/prod/.env.prod -f shared/docker/prod/docker-compose.yml build
docker compose --env-file shared/docker/prod/.env.prod -f shared/docker/prod/docker-compose.yml up -d
docker compose --env-file shared/docker/prod/.env.prod -f shared/docker/prod/docker-compose.yml down
docker compose --env-file shared/docker/prod/.env.prod -f shared/docker/prod/docker-compose.yml ps
docker compose --env-file shared/docker/prod/.env.prod -f shared/docker/prod/docker-compose.yml logs -f api-gateway
```
