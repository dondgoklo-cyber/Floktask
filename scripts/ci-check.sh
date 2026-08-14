#!/usr/bin/env bash
# Запускает CI и ждёт результат. Возвращает conclusion.
# Использование: ./scripts/ci-check.sh [run_id]
set -euo pipefail
REPO="dondgoklo-cyber/Floktask"
if [ -z "${1:-}" ]; then
  RUN_ID=$(gh run list --repo "$REPO" --limit 1 --json databaseId -q '.[0].databaseId')
else
  RUN_ID="$1"
fi
echo "Жду завершения рана $RUN_ID..."
for i in $(seq 1 60); do
  STATUS=$(gh run view "$RUN_ID" --repo "$REPO" --json status -q '.status' 2>/dev/null)
  if [ "$STATUS" = "completed" ]; then break; fi
  sleep 20
done
CONCLUSION=$(gh run view "$RUN_ID" --repo "$REPO" --json conclusion -q '.conclusion')
echo "Результат: $CONCLUSION"
if [ "$CONCLUSION" = "failure" ]; then
  echo "=== ошибки компиляции ==="
  gh run view "$RUN_ID" --repo "$REPO" --log-failed 2>&1 | grep -E "e: |error:|Unresolved|FAILED|Caused by" | grep -viE "Deprecated" | head -20
fi
[ "$CONCLUSION" = "success" ]
