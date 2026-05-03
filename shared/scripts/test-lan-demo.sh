#!/usr/bin/env bash
set -euo pipefail

SERVER_LAN_IP="${1:-192.168.1.13}"

echo "Testing local server access..."
curl -fsS "http://127.0.0.1:8088" | head

echo "Testing LAN IP access from server..."
curl -fsS "http://${SERVER_LAN_IP}:8088" | head || true

echo "Now test from your Lenovo:"
echo "ping ${SERVER_LAN_IP}"
echo "curl http://${SERVER_LAN_IP}:8088"
echo "open http://${SERVER_LAN_IP}:8088 in browser"
