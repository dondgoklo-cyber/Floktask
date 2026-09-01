# 🔍 Полный аудит кода приложения Floktask

## 📋 Общая информация

- **Дата аудита:** 2025-01-01
- **Анализируемая версия:** main (коммит: не указан)
- **Общее количество файлов Kotlin:** 245
- **Общее количество тестов:** 17
- **Пакеты:** com.taskmanager.*

---

## 🚨 КРИТИЧЕСКИЕ ПРОБЛЕМЫ

### 1. Мертвый код (Dead Code)

#### 🔴 Высокий приоритет - Удаление обязательно

##### 1.1. Папка `work/` - Полностью неиспользуемая
- **Файл:** `app/src/main/java/com/taskmanager/work/WorkConstraints.kt`
- **Проблема:** Класс `WorkConstraints` не используется нигде в проекте
- **Доказательства:** 
  - `grep -r "WorkConstraints"` возвращает только определение самого класса
  - `grep -r "androidx.work"` возвращает только импорты в этом файле
  - Нет импортов `com.taskmanager.work.*` в других файлах
- **Решение:** ✅ **УДАЛИТЬ** всю папку `work/`

##### 1.2. FinanceDraft в voice пакете - Частично неиспользуемый
- **Файл:** `app/src/main/java/com/taskmanager/voice/TaskDraft.kt`
- **Проблема:** Класс `FinanceDraft` определен, но не используется
- **Доказательства:**
  - `grep -r "FinanceDraft"` показывает только определение и использование в `RussianVoiceParser.parseFinance()`
  - Функция `parseFinance()` в `RussianVoiceParser` не используется нигде в проекте
  - Нет вызовов `parseFinance()` в коде
- **Решение:** 
  - ✅ **УДАЛИТЬ** класс `FinanceDraft` из `TaskDraft.kt`
  - ✅ **УДАЛИТЬ** функцию `parseFinance()` из `RussianVoiceParser.kt`

##### 1.3. LocalHapticManager - Избыточная абстракция
- **Файл:** `app/src/main/java/com/taskmanager/haptic/LocalHapticManager.kt`
- **Проблема:** 
  - Создает новый экземпляр `UserPrefs` и `HapticManager` при каждом вызове
  - Не использует DI (Hilt)
  - `HapticManager` уже есть в DI модуле (`AppModule.kt`)
- **Доказательства:**
  - `LocalHapticManager` используется в нескольких экранах
  - Но `HapticManager` уже инжектится через Hilt
- **Решение:** 
  - ✅ Заменить использование `rememberHaptic()` на инжекцию `HapticManager` через Hilt
  - ✅ **УДАЛИТЬ** файл `LocalHapticManager.kt`

#### 🟡 Средний приоритет - Рекомендуется удалить

##### 1.4. LocalExchangeRateProvider - Не используется
- **Файл:** `app/src/main/java/com/taskmanager/data/repository/LocalExchangeRateProvider.kt`
- **Проблема:** Провайдер захардкожен, но не используется в реальных сценариях
- **Доказательства:**
  - Присутствует в `AppModule.kt` как провайдер
  - Нет вызовов методов этого класса в коде
- **Решение:** Удалить из `AppModule.kt` если не планируется интеграция с реальным API

---

## 🐛 ОШИБКИ И БАГИ

### 2.1. Ошибки в AlarmScheduler

#### 2.1.1. Потенциальная утечка памяти
- **Файл:** `app/src/main/java/com/taskmanager/notification/AlarmScheduler.kt`
- **Проблема:** 
  ```kotlin
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  ```
  - Создается корутин скоуп без привязки к жизненному циклу
  - Может вызвать утечку памяти
- **Решение:** 
  ```kotlin
  @Singleton
  class AlarmScheduler @Inject constructor(
      @ApplicationContext private val context: Context,
      private val taskDao: TaskDao,
      @ApplicationScope private val scope: CoroutineScope  // Добавить в DI
  )
  ```

#### 2.1.2. Не обрабатывается ошибка отмены PendingIntent
- **Файл:** `app/src/main/java/com/taskmanager/notification/AlarmScheduler.kt`
- **Проблема:** В `cancelReminder()` не проверяется успешность отмены
- **Решение:** Добавить проверку возвращаемого значения

### 2.2. Проблемы в UserPrefs (Безопасность)

