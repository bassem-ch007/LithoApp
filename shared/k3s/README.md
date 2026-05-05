# LithoApp k3s production manifests

This folder deploys LithoApp on a single Ubuntu/k3s server using Cloudflare Tunnel.

External HTTPS:

- `https://lithoapp.online` -> Angular frontend
- `https://www.lithoapp.online` -> Angular frontend
- `https://api.lithoapp.online` -> API Gateway
- `https://auth.lithoapp.online` -> Keycloak

Internal cluster traffic stays HTTP and private:

- `frontend:80`
- `api-gateway:9090`
- `keycloak:8080`
- `patient-service:8086`
- `episode-service:8085`
- `analysis-service:8082`
- `drainage-service:8084`
- `notification-service:8087`
- `postgres:5432`
- `minio:9000`

No public NodePort, LoadBalancer, Ingress controller, cert-manager, or router port forwarding is required for this setup.

## Required Kubernetes secrets

The manifests expect these three secrets to exist in namespace `lithoapp` before deployment:

```bash
kubectl -n lithoapp get secret lithoapp-secrets
kubectl -n lithoapp get secret keycloak-realm-secret
kubectl -n lithoapp get secret cloudflare-tunnel-secret
```

Expected final shape:

```text
cloudflare-tunnel-secret   Opaque   2
keycloak-realm-secret      Opaque   1
lithoapp-secrets           Opaque   16
```

### `lithoapp-secrets`

Create it from `/opt/lithoapp/secrets/lithoapp-secrets.env`:

```bash
kubectl -n lithoapp create secret generic lithoapp-secrets \
  --from-env-file=/opt/lithoapp/secrets/lithoapp-secrets.env \
  --dry-run=client -o yaml | kubectl apply -f -
```

Required keys:

- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `PATIENT_DB_NAME`
- `EPISODE_DB_NAME`
- `ANALYSIS_DB_NAME`
- `DRAINAGE_DB_NAME`
- `NOTIFICATION_DB_NAME`
- `KEYCLOAK_DB_NAME`
- `KEYCLOAK_ADMIN`
- `KEYCLOAK_ADMIN_PASSWORD`
- `KC_DB_USERNAME`
- `KC_DB_PASSWORD`
- `MINIO_ROOT_USER`
- `MINIO_ROOT_PASSWORD`
- `STORAGE_MINIO_ACCESS_KEY`
- `STORAGE_MINIO_SECRET_KEY`

A server-only example is available in `base/secrets/lithoapp-secrets.env.example`.

### `keycloak-realm-secret`

The current strategy uses a server-only production realm JSON, not a committed real realm file.

```bash
kubectl -n lithoapp create secret generic keycloak-realm-secret \
  --from-file=medical-platform-realm.json=/opt/lithoapp/secrets/medical-prod-realm.json \
  --dry-run=client -o yaml | kubectl apply -f -
```

The production realm should keep:

```json
"sslRequired": "external"
```

and should use:

- frontend: `https://lithoapp.online` and optionally `https://www.lithoapp.online`
- Keycloak public URL: `https://auth.lithoapp.online`
- Google callback: `https://auth.lithoapp.online/realms/medical-platform/broker/google/endpoint`

### `cloudflare-tunnel-secret`

Create this from the Cloudflare Tunnel credentials generated on the server:

```bash
TUNNEL_ID=$(python3 - <<'PY'
import json
with open('/opt/lithoapp/secrets/cloudflared-credentials.json') as f:
    print(json.load(f)['TunnelID'])
PY
)

kubectl -n lithoapp create secret generic cloudflare-tunnel-secret \
  --from-literal=TUNNEL_ID="$TUNNEL_ID" \
  --from-file=credentials.json=/opt/lithoapp/secrets/cloudflared-credentials.json \
  --dry-run=client -o yaml | kubectl apply -f -
```

## Image tags

Before deployment, replace every `replace-with-git-sha` image tag in `overlays/prod/kustomization.yaml` with immutable DockerHub tags produced by CI.

Images:

- `docker.io/bassem00/lithoapp-patient-service:<git-sha>`
- `docker.io/bassem00/lithoapp-episode-service:<git-sha>`
- `docker.io/bassem00/lithoapp-analysis-service:<git-sha>`
- `docker.io/bassem00/lithoapp-drainage-service:<git-sha>`
- `docker.io/bassem00/lithoapp-notification-service:<git-sha>`
- `docker.io/bassem00/lithoapp-api-gateway:<git-sha>`
- `docker.io/bassem00/lithoapp-frontend:<git-sha>`

## Deploy

Manual:

```bash
bash shared/k3s/scripts/apply-prod.sh
```

CI/self-hosted runner:

```bash
AUTO_APPROVE=true bash shared/k3s/scripts/apply-prod.sh
```

## Verify

```bash
kubectl -n lithoapp get pods -o wide
kubectl -n lithoapp get svc
kubectl -n lithoapp logs deploy/cloudflared
kubectl -n lithoapp logs deploy/keycloak
kubectl -n lithoapp logs deploy/api-gateway
```

Public checks:

```bash
curl -I https://lithoapp.online
curl -I https://auth.lithoapp.online/realms/medical-platform
curl -I https://api.lithoapp.online/actuator/health
```
