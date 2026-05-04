#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
TEMPLATE="${ROOT_DIR}/shared/k3s/base/keycloak/medical-platform-realm.json.tpl"
NAMESPACE="${NAMESPACE:-lithoapp}"
REALM_NAME="${REALM_NAME:-medical-platform}"
FRONTEND_PUBLIC_URL="${FRONTEND_PUBLIC_URL:-https://lithoapp.online}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

escape_sed() {
  printf "%s" "$1" | sed -e 's/[\/&]/\\&/g'
}

require_cmd kubectl
require_cmd sed
require_cmd grep

tmp_file="$(mktemp)"
trap 'rm -f "$tmp_file"' EXIT

sed \
  -e "s/__REALM_NAME__/$(escape_sed "$REALM_NAME")/g" \
  -e "s/__FRONTEND_PUBLIC_URL__/$(escape_sed "$FRONTEND_PUBLIC_URL")/g" \
  "$TEMPLATE" > "$tmp_file"

if grep -qi "localhost" "$tmp_file"; then
  echo "Rendered Keycloak realm contains localhost; refusing to create production secret." >&2
  exit 1
fi

kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -
kubectl -n "$NAMESPACE" create secret generic keycloak-realm-secret \
  --from-file=medical-platform-realm.json="$tmp_file" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Created/updated secret keycloak-realm-secret in namespace ${NAMESPACE}."