#### 2.2.1. Небезопасное хэширование PIN
- **Файл:** `app/src/main/java/com/taskmanager/security/UserPrefs.kt`
- **Проблема:**
  ```kotlin
  private fun hashPin(pin: String): String {
      var hash = 0
      for (c in pin) {
          hash = hash * 31 + c.code
      }
      return hash.toString()
  }
  ```
  - Простой хэш без соли
  - Уязвим для радужных таблиц
  - Легко подбирается перебором
- **Решение:** 
  ```kotlin
  // Использовать Android Keystore или хотя бы SHA-256 с солью
  private fun hashPin(pin: String): String {
      val bytes = pin.toByteArray(Charsets.UTF_8)
      val md = MessageDigest.getInstance("SHA-256")
      val digest = md.digest(bytes)
      return digest.joinToString("") { "%02x".format(it) }
  }
  ```

#### 2.2.2. Нет ограничения на длину PIN
- **Проблема:** Можно установить PIN любой длины
- **Решение:** Добавить валидацию (4-6 цифр)

### 2.3. Проблемы в RussianVoiceParser

#### 2.3.1. Ошибка в парсинге дат
- **Файл:** `app/src/main/java/com/taskmanager/voice/RussianVoiceParser.kt`
- **Проблема:** 
  ```kotlin
  // В функции nextDayOfWeek
  if (skipCurrent && date.dayOfWeek == target && date == from) {
      date = date.plusWeeks(1)
  }
  ```
  - Логика избыточна и потенциально ошибочна
  - Может вызвать бесконечный цикл в некоторых случаях
- **Решение:** Упростить логику

#### 2.3.2. Не обрабатываются null значения
- **Проблема:** В `parseTime()`, `parseDate()` и других функциях не все пути возвращают значение
- **Решение:** Добавить дефолтные значения

#### 2.3.3. Ошибка в regex для дат
- **Проблема:** 
  ```kotlin
  val shortDateRegex = Regex("""\b(\d{1,2})\.(\d{1,2})(?:\.(\d{2,4}))?\b""")
  ```
  - Не корректно обрабатывает даты вида "1.1.25" (пропускает год)
- **Решение:** Исправить regex

### 2.4. Проблемы в PinScreen

#### 2.4.1. Нет ограничения на количество попыток
- **Проблема:** Можно бесконечно пытаться ввести PIN
- **Решение:** Добавить ограничение (например, 5 попыток)

#### 2.4.2. Нет блокировки после неудачных попыток
- **Решение:** Добавить временную блокировку

#### 2.4.3. PIN хранится в plain text во время ввода
- **Проблема:** `input` и `firstPin` хранятся как String
- **Решение:** Использовать CharArray и очищать после использования

### 2.5. Проблемы в VoiceTaskSheet

#### 2.5.1. Утечка SpeechRecognizer
- **Файл:** `app/src/main/java/com/taskmanager/presentation/screens/voice/VoiceTaskSheet.kt`
- **Проблема:** 
  ```kotlin
  val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
  ```
  - Не вызывается `destroy()` при закрытии экрана
  - Может вызвать утечку ресурсов
- **Решение:** 
  ```kotlin
  DisposableEffect(Unit) {
      onDispose {
          speechRecognizer.destroy()
      }
  }
  ```

#### 2.5.2. Нет проверки на null для context
- **Проблема:** `LocalContext.current` может быть null в некоторых случаях
- **Решение:** Добавить проверку

---

## ⚠️ ПРОБЛЕМЫ В WORKFLOW

### 3.1. GitHub Actions Workflow

#### 3.1.1. Отсутствует workflow для тестов
- **Файл:** `.github/workflows/build.yml`
- **Проблема:** 
  - Только сборка APK
  - Нет запуска тестов
  - Нет проверки кода (lint, detekt)
- **Решение:** Добавить stages для:
  ```yaml
  - name: Run tests
    run: ./gradlew testDebugUnitTest
  
  - name: Run lint
    run: ./gradlew lintDebug
  ```

#### 3.1.2. Жестко захардкожена версия APK
- **Проблема:** 
  ```yaml
  cp app/build/outputs/apk/debug/app-debug.apk release/TaskManager-v1.1.0-debug.apk
  ```
  - Версия жестко прописана
- **Решение:** Использовать переменную из tag или generate dynamically

#### 3.1.3. Нет кэширования Gradle
- **Проблема:** 
  ```yaml
  cache-disabled: true
  ```
  - Отключено кэширование
- **Решение:** Включить кэширование для ускорения сборки

#### 3.1.4. Нет сборки release APK
- **Проблема:** Собирается только debug APK
- **Решение:** Добавить сборку release версии

