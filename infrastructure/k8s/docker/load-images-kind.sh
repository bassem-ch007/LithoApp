#!/usr/bin/env sh
set -eu

KIND_CLUSTER_NAME=${KIND_CLUSTER_NAME:-lithoapp-cluster}

kind load docker-image lithoapp/patient-service:local --name "$KIND_CLUSTER_NAME"
kind load docker-image lithoapp/episode-service:local --name "$KIND_CLUSTER_NAME"
kind load docker-image lithoapp/analysis-service:local --name "$KIND_CLUSTER_NAME"
kind load docker-image lithoapp/drainage-service:local --name "$KIND_CLUSTER_NAME"
kind load docker-image lithoapp/api-gateway:local --name "$KIND_CLUSTER_NAME"
kind load docker-image lithoapp/frontend:local --name "$KIND_CLUSTER_NAME"

