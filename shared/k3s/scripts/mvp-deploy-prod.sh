#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
NAMESPACE="${NAMESPACE:-lithoapp}"
DOCKERHUB_NAMESPACE="${DOCKERHUB_NAMESPACE:-bassem00}"
IMAGE_TAG="${IMAGE_TAG:-}"
OVERLAY="${OVERLAY:-${ROOT_DIR}/shared/k3s/overlays/prod}"
RENDERED_FILE="${RENDERED_FILE:-/tmp/lithoapp-prod-rendered.yaml}"
SKIP_WAIT="${SKIP_WAIT:-false}"

if [ -z "$IMAGE_TAG" ]; then
  echo "IMAGE_TAG is required. Example:" >&2
  echo "  IMAGE_TAG=abc123 DOCKERHUB_NAMESPACE=bassem00 bash shared/k3s/scripts/mvp-deploy-prod.sh" >&2
  exit 1
fi

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "Missing required command: $1" >&2; exit 1; }
}

replace_image() {
  local image="$1"
  sed -i "s|docker.io/bassem00/${image}:replace-with-git-sha|docker.io/${DOCKERHUB_NAMESPACE}/${image}:${IMAGE_TAG}|g" "$RENDERED_FILE"
}

require_cmd kubectl
require_cmd jq
require_cmd sed
require_cmd grep

kubectl get namespace "$NAMESPACE" >/dev/null 2>&1 || kubectl create namespace "$NAMESPACE"

bash "${SCRIPT_DIR}/mvp-verify-secrets.sh"

echo "Rendering kustomize overlay with kubectl kustomize: $OVERLAY"
kubectl kustomize "$OVERLAY" > "$RENDERED_FILE"

replace_image lithoapp-patient-service
replace_image lithoapp-episode-service
replace_image lithoapp-analysis-service
replace_image lithoapp-drainage-service
replace_image lithoapp-notification-service
replace_image lithoapp-api-gateway
replace_image lithoapp-frontend

if grep -q "replace-with-git-sha" "$RENDERED_FILE"; then
  echo "ERROR: replace-with-git-sha still exists after rendering/replacement." >&2
  grep -n "replace-with-git-sha" "$RENDERED_FILE" || true
  exit 1
fi

echo "Application images to deploy:"
grep -n "image: docker.io/${DOCKERHUB_NAMESPACE}/lithoapp-" "$RENDERED_FILE" || true

echo "Applying rendered manifests: $RENDERED_FILE"
kubectl apply -f "$RENDERED_FILE"

if [ "$SKIP_WAIT" = "true" ]; then
  echo "SKIP_WAIT=true, not waiting for rollouts."
  exit 0
fi

# Infrastructure first, then app. This makes failures easier to understand on small servers.
for deployment in postgres minio keycloak patient-service episode-service analysis-service drainage-service notification-service api-gateway frontend cloudflared; do
  echo "Waiting for rollout: $deployment"
  kubectl -n "$NAMESPACE" rollout status "deployment/${deployment}" --timeout=10m
done

# The MinIO job may already be complete from a previous deploy. This is fine.
kubectl -n "$NAMESPACE" wait --for=condition=complete job/minio-create-analysis-bucket --timeout=5m || {
  echo "MinIO bucket job did not complete in time. Inspect with:" >&2
  echo "  kubectl -n ${NAMESPACE} logs job/minio-create-analysis-bucket" >&2
}

echo "Deployment status:"
kubectl -n "$NAMESPACE" get pods -o wide
kubectl -n "$NAMESPACE" get svc
