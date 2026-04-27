#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPO_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../../.." && pwd)

"$SCRIPT_DIR/build-images.sh"
"$SCRIPT_DIR/load-images-kind.sh"

cd "$REPO_ROOT"

printf '\nImages are built and loaded into Kind.\n'
printf 'Next command:\n'
printf 'kubectl apply -k infrastructure/k8s/local\n'

