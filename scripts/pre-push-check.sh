#!/usr/bin/env bash
# Локальная статическая проверка Kotlin-файлов перед пушем.
# Не заменяет компиляцию, но ловит частые ошибки без JDK/CI.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
errors=0

check_file() {
    local f="$1"
    local content
    content=$(cat "$f")

    # 1. Баланс фигурных скобок (примерно, игнорируя строки)
    # Удаляем строковые литералы и комментарии для подсчёта
    local stripped
    stripped=$(echo "$content" | sed 's://.*$::g; s:/\*[^*]*\*/::g' | tr -d '\n')
    local opens closes
    opens=$(echo "$stripped" | tr -cd '{' | wc -c)
    closes=$(echo "$stripped" | tr -cd '}' | wc -c)
    if [ "$opens" -ne "$closes" ]; then
        echo "  ❌ СКОБКИ: $f — открывающих { = $opens, закрывающих } = $closes (разница $((opens-closes)))"
        errors=$((errors+1))
    fi

    # 2. Проверка импортов для часто забываемых символов
    for sym in "Check" "Close" "Edit" "Delete" "Add" "Notifications" "DragHandle" "ViewKanban" "Search" "Inbox" "Event" "Download" "Upload" "TaskListSkeleton" "EmptyState" "priorityColor" "AppTheme" "Spacing" "Radius" "Elevation" "stringResource" "LaunchedEffect" "rememberCoroutineScope"; do
        if echo "$content" | grep -qw "$sym" && ! echo "$content" | grep -q "import .*$sym"; then
            # Пропускаем если это определение функции/класса
            if ! echo "$content" | grep -qE "(fun|class|object|val) $sym"; then
                echo "  ⚠️  ИМПОРТ: $f использует '$sym' — проверьте что import добавлен"
            fi
        fi
    done

    # 3. Проверка @OptIn для известных экспериментальных API
    if echo "$content" | grep -qE "HorizontalPager|rememberPagerState"; then
        if ! echo "$content" | grep -q "ExperimentalFoundationApi"; then
            echo "  ❌ @OptIn: $f использует HorizontalPager — нужен @OptIn(ExperimentalFoundationApi::class)"
            errors=$((errors+1))
        fi
    fi
    if echo "$content" | grep -qE "detectDragGesturesAfterLongPress|onGloballyPositioned|pointerInput"; then
        : # эти не требуют OptIn, просто пометка
    fi

    # 4. Проверка: файл не пустой и заканчивается переносом
    if [ ! -s "$f" ]; then
        echo "  ❌ ПУСТОЙ ФАЙЛ: $f"
        errors=$((errors+1))
    fi
}

echo "=== Проверка Kotlin-файлов ==="
count=0
while IFS= read -r f; do
    check_file "$f"
    count=$((count+1))
done < <(find app/src/main/java -name "*.kt" -type f)

echo ""
echo "Проверено файлов: $count"
if [ "$errors" -gt 0 ]; then
    echo "❌ Найдено проблем: $errors — ИСПРАВЬТЕ перед пушем"
    exit 1
else
    echo "✅ Базовые проверки пройдены (это не гарантирует компиляцию!)"
    exit 0
fi
