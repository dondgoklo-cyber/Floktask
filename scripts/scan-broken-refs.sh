#!/usr/bin/env bash
# Ищет битые ссылки на удалённые символы/пакеты в исходниках.
set -euo pipefail
cd /workspace/dondgoklo-cyber__Floktask/app/src/main/java
echo "=== проверка битых импортов ==="
MISSING=0
for pkg in ai geofence location; do
  if grep -rln "import com.taskmanager.*\.$pkg\." . 2>/dev/null; then
    echo "НАЙДЕНЫ ссылки на удалённый пакет: $pkg"
    MISSING=1
  fi
done
[ "$MISSING" = "0" ] && echo "OK: битых ссылок не найдено"
