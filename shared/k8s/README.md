# LithoApp Kubernetes Manifests for Kind

These manifests run LithoApp on a local Kind cluster named `lithoapp-cluster`.

They keep the existing project layout intact and define Kubernetes resources under `infrastructure/k8s`.

## Layout

```text
infrastructure/k8s/
  base/
    namespace.yaml
    configmaps/
      common-configmap.yaml
    secrets/
      postgres-secret.yaml
      keycloak-secret.yaml
    postgres/
      postgres-pvc.yaml
      postgres-deployment.yaml
      postgres-service.yaml
    keycloak/
      keycloak-configmap.yaml
      keycloak-deployment.yaml
      keycloak-service.yaml
      medical-platform-realm.json.tpl
    patient-service/
      deployment.yaml
      service.yaml
    episode-service/
      deployment.yaml
      service.yaml
    analysis-service/
      deployment.yaml
      service.yaml
    drainage-service/
      deployment.yaml
      service.yaml
    api-gateway/
      deployment.yaml
      service.yaml
    frontend/
      deployment.yaml
      service.yaml
    kustomization.yaml
  local/
    kustomization.yaml
  README.md
```

## Manifest Groups

- `namespace.yaml`: creates the `lithoapp` namespace.
- `configmaps/common-configmap.yaml`: centralizes internal Kubernetes DNS URLs, JDBC URLs, Keycloak URLs, JVM options, analysis local upload storage, and the PostgreSQL bootstrap script.
- `secrets/`: stores local development PostgreSQL credentials and sensitive Keycloak values. Replace these before any shared environment.
- `postgres/`: runs one PostgreSQL 16 instance with a PVC and initializes `patient_db`, `episode_db`, `analysis_db`, `drainage_db`, and `keycloak_db`.
- `keycloak/`: runs Keycloak in `start-dev` mode, renders `medical-platform-realm.json.tpl` into a runtime-only realm JSON, and imports that rendered file.
- backend service folders: create one internal `ClusterIP` Service and one Deployment per Spring Boot app, with probes, resources, local Kind image policy, ConfigMap values, and Secret references.
- `api-gateway/`: exposes only the gateway inside the cluster. It routes to `patient-service`, `episode-service`, `analysis-service`, and `drainage-service` by Kubernetes DNS names.
- `frontend/`: runs the Angular frontend image behind an internal `ClusterIP` Service. Use port-forward for browser access.
- `local/kustomization.yaml`: local overlay that currently points directly at `base`.

## Important Notes

The frontend folder currently has no `Dockerfile`, but the commands below follow the required image convention. Add a frontend Dockerfile before running the frontend build command, or build `lithoapp/frontend:local` by your existing frontend image process.

The frontend must call the API Gateway only. In local browser-based use, that usually means the built Angular app should target the forwarded gateway URL, for example `http://localhost:9090`, or the frontend image should proxy API calls to `http://api-gateway:9090` from inside the cluster.

Keycloak issuer URLs have a local development trap:

- The API Gateway runs inside Kubernetes and can reach Keycloak at `http://keycloak:8080/realms/medical-platform`.
- Your browser reaches Keycloak through port-forward at `http://localhost:8080/realms/medical-platform`.
- JWT issuer validation compares the token `iss` claim to the gateway issuer config. If tokens are issued with `localhost` but the gateway expects `keycloak`, validation can fail. For local Kind, either configure Keycloak hostname/frontend URL consistently or keep the gateway issuer/JWK strategy aligned with how tokens are issued.

## Keycloak Realm Template And Secrets

The Kubernetes realm import uses a sanitized template:

```text
infrastructure/k8s/base/keycloak/medical-platform-realm.json.tpl
```

Do not put real secrets in that file. It contains placeholders such as `__SMTP_PASSWORD__` and `__GOOGLE_CLIENT_SECRET__`.

Sensitive values live in:

```text
infrastructure/k8s/base/secrets/keycloak-secret.yaml
```

That Secret currently contains local dummy values for:

- `admin-username`
- `admin-password`
- `SMTP_USERNAME`
- `SMTP_PASSWORD`
- `SMTP_FROM`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

Non-sensitive environment-specific values live in:

```text
infrastructure/k8s/base/keycloak/keycloak-configmap.yaml
```

That ConfigMap contains:

- Keycloak local runtime switches: `KC_HTTP_ENABLED`, `KC_HOSTNAME_STRICT`
- SMTP host, port, TLS/SSL/auth/debug flags
- frontend base URL, redirect URI, post-logout redirect URI, and web origin
- `render-realm.js`, the renderer used by the initContainer

The realm import receives dynamic values through an explicit preprocessing step:

1. Kustomize creates a `keycloak-realm-template` ConfigMap from `medical-platform-realm.json.tpl`.
2. The `render-keycloak-realm` initContainer runs `node /realm-renderer/render-realm.js`.
3. The script reads ConfigMap and Secret values from environment variables.
4. The script replaces only `__PLACEHOLDER__` tokens, validates the result as JSON, and writes `/realm-rendered/medical-platform-realm.json`.
5. The Keycloak container mounts that rendered file at `/opt/keycloak/data/import` and imports it with `start-dev --import-realm`.

