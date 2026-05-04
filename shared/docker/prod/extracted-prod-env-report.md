# Extracted PROD Environment Report

## Files Inspected

- `services/patient-service/src/main/resources/application.yml`
- `services/patient-service/src/main/resources/application-prod.yml`
- `services/patient-service/pom.xml`
- `services/patient-service/Dockerfile`
- `services/episode-service/src/main/resources/application.yml`
- `services/episode-service/src/main/resources/application-prod.yml`
- `services/episode-service/pom.xml`
- `services/episode-service/Dockerfile`
- `services/analysis-service/src/main/resources/application.yml`
- `services/analysis-service/src/main/resources/application-prod.yml`
- `services/analysis-service/pom.xml`
- `services/analysis-service/Dockerfile`
- `services/drainage-service/src/main/resources/application.yml`
- `services/drainage-service/src/main/resources/application-prod.yml`
- `services/drainage-service/pom.xml`
- `services/drainage-service/Dockerfile`
- `services/api-gateway/src/main/resources/application.yml`
- `services/api-gateway/src/main/resources/application-prod.yml`
- `services/api-gateway/pom.xml`
- `services/api-gateway/Dockerfile`
- `frontend/frontLitho/angular.json`
- `frontend/frontLitho/package.json`
- `frontend/frontLitho/src/environments/environment.ts`
- `frontend/frontLitho/src/environments/environment.prod.ts`
- `frontend/frontLitho/Dockerfile`
- `frontend/frontLitho/nginx.conf`
- `shared/docker/keycloak/realm/medical-platform-realm.json`
- `shared/local/01-config/lithoapp-common-config.yaml`
- `shared/local/02-secrets/postgres-secret.yaml`
- `shared/local/02-secrets/keycloak-secret.yaml`
- `shared/local/02-secrets/minio-secret.yaml`
- `shared/local/06-services/*/deployment.yaml`
- `shared/local/06-services/*/service.yaml`
- `shared/k3s/base/common-configmap.yaml`
- `shared/k3s/base/postgres/postgres-secret.yaml`
- `shared/k3s/base/keycloak/keycloak-secret.yaml`
- `shared/k3s/base/minio/minio-secret.yaml`
- `shared/k3s/base/keycloak/keycloak-deployment.yaml`
- `shared/k3s/base/minio/minio-statefulset.yaml`

## Values Found

- Backend service ports: patient `8086`, episode `8085`, analysis `8082`, drainage `8084`, API gateway `9090`.
- Frontend runtime port: Nginx serves on `80`; existing dev compose requirement maps this to `4200`, and existing local/k3s-style production URLs use `30080`.
- Angular output path: `dist/front-litho/browser`.
- Angular production build configuration: `production`.
- Angular production API URL: `http://192.168.1.13:30090`.
- Angular production Keycloak URL: `http://192.168.1.13:30081`.
- Angular Keycloak realm/client: realm `medical-platform`, client id `frontend-client`.
- Realm file: `medical-platform`; frontend client `frontend-client`; allowed redirect URI `http://localhost:4200/*`; web origin `http://localhost:4200`.
- Default service database names: patient `lithoapp_patients`, episode `episode_db`, analysis `bilan_db`, drainage `drainage_db`.
- Production/local manifest database names: patient `patient_db`, episode `episode_db`, analysis `bilan_db`, drainage `drainage_db`, Keycloak `keycloak_db`.
- Local real database credentials: `postgres` / `postgres`.
- Local real Keycloak admin credentials: `lithoapp` / `bassemsofien`.
- Keycloak DB credentials from local manifests: `postgres` / `postgres`.
- Keycloak issuer/JWK values from local manifests: issuer `http://192.168.1.13:30081/realms/medical-platform`; JWK `http://keycloak:8080/realms/medical-platform/protocol/openid-connect/certs`.
- k3s base issuer/JWK values: issuer `http://auth.lithoapp.local/realms/medical-platform`; JWK `http://keycloak:8080/realms/medical-platform/protocol/openid-connect/certs`.
- Service URLs: patient `http://patient-service:8086`, episode `http://episode-service:8085`, analysis `http://analysis-service:8082`, drainage `http://drainage-service:8084`.
- MinIO is required by `analysis-service`: endpoint `http://minio:9000`, bucket `analysis-pdfs`, access key `minioadmin`, secret key `minioadmin`.
- Existing MinIO image in k3s: `minio/minio:RELEASE.2025-04-22T22-12-26Z`.
- Existing Keycloak image in k3s: `quay.io/keycloak/keycloak:26.5.6`.
- Java version in backend POMs: `21` for all five backend services.

