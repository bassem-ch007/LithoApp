#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-lithoapp}"

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "Missing required command: $1" >&2; exit 1; }
}

secret_has_key() {
  local secret="$1"
  local key="$2"
  kubectl -n "$NAMESPACE" get secret "$secret" -o json | jq -e --arg key "$key" '.data[$key] != null and .data[$key] != ""' >/dev/null
}

require_secret() {
  local secret="$1"
  kubectl -n "$NAMESPACE" get secret "$secret" >/dev/null 2>&1 || { echo "Missing secret: $secret in namespace $NAMESPACE" >&2; exit 1; }
}

require_secret_key() {
  local secret="$1"
  local key="$2"
  secret_has_key "$secret" "$key" || { echo "Missing key '$key' in secret '$secret'" >&2; exit 1; }
}

require_cmd kubectl
require_cmd jq

kubectl get namespace "$NAMESPACE" >/dev/null 2>&1 || { echo "Missing namespace: $NAMESPACE" >&2; exit 1; }

require_secret lithoapp-secrets
require_secret keycloak-realm-secret
require_secret cloudflare-tunnel-secret

for key in \
  POSTGRES_USER POSTGRES_PASSWORD \
  PATIENT_DB_NAME EPISODE_DB_NAME ANALYSIS_DB_NAME DRAINAGE_DB_NAME NOTIFICATION_DB_NAME KEYCLOAK_DB_NAME \
  KEYCLOAK_ADMIN KEYCLOAK_ADMIN_PASSWORD \
  KC_DB_USERNAME KC_DB_PASSWORD \
  MINIO_ROOT_USER MINIO_ROOT_PASSWORD \
  STORAGE_MINIO_ACCESS_KEY STORAGE_MINIO_SECRET_KEY; do
  require_secret_key lithoapp-secrets "$key"
done

require_secret_key keycloak-realm-secret "medical-platform-realm.json"
require_secret_key cloudflare-tunnel-secret "TUNNEL_ID"
require_secret_key cloudflare-tunnel-secret "credentials.json"

lithoapp_count="$(kubectl -n "$NAMESPACE" get secret lithoapp-secrets -o json | jq '.data | length')"
keycloak_count="$(kubectl -n "$NAMESPACE" get secret keycloak-realm-secret -o json | jq '.data | length')"
cloudflare_count="$(kubectl -n "$NAMESPACE" get secret cloudflare-tunnel-secret -o json | jq '.data | length')"

echo "Secrets OK in namespace $NAMESPACE:"
echo "  lithoapp-secrets:         DATA=$lithoapp_count"
echo "  keycloak-realm-secret:    DATA=$keycloak_count"
echo "  cloudflare-tunnel-secret: DATA=$cloudflare_count"
