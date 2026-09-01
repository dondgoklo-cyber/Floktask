#!/usr/bin/env bash
# 18493542 3138424b35 41414b3b38 3d30 4334303b513d3d4b35 41383c323e3b3e324f/3f303a35424b 32 3841453e343d383a3045.
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../app/src/main/java"
echo "=== 1f403e3235403a30 3138424b45 383c3f3e40423e32 ==="
MISSING=0
for pkg in ai geofence location; do
  if grep -rln "import com.taskmanager.*\.$pkg\." . 2>/dev/null; then
    echo "1d101914153d4b35 41414b3b38 3d30 4334303b513d3d4b35 3f303a3542: $pkg"
    MISSING=1
  fi
done
[ "$MISSING" = "0" ] && echo "OK: 3138424b35 41414b3b3e3a 3d35 3d303934353d3e"
