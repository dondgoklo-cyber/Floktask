#!/usr/bin/env bash
# OODA-цикл: самотестирование приложения в эмуляторе CI
# Plan (P): собрать APK + запустить эмулятор + проверить запуск
# Do (D):   установить APK, запустить MainActivity
# Check (C): проверить logcat на краши
# Act (A):  вывести результат и стек-трейс если есть краш
set -euo pipefail
REPO="dondgoklo-cyber/Floktask"
echo "=== OODA: Наблюдение (Observe) ==="
echo "Последний CI ран:"
gh run list --repo "$REPO" --limit 1 --json conclusion,displayTitle,databaseId -q '.[] | "\(.conclusion) \(.displayTitle) (id=\(.databaseId))"'
echo ""
echo "=== OODA: Ориентация (Orient) ==="
# Проверить свежесть APK
bash scripts/check-apk.sh 2>&1
echo ""
echo "=== PDCA: Plan ==="
echo "Цель: запустить приложение в эмуляторе, поймать краш при старте"
echo ""
echo "=== PDCA: Do ==="
echo "Эмулятор запускается через CI workflow emulator-test.yml"
echo "Проверяю последний ран эмулятора..."
EMU_RUN=$(gh run list --repo "$REPO" --workflow "emulator-test" --limit 1 --json databaseId -q '.[0].databaseId' 2>/dev/null || echo "none")
echo "Эмулятор ран: $EMU_RUN"
echo ""
echo "=== PDCA: Check ==="
if [ "$EMU_RUN" = "none" ]; then
  echo "Workflow emulator-test ещё не запускался. Нужно создать и запушить."
else
  STATUS=$(gh run view "$EMU_RUN" --repo "$REPO" --json status,conclusion -q '"\(.status) \(.conclusion)"' 2>/dev/null)
  echo "Статус: $STATUS"
  if echo "$STATUS" | grep -q "failure"; then
    echo "=== КРАШ ОБНАРУЖЕН ==="
    gh run view "$EMU_RUN" --repo "$REPO" --log-failed 2>&1 | grep -iE "FATAL|crash|exception|AndroidRuntime" | head -20
  fi
fi
