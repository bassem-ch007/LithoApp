# LithoApp k3s production manifests

This folder contains a simple k3s/Kustomize deployment for LithoApp on an Ubuntu Server.

It is built for Cloudflare Tunnel public exposure:

- `https://lithoapp.online` -> Angular frontend
- `https://api.lithoapp.online` -> API Gateway
- `https://auth.lithoapp.online` -> Keycloak

Everything else stays private inside the `lithoapp` namespace.

Expected TLS flow:

- Browser -> Cloudflare: HTTPS
- Cloudflare -> cloudflared: encrypted Cloudflare Tunnel
- cloudflared -> k3s services: HTTP inside the cluster

Cloudflare SSL/TLS mode can stay `Full`; these manifests do not configure internal HTTPS, cert-manager, Let's Encrypt, Traefik TLS certificates, or service TLS for the first deployment.

## Architecture

Public traffic enters through the `cloudflared` pod and routes to internal ClusterIP services:

- `https://lithoapp.online` -> `http://frontend:80`
- `https://api.lithoapp.online` -> `http://api-gateway:9090`
- `https://auth.lithoapp.online` -> `http://keycloak:8080`

Internal-only services:

- `patient-service:8086`
- `episode-service:8085`
- `analysis-service:8082`
- `drainage-service:8084`
- `postgres:5432`
- `minio:9000`

No LoadBalancer, public NodePort, cert-manager, or Ingress controller is required for this setup.

## Files

- `base/`: reusable manifests
- `base/secrets/`: example-only secret templates, not included by Kustomize
- `base/keycloak/medical-platform-realm.json.tpl`: production realm template
- `overlays/prod/`: production Kustomize overlay and image tags
- `scripts/`: helper scripts for rendering the realm, applying, deleting workloads, and status

## Required secrets

Create these secrets manually on the Ubuntu server. Do not commit real values.

### 1. Application and infrastructure secrets

Create `lithoapp-secrets` with these exact keys:

- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `PATIENT_DB_NAME=patient_db`
- `EPISODE_DB_NAME=episode_db`
- `ANALYSIS_DB_NAME=bilan_db`
- `DRAINAGE_DB_NAME=drainage_db`
- `KEYCLOAK_DB_NAME=keycloak_db`
- `KEYCLOAK_ADMIN`
- `KEYCLOAK_ADMIN_PASSWORD`
- `KC_DB_USERNAME`
- `KC_DB_PASSWORD`
- `MINIO_ROOT_USER`
- `MINIO_ROOT_PASSWORD`
- `STORAGE_MINIO_ACCESS_KEY`
- `STORAGE_MINIO_SECRET_KEY`

Example flow:

```bash
kubectl create namespace lithoapp --dry-run=client -o yaml | kubectl apply -f -
cp shared/k3s/base/secrets/lithoapp-secrets.example.yaml /tmp/lithoapp-secrets.yaml
nano /tmp/lithoapp-secrets.yaml
kubectl apply -f /tmp/lithoapp-secrets.yaml
shred -u /tmp/lithoapp-secrets.yaml
```

The PostgreSQL init script creates `patient_db`, `episode_db`, `bilan_db`, `drainage_db`, and `keycloak_db`.

### 2. Keycloak realm secret

The production realm import deliberately contains no localhost redirect URI and no Google OAuth secret.
For the first deployment, the realm template uses `sslRequired: none` so Keycloak does not block verification while running behind Cloudflare Tunnel. Tighten this after the public hostname, issuer, redirects, and login flow are verified.

Render and apply it:

```bash
FRONTEND_PUBLIC_URL=https://lithoapp.online \
REALM_NAME=medical-platform \
bash shared/k3s/scripts/render-keycloak-realm.sh
```

The rendered `frontend-client` uses:

- redirect URI: `https://lithoapp.online/*`
- web origin: `https://lithoapp.online`
- post logout redirect: `https://lithoapp.online/*`

The browser issuer becomes:

```text
https://auth.lithoapp.online/realms/medical-platform
```

Internal services validate JWKs through:

```text
http://keycloak:8080/realms/medical-platform/protocol/openid-connect/certs
```

### 3. Cloudflare Tunnel secret

This setup uses a locally managed Cloudflare Tunnel config so the hostname-to-service mapping lives in Git while credentials stay out of Git.

Create the tunnel and DNS routes on a trusted machine:

```bash
cloudflared tunnel login
cloudflared tunnel create lithoapp
cloudflared tunnel route dns lithoapp lithoapp.online
cloudflared tunnel route dns lithoapp api.lithoapp.online
cloudflared tunnel route dns lithoapp auth.lithoapp.online
```

Then create the Kubernetes secret:

```bash
kubectl -n lithoapp create secret generic cloudflare-tunnel-secret \
  --from-literal=TUNNEL_ID=<cloudflare-tunnel-uuid> \
  --from-file=credentials.json=$HOME/.cloudflared/<cloudflare-tunnel-uuid>.json
```

The committed tunnel config routes:

- `lithoapp.online` -> `http://frontend:80`
- `api.lithoapp.online` -> `http://api-gateway:9090`
- `auth.lithoapp.online` -> `http://keycloak:8080`