### 3.2. Отсутствуют другие workflow

#### 3.2.1. Нет workflow для PR
- **Решение:** Создать workflow для проверки PR:
  - Запуск тестов
  - Проверка lint
  - Проверка кодстайла

#### 3.2.2. Нет workflow для статического анализа
- **Решение:** Добавить Detekt, Ktlint

---

## 📜 ПРОБЛЕМЫ В СКРИПТАХ

### 4.1. Скрипты в папке `scripts/`

#### 4.1.1. `ooda-test.sh` - Ссылка на несуществующий workflow
- **Файл:** `scripts/ooda-test.sh`
- **Проблема:** 
  ```bash
  EMU_RUN=$(gh run list --repo "$REPO" --workflow "emulator-test" --limit 1 --json databaseId -q '.[0].databaseId' 2>/dev/null || echo "none")
  ```
  - Workflow `emulator-test.yml` не существует в репозитории
  - Это вызывает ошибку при выполнении
- **Решение:** 
  - ✅ **УДАЛИТЬ** ссылку на несуществующий workflow
  - Или создать workflow `emulator-test.yml`

#### 4.1.2. Жестко захардкоженные пути
- **Проблема:** Во всех скриптах:
  ```bash
  cd /workspace/dondgoklo-cyber__Floktask
  ```
  - Путь жестко захардкожен
  - Не будет работать в других средах
- **Решение:** Использовать относительные пути или переменные окружения

#### 4.1.3. `commit-push.sh` - Жестко захардкожена ветка
- **Файл:** `scripts/commit-push.sh`
- **Проблема:** 
  ```bash
  BRANCH="vibe/taskmanager-scaffold-8c6512"
  ```
  - Ветка жестко прописана
- **Решение:** Принимать ветку как параметр

#### 4.1.4. Нет обработки ошибок в скриптах
- **Проблема:** Скрипты не проверяют коды возврата команд
- **Решение:** Добавить проверки `set -e` и обработку ошибок

#### 4.1.5. `scan-broken-refs.sh` - Неполная проверка
- **Файл:** `scripts/scan-broken-refs.sh`
- **Проблема:** 
  ```bash
  for pkg in ai geofence location; do
  ```
  - Проверяет только 3 пакета
  - Не проверяет все возможные импорты
- **Решение:** Расширить список или сделать динамическим

---

## 🗃️ ПРОБЛЕМЫ В КОНФИГУРАЦИОННЫХ ФАЙЛАХ

### 5.1. Gradle конфигурация

#### 5.1.1. Отсутствует конфигурация для WorkManager
- **Проблема:** Используется `androidx.work`, но зависимость не подключена
- **Доказательства:** 
  - `WorkConstraints.kt` использует `androidx.work.Constraints`
  - Но в `build.gradle.kts` нет зависимости
- **Решение:** Добавить в `app/build.gradle.kts`:
  ```kotlin
  implementation("androidx.work:work-runtime-ktx:2.9.0")
  ```
  **ИЛИ** удалить `WorkConstraints.kt` (рекомендуется, так как он не используется)

#### 5.1.2. Версии зависимостей
- **Файл:** `gradle/libs.versions.toml`
- **Проблема:** Не проверялся на актуальность
- **Решение:** Обновить версии зависимостей

### 5.2. AndroidManifest.xml

#### 5.2.1. Отсутствуют разрешения для WorkManager
- **Проблема:** Если будет использоваться WorkManager, нужны разрешения
- **Решение:** Добавить в манифест (если будет использоваться):
  ```xml
  <uses-permission android:name="android.permission.WAKE_LOCK" />
  ```

#### 5.2.2. Отсутствует declare для BroadcastReceiver
- **Проблема:** `AlarmReceiver` и `BootReceiver` не объявлены в манифесте
- **Доказательства:** 
  - Классы существуют
  - Но их нет в AndroidManifest.xml
- **Решение:** Добавить в `AndroidManifest.xml`:
  ```xml
  <receiver android:name=".notification.AlarmReceiver" android:exported="false" />
  <receiver android:name=".notification.BootReceiver" android:exported="false">
      <intent-filter>
          <action android:name="android.intent.action.BOOT_COMPLETED" />
          <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
          <action android:name="android.intent.action.QUICKBOOT_POWERON" />
      </intent-filter>
  </receiver>
  ```

---

## 🎯 РЕКОМЕНДАЦИИ ПО УЛУЧШЕНИЮ

