#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-lithoapp}"

echo "Namespace: $NAMESPACE"
echo
kubectl -n "$NAMESPACE" get deployments || true
echo
kubectl -n "$NAMESPACE" get pods -o wide || true
echo
kubectl -n "$NAMESPACE" get svc || true
echo
kubectl -n "$NAMESPACE" get jobs || true
echo
kubectl -n "$NAMESPACE" get pvc || true
echo
kubectl -n "$NAMESPACE" get events --sort-by=.lastTimestamp | tail -80 || true
