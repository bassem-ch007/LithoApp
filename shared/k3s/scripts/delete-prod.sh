#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-lithoapp}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_cmd kubectl

context="$(kubectl config current-context)"
echo "kubectl context: ${context}"
echo "This removes LithoApp workloads and services, but keeps namespace, secrets, and PVC data."
read -r -p "Continue? [y/N] " answer
case "$answer" in
  y|Y|yes|YES) ;;
  *) echo "Aborted."; exit 1 ;;
esac

kubectl -n "$NAMESPACE" delete deployment \
  postgres minio keycloak patient-service episode-service analysis-service drainage-service api-gateway frontend cloudflared \
  --ignore-not-found

kubectl -n "$NAMESPACE" delete service \
  postgres minio keycloak patient-service episode-service analysis-service drainage-service api-gateway frontend \
  --ignore-not-found

kubectl -n "$NAMESPACE" delete job minio-create-analysis-bucket --ignore-not-found
kubectl -n "$NAMESPACE" delete configmap lithoapp-common-config postgres-initdb cloudflared-config --ignore-not-found

echo "Kept PVCs and secrets. To delete all data too, delete the namespace manually:"
echo "kubectl delete namespace ${NAMESPACE}"