### 6.1. Архитектура

#### 6.1.1. Разделение на модули
- **Проблема:** Весь код в одном модуле `app`
- **Решение:** Разделить на:
  - `:core` - domain, data
  - `:feature:tasks` - задачи
  - `:feature:finance` - финансы
  - `:feature:voice` - голосовой ввод
  - `:feature:notification` - уведомления

#### 6.1.2. Использование Clean Architecture
- **Текущее состояние:** Частично реализовано
- **Решение:** Четко разделить на:
  - Presentation (UI)
  - Domain (Use Cases, Models)
  - Data (Repository, Data Sources)

### 6.2. Тестирование

#### 6.2.1. Низкий покрытие кода тестами
- **Проблема:** Только 17 тестов для 245 файлов
- **Решение:** Добавить тесты для:
  - ViewModel
  - Use Cases
  - Repository
  - Mappers

#### 6.2.2. Нет UI тестов
- **Решение:** Добавить Compose тесты

### 6.3. Качество кода

#### 6.3.1. Длинные функции
- **Проблема:** `RussianVoiceParser.parse()` - 430 строк
- **Решение:** Разбить на меньшие функции

#### 6.3.2. Дублирование кода
- **Проблема:** Одинаковые regex паттерны в разных местах
- **Решение:** Вынести в константы или утилитарные классы

#### 6.3.3. Магические числа
- **Проблема:** 
  ```kotlin
  hash = hash * 31 + c.code
  ```
  - 31 - магическое число
- **Решение:** Вынести в константу с комментарием

### 6.4. Безопасность

#### 6.4.1. Хранение чувствительных данных
- **Проблема:** PIN хранится в SharedPreferences
- **Решение:** Использовать Android Keystore или EncryptedSharedPreferences

#### 6.4.2. Нет проверки SSL pinning
- **Решение:** Добавить SSL pinning для сетевых запросов

---

## 📊 СТАТИСТИКА

### Обнаруженные проблемы:
- **Критические (удалить):** 3
- **Высокого приоритета:** 5
- **Среднего приоритета:** 8
- **Низкого приоритета:** 10+

### Файлы для удаления:
1. `app/src/main/java/com/taskmanager/work/WorkConstraints.kt` (и вся папка work/)
2. Класс `FinanceDraft` из `TaskDraft.kt`
3. Функция `parseFinance()` из `RussianVoiceParser.kt`
4. `app/src/main/java/com/taskmanager/haptic/LocalHapticManager.kt`

### Файлы для исправления:
1. `UserPrefs.kt` - исправить хэширование
2. `AlarmScheduler.kt` - исправить корутин скоуп
3. `VoiceTaskSheet.kt` - добавить cleanup
4. `RussianVoiceParser.kt` - исправить логику парсинга
5. `PinScreen.kt` - добавить ограничение попыток
6. `.github/workflows/build.yml` - добавить тесты и lint

---

## ✅ ЧЕК-ЛИСТ ИСПРАВЛЕНИЙ

- [ ] Удалить папку `work/`
- [ ] Удалить `FinanceDraft` и `parseFinance()`
- [ ] Удалить `LocalHapticManager.kt`
- [ ] Исправить хэширование PIN в `UserPrefs.kt`
- [ ] Исправить корутин скоуп в `AlarmScheduler.kt`
- [ ] Добавить cleanup в `VoiceTaskSheet.kt`
- [ ] Исправить логику парсинга в `RussianVoiceParser.kt`
- [ ] Добавить ограничение попыток в `PinScreen.kt`
- [ ] Добавить тесты в workflow
- [ ] Исправить hardcoded пути в скриптах
- [ ] Добавить разрешения в AndroidManifest.xml
- [ ] Проверить и исправить зависимости в build.gradle

---

## 🎯 ПРИОРИТЕТЫ

### 🔴 Немедленное исправление (Проблемы безопасности и критических багов):
1. Исправить хэширование PIN
2. Добавить cleanup для SpeechRecognizer
3. Добавить разрешения в манифест

### 🟡 Высокий приоритет (Удаление мертвого кода):
1. Удалить папку `work/`
2. Удалить неиспользуемый код в voice пакете
3. Удалить LocalHapticManager

### 🟢 Средний приоритет (Улучшение workflow и скриптов):
1. Исправить workflow
2. Исправить скрипты
3. Добавить тесты

---

*Отчет создан автоматически. Для уточнений обратитесь к разработчикам.*
