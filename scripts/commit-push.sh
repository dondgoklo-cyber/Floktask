#!/usr/bin/env bash
# 1a3e3c3c38423842 38 3f43483842 38373c353d353d384f 41 413e3e3149353d38353c. 1f35403534 3f4348353c 37303f43413a3042 pre-push-check.
# 18413f3e3b4c373e32303d3835: ./scripts/commit-push.sh "413e3e3149353d3835 3a3e3c3c384230"
set -euo pipefail
REPO="dondgoklo-cyber/Floktask"
BRANCH="${2:-vibe/taskmanager-scaffold-8c6512}"
MSG="${1:-\u00043e\u000431\u00043d\u00043e\u000432\u00043b\u000435\u00043d\u000438\u000435}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

echo "=== 1f4035343f403e3235403a30 ==="
if ! bash scripts/pre-push-check.sh; then
    echo "\u274c 1f4035343f403e3235403a30 3d35 3f403e3934353d30 20413b353447353d4b4b. 18413f3e3b4c373e32303b4c4235 3e4838313a38 324b4835."
    exit 1
fi

git add -A
git commit -m "$MSG"
git push origin "$BRANCH" 2>&1 | tail -2
sleep 20
RUN_ID=$(gh run list --repo "$REPO" --limit 1 --json databaseId -q '.[0].databaseId')
echo "CI 40303d: $RUN_ID"