## Missing Values

- No compose-blocking value was missing after using the real values from `shared/local`.
- The k3s base manifests intentionally still contain placeholder production secrets such as `CHANGE_ME_POSTGRES_PASSWORD`, `CHANGE_ME_KEYCLOAK_ADMIN_PASSWORD`, `CHANGE_ME_MINIO_ROOT_PASSWORD`, SMTP placeholders, Google OAuth placeholders, and Keycloak crypto placeholders. These are not used in `.env.prod` because real local values were present elsewhere in the project.
- A final public production host/domain is not settled: existing local files use `192.168.1.13` and k3s base files use `*.lithoapp.local`.

## Conflicts Found

- Patient database name conflict: default `application.yml` uses `lithoapp_patients`, while `application-prod.yml`, local manifests, and k3s manifests use `patient_db`. DEV compose uses `lithoapp_patients`; PROD compose uses `patient_db`.
- Keycloak public host conflict: local/prod Angular files use `192.168.1.13:30081`, while k3s base uses `auth.lithoapp.local`.
- Frontend public host conflict: local config uses `http://192.168.1.13:30080`, while k3s base uses `http://app.lithoapp.local`.
- API public host conflict: Angular prod uses `http://192.168.1.13:30090`, while k3s base uses `http://api.lithoapp.local`.
- Realm redirect/origin conflict: the mounted realm allows only `http://localhost:4200/*` and `http://localhost:4200` for `frontend-client`, but `.env.prod` and Angular prod values point at `http://192.168.1.13:30080`.
- Existing `analysis-service` and `drainage-service` Dockerfiles use Java 17 images even though their POMs declare Java 21. The new Dockerfiles use Java 21.

## Keycloak Issuer URI Risks

- Browser-issued tokens must have an `iss` claim that exactly matches `KEYCLOAK_ISSUER_URI` used by the resource servers.
- DEV compose uses `KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/medical-platform` so browser tokens obtained from the dev Keycloak port match the backend validation issuer. Backends use the internal JWK URL `http://keycloak:8080/.../certs`.
- PROD compose uses `KEYCLOAK_ISSUER_URI=http://192.168.1.13:30081/realms/medical-platform` from existing local production values and an internal JWK URL. If the public host changes, `.env.prod`, Angular production environment, Keycloak hostname settings, and realm redirect/web origin settings must be aligned together.
- The current realm file is safe to mount as requested, but it is dev-oriented for frontend redirect URIs. A production browser origin other than `http://localhost:4200` may fail login until the realm source is updated outside this task.

## Notes For Future k3s Manifests

- Move final secrets into Kubernetes Secrets or sealed/external secret tooling; do not preserve local real secrets in committed manifests.
- Decide one public host strategy before final manifests: `192.168.1.13` NodePort-style URLs or stable DNS names such as `app.lithoapp.local`, `api.lithoapp.local`, and `auth.lithoapp.local`.
- Align Angular production environment, gateway CORS, Keycloak `KC_HOSTNAME`, `KEYCLOAK_ISSUER_URI`, and realm redirect/web origin values as one deployment contract.
- Keep JWK URL internal (`http://keycloak:8080/.../certs`) while keeping issuer URI public, provided Keycloak emits tokens with the same public issuer.
- Use Java 21 images for all backend services to match the POMs.
- Consider replacing per-service PostgreSQL instances with one PostgreSQL StatefulSet plus init database script in k3s if operational simplicity is more important than database-level isolation.