## Image tags

The production overlay uses DockerHub images and one central tag setting per app.

Before deploy, replace `replace-with-git-sha` in `shared/k3s/overlays/prod/kustomization.yaml` with immutable tags:

```bash
cd shared/k3s/overlays/prod
kustomize edit set image docker.io/bassem00/lithoapp-patient-service=docker.io/bassem00/lithoapp-patient-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-episode-service=docker.io/bassem00/lithoapp-episode-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-analysis-service=docker.io/bassem00/lithoapp-analysis-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-drainage-service=docker.io/bassem00/lithoapp-drainage-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-api-gateway=docker.io/bassem00/lithoapp-api-gateway:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-frontend=docker.io/bassem00/lithoapp-frontend:<git-sha>
cd -
```

## Deploy

From the project root on the Ubuntu server:

```bash
kubectl kustomize shared/k3s/overlays/prod
bash shared/k3s/scripts/apply-prod.sh
```

The apply script checks:

- the current kubectl context
- required secrets exist
- image tags are not left as `replace-with-git-sha`
- rollouts complete

## Verify

```bash
kubectl get pods -n lithoapp
kubectl get services -n lithoapp
kubectl logs -n lithoapp deploy/api-gateway
kubectl logs -n lithoapp deploy/keycloak
kubectl logs -n lithoapp deploy/cloudflared
```

Public checks:

```bash
curl -i https://api.lithoapp.online/actuator/health
```

Then browse to:

- `https://lithoapp.online`
- `https://auth.lithoapp.online/realms/medical-platform`

Use `bash shared/k3s/scripts/show-status.sh` for a quick cluster summary.

## Delete workloads

Safe workload delete, preserving namespace, PVCs, and secrets:

```bash
bash shared/k3s/scripts/delete-prod.sh
```

Full data deletion is intentionally manual:

```bash
kubectl delete namespace lithoapp
```

## Troubleshooting

### Keycloak issuer mismatch

Browser-issued tokens must have:

```text
iss=https://auth.lithoapp.online/realms/medical-platform
```

If tokens use `localhost`, a LAN IP, or `http://keycloak:8080`, check:

- `KC_HTTP_ENABLED=true`
- `KC_PROXY_HEADERS=xforwarded`
- `KC_HOSTNAME=auth.lithoapp.online`
- `KC_HOSTNAME_STRICT=false`
- Cloudflare Tunnel forwards `auth.lithoapp.online` to `keycloak:8080`
- the frontend production build uses `https://auth.lithoapp.online`

Keycloak only imports a realm when it does not already exist. If you previously imported the LAN/dev realm, update the existing realm manually or recreate the Keycloak database before relying on this production import.

### JWK public vs internal URL

Spring services use the public issuer and internal JWK URI:

- `KEYCLOAK_ISSUER_URI=https://auth.lithoapp.online/realms/medical-platform`
- `KEYCLOAK_JWK_SET_URI=http://keycloak:8080/realms/medical-platform/protocol/openid-connect/certs`

This avoids browser issuer mismatch while keeping service-to-Keycloak traffic inside the cluster.

### Frontend still calls localhost

Rebuild and push the frontend image after changing:

```text
frontend/frontLitho/src/environments/environment.prod.ts
```

It must use:

- `apiBaseUrl=https://api.lithoapp.online`
- `keycloak.url=https://auth.lithoapp.online`

### API CORS errors

The gateway reads `FRONTEND_ALLOWED_ORIGIN` from the environment. The k3s ConfigMap sets it to:

```text
https://lithoapp.online
```

If you add another frontend domain, set a comma-separated value and rebuild/redeploy the gateway if needed.

### Cloudflare Tunnel not routing

Check:

```bash
kubectl logs -n lithoapp deploy/cloudflared
kubectl get secret -n lithoapp cloudflare-tunnel-secret
kubectl get configmap -n lithoapp cloudflared-config -o yaml
```

Confirm the Cloudflare DNS records route to the same tunnel UUID in `TUNNEL_ID`.

### Pods waiting for DB

PostgreSQL initializes databases only on first creation of the PVC. If you changed DB names after first boot, either create the DB manually or intentionally delete the PVC/namespace.

### MinIO bucket missing

Check the bucket job:

```bash
kubectl logs -n lithoapp job/minio-create-analysis-bucket
```

The job creates `analysis-pdfs` and, when configured, a dedicated MinIO user for `analysis-service`.

### Wrong image tag

If pods show `ImagePullBackOff`, check:

```bash
kubectl describe pod -n lithoapp <pod-name>
```

Then update `shared/k3s/overlays/prod/kustomization.yaml` to a pushed DockerHub tag.

## Notes

- No `notification-service` exists in this repo at the time these manifests were created.
- The production realm template omits the existing Google identity provider because the current exported realm contains real OAuth credentials. Configure Google in Keycloak manually or extend the template with secret placeholders.
- MinIO console is not publicly routed.
- PostgreSQL, MinIO, and all microservices are ClusterIP-only.
