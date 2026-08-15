# ТЗ ДЛЯ СЛЕДУЮЩЕЙ СЕССИИ
## WOLFTASK — Personal Life OS: Tasks, Finance, Notes, Habits

---

## 0. КАК НАЧАТЬ

- GitHub: `https://github.com/dondgoklo-cyber/Floktask`
- Ветвь: `vibe/taskmanager-scaffold-8c6512`
- PR: `https://github.com/dondgoklo-cyber/Floktask/pull/1`
- Локальный путь: `/workspace/dondgoklo-cyber__Floktask`
- `cd /workspace/dondgoklo-cyber__Floktask && git pull origin vibe/taskmanager-scaffold-8c6512`
- **НЕТ локальной сборки** (нет JDK/Android SDK/gradlew) — CI единственный верификатор (~5 мин/цикл)
- **ВСЕГДА** запускай `bash scripts/pre-push-check.sh` перед пушем
- **ПРИЛОЖЕНИЕ НАЗЫВАЕТСЯ WOLFTASK** (app_name в strings.xml)
- **ЯЗЫК ИНТЕРФЕЙСА: ТОЛЬКО РУССКИЙ**
- Файлы часто в одну строку — search_replace не работает, использовать python3 или cat >

---

## 1. ЧТО УЖЕ СДЕЛАНО

### Базовый функционал (сессии 1-7)
- Clean Architecture + MVVM, Compose, Room, Hilt, Navigation Compose
- 11 сущностей БД (DB v10), 196+ Kotlin-файлов
- Inbox, Upcoming, Search, Onboarding, Kanban, Eisenhower, Focus↔Task
- Напоминания (AlarmManager + BootReceiver)
- Подзадачи 5 уровней (parentSubtaskId)
- PIN-код, имя пользователя, приветствие
- Экспорт/импорт JSON (BackupManager)
- Теги хранятся как JSON-строка в TaskEntity.tags

### Сессия 8 — все модули 14-19
- **Модуль 14: Notes & Knowledge Base** ✅
  - Note/NoteFolder models, NoteEntity/NoteFolderEntity (DB v9)
  - NoteDao (CRUD, search, pin, archive, move), NoteFolderDao
  - NoteEditScreen: Markdown editor (Edit/Preview toggle), toolbar, autosave (debounce 800ms)
  - NotesScreen: pinned + recent + folders, FAB create, pin toggle, delete
  - Notes в bottomNav (заменил Focus), Focus в More
  - NoteExportManager: Markdown export (.md)
  - Project↔Note: вкладка Notes в ProjectDetail
  - Task↔Note: Related Notes в TaskDetailSheet
  - Search: Notes search (по title + contentMarkdown)
  - Dashboard: NotesPreviewCard (3 recent notes)
- **Модуль 15: Haptic Feedback** ✅
  - HapticManager (LIGHT/SELECTION/SUCCESS/WARNING), UserPrefs.hapticEnabled
  - SettingsScreen switch, rememberHaptic() composable
  - Применён в NotesScreen, FinanceScreen, CreateMenuSheet
- **Модуль 16: Multi-currency Finance** ✅
  - Transaction.currency, TRANSFER type, toAccountId/destinationAmount/destinationCurrency
  - TransactionDao: TRANSFER excluded from income/expense, CurrencyTotal
  - ExchangeRateProvider (LocalExchangeRateProvider, mock rates RUB/USD/EUR/GBP)
  - UserPrefs.baseCurrency, SettingsScreen selector
  - FinanceViewModel: balanceInBaseCurrency, balancesByCurrency, baseCurrency
  - FinanceScreen: BalanceCard (multi-currency), PeriodSelector, PeriodSummary,
    CategoryBreakdown, AnalyticsCard, TransactionRow, export CSV/JSON, import JSON
  - AddTransactionSheet: currency selector, type toggle (Income/Expense)
  - FinanceExportManager: CSV + JSON export, JSON import с валидацией
  - Transaction delete: long-press + confirmation dialog
  - FinanceDataSeeder: 16 категорий по умолчанию + Основной счёт
- **Модуль 17: Premium Visual System** ✅ (частично)
  - EmptyState: premium gradient-контейнер (80dp, Brush.linearGradient)
  - PrimaryButton: press scale 0.96f, 150ms tween
  - AppFloatingActionButton: press scale 0.92f, 150ms tween
  - ProgressCard: subtle gradient background
  - FinanceSummaryCard: subtle gradient
- **Модуль 18: Voice Task Creation** ✅
  - RussianVoiceParser: rule-based (НЕ AI), даты/время/приоритет/теги/проект/повтор
  - TaskDraft model, VoiceTaskSheet (SpeechRecognizer ru-RU, draft preview, manual edit)
  - CreateMenuSheet: «Задача голосом» (Mic icon)
