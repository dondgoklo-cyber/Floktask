#!/usr/bin/env bash
# Коммитит и пушит изменения с сообщением. Перед пушем запускает pre-push-check.
# Использование: ./scripts/commit-push.sh "сообщение коммита"
set -euo pipefail
REPO="dondgoklo-cyber/Floktask"
BRANCH="vibe/taskmanager-scaffold-8c6512"
MSG="${1:-обновление}"
cd /workspace/dondgoklo-cyber__Floktask

echo "=== Предпроверка ==="
if ! bash scripts/pre-push-check.sh; then
    echo "❌ Предпроверка не пройдена — пуши отменён. Исправьте ошибки выше."
    exit 1
fi

git add -A
git commit -m "$MSG"
git push origin "$BRANCH" 2>&1 | tail -2
sleep 20
RUN_ID=$(gh run list --repo "$REPO" --limit 1 --json databaseId -q '.[0].databaseId')
echo "CI ран: $RUN_ID"
