#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
OVERLAY="${ROOT_DIR}/shared/k3s/overlays/prod"
NAMESPACE="${NAMESPACE:-lithoapp}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_cmd kubectl
require_cmd grep

context="$(kubectl config current-context)"
echo "kubectl context: ${context}"
read -r -p "Apply LithoApp production manifests to this context? [y/N] " answer
case "$answer" in
  y|Y|yes|YES) ;;
  *) echo "Aborted."; exit 1 ;;
esac

kubectl apply -f "${ROOT_DIR}/shared/k3s/base/namespace.yaml"

for secret in lithoapp-secrets keycloak-realm-secret cloudflare-tunnel-secret; do
  if ! kubectl -n "$NAMESPACE" get secret "$secret" >/dev/null 2>&1; then
    echo "Missing required secret ${secret} in namespace ${NAMESPACE}." >&2
    echo "Create secrets first; see shared/k3s/README.md." >&2
    exit 1
  fi
done

if kubectl kustomize "$OVERLAY" | grep -q "replace-with-git-sha"; then
  echo "One or more application images still use replace-with-git-sha." >&2
  echo "Set immutable DockerHub tags in shared/k3s/overlays/prod/kustomization.yaml before applying." >&2
  exit 1
fi

kubectl apply -k "$OVERLAY"

for deployment in postgres minio keycloak patient-service episode-service analysis-service drainage-service notification-service api-gateway frontend cloudflared; do
  kubectl -n "$NAMESPACE" rollout status "deployment/${deployment}" --timeout=10m
done

kubectl -n "$NAMESPACE" wait --for=condition=complete job/minio-create-analysis-bucket --timeout=5m || {
  echo "MinIO bucket job did not complete within 5 minutes. Inspect with:" >&2
  echo "kubectl -n ${NAMESPACE} logs job/minio-create-analysis-bucket" >&2
}

kubectl -n "$NAMESPACE" get pods -o wide
kubectl -n "$NAMESPACE" get services

