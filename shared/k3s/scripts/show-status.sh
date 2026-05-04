#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-lithoapp}"

if ! command -v kubectl >/dev/null 2>&1; then
  echo "Missing required command: kubectl" >&2
  exit 1
fi

echo "kubectl context: $(kubectl config current-context)"
kubectl -n "$NAMESPACE" get deployments
kubectl -n "$NAMESPACE" get pods -o wide
kubectl -n "$NAMESPACE" get services
kubectl -n "$NAMESPACE" get jobs
kubectl -n "$NAMESPACE" get pvc

