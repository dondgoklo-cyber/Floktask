#!/usr/bin/env bash
# Проверяет наличие свежего APK в Releases.
set -euo pipefail
REPO="dondgoklo-cyber/Floktask"
echo "=== APK в Releases ==="
gh release view v1.0.0-debug --repo "$REPO" --json assets,publishedAt --jq \
  '.publishedAt, (.assets[] | "\(.name)\t\((.size/1048576*100|floor)/100) MB")' 2>&1