The exported Keycloak key-provider private keys and generated HMAC/AES secrets were removed from the template so each environment can generate fresh realm keys. Do not re-add exported private keys to the template.

To customize local dummy values, edit `keycloak-secret.yaml` and `keycloak-configmap.yaml` before applying. For real environments, prefer generating Secrets outside Git, for example with your CI/CD system, SOPS, Sealed Secrets, External Secrets, or a local untracked overlay. Never commit real SMTP passwords, Google OAuth secrets, OIDC provider secrets, or confidential client secrets.

## Create Kind Cluster

```powershell
kind create cluster --name lithoapp-cluster
```

## Build Local Images

```powershell
docker build -t lithoapp/patient-service:local ./services/patient-service
docker build -t lithoapp/episode-service:local ./services/episode-service
docker build -t lithoapp/analysis-service:local ./services/analysis-service
docker build -t lithoapp/drainage-service:local ./services/drainage-service
docker build -t lithoapp/api-gateway:local ./services/api-gateway
docker build -t lithoapp/frontend:local ./frontend/frontLitho
```

## Load Images Into Kind

```powershell
kind load docker-image lithoapp/patient-service:local --name lithoapp-cluster
kind load docker-image lithoapp/episode-service:local --name lithoapp-cluster
kind load docker-image lithoapp/analysis-service:local --name lithoapp-cluster
kind load docker-image lithoapp/drainage-service:local --name lithoapp-cluster
kind load docker-image lithoapp/api-gateway:local --name lithoapp-cluster
kind load docker-image lithoapp/frontend:local --name lithoapp-cluster
```

## Apply Manifests

```powershell
kubectl apply -k infrastructure/k8s/local
kubectl get pods -n lithoapp
```

## Local Access With Port Forwarding

Run each command in a separate terminal:

```powershell
kubectl port-forward svc/frontend 4200:80 -n lithoapp
kubectl port-forward svc/api-gateway 9090:9090 -n lithoapp
kubectl port-forward svc/keycloak 8080:8080 -n lithoapp
```

Then open:

- Frontend: `http://localhost:4200`
- API Gateway: `http://localhost:9090`
- Keycloak: `http://localhost:8080`

## Debug Commands

```powershell
kubectl get all -n lithoapp
kubectl get pvc -n lithoapp
kubectl describe pod -n lithoapp -l app=api-gateway
kubectl logs -n lithoapp deploy/api-gateway
kubectl logs -n lithoapp deploy/patient-service
kubectl logs -n lithoapp deploy/episode-service
kubectl logs -n lithoapp deploy/analysis-service
kubectl logs -n lithoapp deploy/drainage-service
kubectl logs -n lithoapp deploy/keycloak
kubectl logs -n lithoapp deploy/postgres
kubectl rollout status deploy/api-gateway -n lithoapp
kubectl exec -it deploy/postgres -n lithoapp -- psql -U litho -d postgres
```

To inspect service DNS from inside the cluster:

```powershell
kubectl run dns-check -n lithoapp --rm -it --image=curlimages/curl --restart=Never -- sh
curl http://patient-service:8086/actuator/health
curl http://api-gateway:9090/actuator/health
curl http://keycloak:8080/realms/medical-platform
```

## Delete Everything

Delete only LithoApp resources:

```powershell
kubectl delete -k infrastructure/k8s/local
```

Delete the Kind cluster:

```powershell
kind delete cluster --name lithoapp-cluster
```

If PostgreSQL initialization needs to rerun after changing database bootstrap scripts, delete the PVC too:

```powershell
kubectl delete pvc postgres-data -n lithoapp
```

## Values To Customize

- `infrastructure/k8s/base/secrets/postgres-secret.yaml`: PostgreSQL username/password.
- `infrastructure/k8s/base/secrets/keycloak-secret.yaml`: Keycloak admin credentials, SMTP username/password/from address, Google OAuth client ID/client secret, and any future OAuth/OIDC or confidential client secrets.
- `infrastructure/k8s/base/keycloak/keycloak-configmap.yaml`: SMTP host/port/TLS settings, frontend redirect URIs/web origins, and Keycloak local runtime settings.
- `infrastructure/k8s/base/keycloak/medical-platform-realm.json.tpl`: sanitized realm template only. Keep placeholders here, not real values.
- `infrastructure/k8s/base/configmaps/common-configmap.yaml`: service URLs, JDBC database names, Keycloak issuer/JWK URLs, JVM memory, and analysis storage mode.
- resource requests/limits in each Deployment.
- image tags if you move from `:local` to CI/CD-built tags.
- frontend runtime API base URL or proxy behavior so browser calls go through the API Gateway only.
- Keycloak hostname/issuer strategy for local browser tokens versus in-cluster validation.