- **Модуль 19: Russian as Primary Language** ✅
  - 100% UI на русском, voice parser ru-RU
  - app_name → WOLFTASK
- **CreateMenuSheet**: универсальная кнопка «+» (Задача/Привычка/Доход/Расход/Проект/Заметка/Голосом)

CI: Build & Test — success (4a675ca)

---

## 2. ЧТО НУЖНО СДЕЛАТЬ

### ПРИОРИТЕТ 1 — Transaction Edit
- Сейчас транзакции можно создавать и удалять, но НЕ редактировать
- Нужно: AddTransactionSheet должна работать в режиме редактирования
- При клике на TransactionRow → открыть AddTransactionSheet с предзаполненными данными
- После сохранения → updateTransaction

### ПРИОРИТЕТ 2 — Budgets
- Пользователь может установить бюджет по категории на месяц
- Показывать: 18 500 / 25 000 ₽ (74%)
- BudgetEntity (id, categoryId, amount, period, month/year)
- BudgetDao + Repository + UseCases
- FinanceScreen: бюджет-прогресс для каждой категории
- НЕ заставлять создавать бюджеты

### ПРИОРИТЕТ 3 — Financial Goals
- Goal: id, title, targetAmount, savedAmount, currency, deadline
- Progress bar: 120 000 / 250 000 ₽ (48%)
- GoalDao + Repository + UseCases
- FinanceScreen: блок Goals
- Сначала только архитектурная возможность

### ПРИОРИТЕТ 4 — Premium Visual System (продолжение)
- Векторные иллюстрации для empty states (через VectorDrawable/Compose Canvas)
- Улучшить navigation bar indicator (premium pill shape)
- Добавить subtle transitions между табами
- Создать единый icon system (все иконки одного visual weight)
- Onboarding: обновить слайды для WOLFTASK (Tasks, Finance, Notes, Habits)

### ПРИОРИТЕТ 5 — Finance Analytics (продолжение)
- Charts: Income vs Expenses (bar chart), Expenses by category (pie chart)
- Period comparison (этот месяц vs прошлый)
- Использовать Compose Canvas для простых графиков
- НЕ добавлять сторонние chart libraries без проверки

### ПРИОРИТЕТ 6 — Notes import (Markdown)
- NoteExportManager.importFromMarkdown: парсинг .md → Note
- Валидация структуры
- AddTransactionSheet-style import flow

### ПРИОРИТЕТ 7 — Dashboard переработка
- Today должен показывать: приветствие → progress → finance → notes → next tasks → habits
- Inbox preview (3 последние входящие)
- Текущая/следующая задача
- Просроченные задачи (заметно, но не агрессивно)
- Иерархия: TODAY → FINANCE → NOTES → UPCOMING → HABITS

### ПРИОРИТЕТ 8 — Разделение Today и Habits
- Today = "что делать сегодня" (задачи)
- Habits = "регулярные действия" (streak, частота, прогресс)
- Habits карточка должна отличаться визуально от TaskCard

---

## 3. ТЕХНИЧЕСКИЙ КОНТЕКСТ

- Kotlin 1.9.22, AGP 8.1.2, Gradle 8.4
- Compose BOM 2024.02.00, Material3 1.2.0
- Room 2.6.1 (БД v10), Hilt 2.48, Navigation Compose 2.7.3
- minSdk 24 (desugaring), targetSdk 34
- DesignTokens: AppTheme.colors, Spacing, Radius, Elevation, AppTypography
- Бренд-цвет: оранжевый #FF7A00 (light) / #FF8C00 (dark)
- Язык интерфейса: ТОЛЬКО РУССКИЙ
- Название приложения: WOLFTASK
- Bottom nav: Today · Projects · Finance · Notes · Habits + More

## 4. ЧАСТЫЕ ПРОБЛЕМЫ КОМПИЛЯЦИИ
- `@OptIn(ExperimentalMaterial3Api)` для Card(onClick), DatePicker, SwipeToDismissBox
- `@OptIn(ExperimentalFoundationApi)` для HorizontalPager
- `@OptIn(ExperimentalCoroutinesApi)` для flatMapLatest
- combine max 5 flows — для большего количества используйте nested combine
- Файлы часто в одну строку — search_replace не работает, использовать python3 или cat >
- `coreLibraryDesugaring` включён — java.time работает на API 24+
- `material-icons-extended` подключён — все иконки доступны
- `pre-push-check.sh` проверяет баланс скобок и импорты перед пушем

---

*Создано для следующей сессии AI-ассистента.*
*Проект: dondgoklo-cyber/Floktask*
*Ветвь: vibe/taskmanager-scaffold-8c6512*
*Последний коммит: 173d94c (Finance Charts)*
