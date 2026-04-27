#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPO_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../../.." && pwd)

cd "$REPO_ROOT"

docker build -t lithoapp/patient-service:local ./services/patient-service
docker build -t lithoapp/episode-service:local ./services/episode-service
docker build -t lithoapp/analysis-service:local ./services/analysis-service
docker build -t lithoapp/drainage-service:local ./services/drainage-service
docker build -t lithoapp/api-gateway:local ./services/api-gateway
docker build -t lithoapp/frontend:local ./frontend/frontLitho

