#!/bin/bash

# =============================================================================
# Floktask APK Build Script
# Собирает Debug и Release APK с автоматическим увеличением версии
# =============================================================================

set -e

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Функция для вывода сообщений
log() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

# Проверяем наличие gradle
if ! command -v ./gradlew &> /dev/null; then
    error "gradle wrapper not found. Run from project root."
fi

# Очистка
log "Cleaning project..."
./gradlew clean

# Тип сборки (по умолчанию debug)
BUILD_TYPE="${1:-debug}"

case "$BUILD_TYPE" in
    debug|Debug)
        log "Building DEBUG APK..."
        ./gradlew assembleDebug --stacktrace --no-daemon
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
        ;;
    release|Release)
        log "Building RELEASE APK..."
        ./gradlew assembleRelease --stacktrace --no-daemon
        APK_PATH="app/build/outputs/apk/release/app-release.apk"
        ;;
    *)
        error "Invalid build type. Use: debug or release"
        ;;
esac

# Проверяем что APK собран
if [ ! -f "$APK_PATH" ]; then
    error "APK not found at $APK_PATH"
fi

# Получаем информацию о коммите
COMMIT_HASH=$(git rev-parse --short HEAD)
COMMIT_MSG=$(git log -1 --pretty=%s | head -c 50)
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")

# Создаём папку для релиза
mkdir -p release

# Переименовываем APK
if [ "$BUILD_TYPE" = "debug" ]; then
    OUTPUT_APK="release/Floktask-v${GITHUB_RUN_NUMBER:-0}-debug-${TIMESTAMP}.apk"
else
    OUTPUT_APK="release/Floktask-v${GITHUB_RUN_NUMBER:-0}-release-${TIMESTAMP}.apk"
fi

cp "$APK_PATH" "$OUTPUT_APK"

# Выводим информацию
success "APK built successfully!"
echo ""
echo "📦 APK Info:"
echo "   Location: $OUTPUT_APK"
echo "   Size: $(du -h "$OUTPUT_APK" | cut -f1)"
echo "   Commit: $COMMIT_HASH"
echo "   Message: $COMMIT_MSG"
echo ""
echo "📱 To install on device:"
echo "   adb install $OUTPUT_APK"
echo ""
echo "💾 To copy to phone:"
echo "   scp $OUTPUT_APK phone:/sdcard/Download/"
