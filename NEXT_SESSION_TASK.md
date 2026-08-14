# ТЗ ДЛЯ СЛЕДУЮЩЕЙ СЕССИИ
## Floktask — полный редизайн: ProjectDetail, Tags, Finance, Dashboard

---

## 0. КАК НАЧАТЬ

- GitHub: `https://github.com/dondgoklo-cyber/Floktask`
- Ветвь: `vibe/taskmanager-scaffold-8c6512`
- PR: `https://github.com/dondgoklo-cyber/Floktask/pull/1`
- Локальный путь: `/workspace/dondgoklo-cyber__Floktask`
- `cd /workspace/dondgoklo-cyber__Floktask && git pull origin vibe/taskmanager-scaffold-8c6512`
- **НЕТ локальной сборки** (нет JDK/Android SDK/gradlew) — CI единственный верификатор (~5 мин/цикл)
- **ВСЕГДА** запускай `bash scripts/pre-push-check.sh` перед пушем

---

## 1. ЧТО УЖЕ СДЕЛАНО

### Сессии 1-5 (базовый функционал)
- Clean Architecture + MVVM, Compose, Room v7, Hilt, Navigation Compose
- 9 сущностей БД, 122 Kotlin-файла
- Inbox, Upcoming, Search, Onboarding, Kanban, Eisenhower, Focus↔Task
- Напоминания (AlarmManager + BootReceiver)
- Подзадачи 5 уровней (parentSubtaskId)
- PIN-код (UserPrefs), имя пользователя, приветствие
- Экспорт/импорт JSON (BackupManager)
- Теги хранятся как JSON-строка в TaskEntity.tags

### Сессия 6 (UI/UX polish)
- Переиспользуемые компоненты: Buttons.kt (PrimaryButton, SecondaryButton, AppTextButton, AppIconButton, AppFloatingActionButton)
- AppTextField.kt (стилизованный OutlinedTextField)
- EmptyState с action-кнопкой
- TaskCard: flat-стиль, pressed scale micro-interaction, priority accent, completed state (LineThrough)
- MoreScreen: grouped sections (Обзор/Рабочее пространство/Аккаунт), карточки с иконками
- Bottom Navigation: 64dp, primary selected, indicator
- SearchScreen: auto-focus
- TodayScreen: flat-карточки (ProgressCard, SummaryStat, FocusCard, HabitsCard)
- Редизайн MoreScreen: карточки вместо плоского списка
- LazyColumn keys везде
- Оранжевый бренд-цвет: #FF7A00 (light) / #FF8C00 (dark)

### Сессия 7
- **ProjectDetail экран** (ГОТОВО): List/Kanban переключатель, FAB Add Task, empty state
  - `projectdetail/ProjectDetailViewModel.kt` — загрузка проекта + задач, moveTask
  - `projectdetail/ProjectDetailScreen.kt` — TopAppBar, FilterChip List/Kanban, TaskCard
  - Навигация: `project/{projectId}` route, ProjectsScreen onProjectClick
  - ViewMode сохраняется через rememberSaveable

### Сессия 8 (текущая)
- **Tags с цветами** (ГОТОВО — Приоритет 1):
  - `TagDao`: +update, +ORDER BY name
  - `TagRepository/Impl`: +updateTag
  - UseCases: `CreateTag`, `UpdateTag`, `DeleteTag`, `GetAllTags` (domain/usecase/tag/)
  - `TagColorPalette.kt`: 14 цветов, `parseTagColor` (#RRGGBB/#AARRGGBB), `DEFAULT_TAG_COLOR`
  - `TagsScreen.kt` + `TagsViewModel.kt`: список, создание, переименование, удаление, color picker
  - `TaskCard.kt`: `TagMiniChip` отображает цвет тега (`tagColors: Map<String,String>`)
  - `TaskEditScreen/ViewModel`: выбор существующих тегов (с цветом) + создание новых; авто-создание Tag-сущности
  - `ProjectDetail`: tagColors проводятся в List и Kanban TaskCard
  - Навигация: `tags` route, MoreScreen entry (Рабочее пространство)
  - Strings: new_tag, edit_tag, delete_tag_title/message, tag_name, select_color, no_tags
  - CI: Build & Test — success (6c79ac2)

---

## 2. ЧТО НУЖНО СДЕЛАТЬ (по приоритетам)

### ПРИОРИТЕТ 1 — Tags: управление цветами ✅ ГОТОВО (сессия 8)
- TagEntity имеет поле `color: String?` — теперь используется в UI
- TagRepository имеет `updateTag` — добавлено
- TaskEditScreen: теги выбираются из существующих (с цветом) + создание новых
- Сделано:
  - [x] Добавить `updateTag` в TagRepository + TagRepositoryImpl
  - [x] Создать экран управления тегами (список, создать, переименовать, удалить, выбрать цвет)
  - [x] Color picker (палитра из 14 цветов)
  - [x] В TaskEdit: выбор тегов из существующих (с цветом) + создание новых
  - [x] Цвет тега отображается в TaskCard (TagMiniChip)
  - [x] Цвет сохраняется в БД, переживает перезапуск

### ПРИОРИТЕТ 2 — Add Task из Project (с авто-выбором проекта)
- Сейчас ProjectDetail FAB → `task/new` (без project context)
- Нужно:
  - [ ] Добавить маршрут `task/new?projectId={projectId}` ИЛИ передавать projectId через SavedStateHandle
  - [ ] TaskEditViewModel: при получении projectId предзаполнить form.projectId
  - [ ] После сохранения — возврат в ProjectDetail (navController.popBackStack)
  - [ ] Задача появляется в Project, Today (если дата), Search

### ПРИОРИТЕТ 3 — "+" универсальная кнопка создания
- Сейчас FAB на Today → QuickAdd (только задача)
- Нужно:
  - [ ] Заменить FAB на SpeedDial/BottomSheet меню: Задача / Привычка / Проект / Доход / Расход
  - [ ] Или: FAB открывает QuickAdd, аlong-press → меню
  - [ ] Не перегружать — максимум 5 пунктов

### ПРИОРИТЕТ 4 — Личные финансы (НОВЫЙ CORE-МОДУЛЬ)
- Сущности:
  - `TransactionEntity` (id, amount, type INCOME/EXPENSE, categoryId, accountId, date, note, createdAt, updatedAt)
  - `CategoryEntity` (id, name, type INCOME/EXPENSE, color, icon, isDefault)
  - `AccountEntity` (id, name, balance, currency)
- DAO + Repository + UseCases
- БД v8 (fallbackToDestructiveMigration)
- Экраны:
  - `FinanceScreen` — баланс, доходы/расходы за период, список операций
  - `AddTransactionSheet` — быстрая форма (сумма → категория → сохранить)
  - Периоды: Сегодня / Неделя / Месяц / Год
- Dashboard (TodayScreen):
  - Финансовый блок: баланс + доходы + расходы + последние 3 операции
- Категории по умолчанию (Продукты, Транспорт, Жильё, Зарплата, и т.д.)
- Баланс = initialBalance + incomes - expenses (Flow-based, автоматически пересчитывается)
- Navigation: Finance в bottomNav или в More

### ПРИОРИТЕТ 5 — Dashboard/Today переработка
- Today должен показывать:
  - Приветствие + краткая статистика (уже есть)
  - Inbox preview (3 последние входящие) + "Посмотреть все"
  - Текущая/следующая задача
  - Просроченные задачи (заметно, но не агрессивно)
  - Финансы (после реализации модуля)
  - Upcoming (кратко)
- Не перегружать — иерархия: Today → Inbox → Current → Finance → Upcoming

### ПРИОРИТЕТ 6 — Разделение Today и Habits
- Today = "что делать сегодня" (задачи)
- Habits = "регулярные действия" (streak, частота, прогресс)
- Habits карточка должна отличаться от TaskCard визуально

### ПРИОРИТЕТ 7 — Inbox как часть flow
- Inbox задачи без проекта/даты → быстрая обработка (Сегодня / Проект / Запланировать)
- Swipe actions в Inbox

---

## 3. ТЕХНИЧЕСКИЙ КОНТЕКСТ

- Kotlin 1.9.22, AGP 8.1.2, Gradle 8.4
- Compose BOM 2024.02.00, Material3 1.2.0
- Room 2.6.1 (БД v7), Hilt 2.48, Navigation Compose 2.7.3
- minSdk 24 (desugaring), targetSdk 34
- DesignTokens: AppTheme.colors, Spacing, Radius, Elevation, AppTypography
- Бренд-цвет: оранжевый #FF7A00 (light) / #FF8C00 (dark)
- Язык интерфейса: ТОЛЬКО РУССКИЙ

## 4. ЧАСТЫЕ ПРОБЛЕМЫ КОМПИЛЯЦИИ
- `@OptIn(ExperimentalMaterial3Api)` для Card(onClick), DatePicker, TimePicker, SwipeToDismissBox
- `@OptIn(ExperimentalFoundationApi)` для HorizontalPager
- `@OptIn(ExperimentalCoroutinesApi)` для flatMapLatest
- Файлы часто в одну строку — `search_replace` не работает, использовать `python3` или `cat >`
- `coreLibraryDesugaring` включён — java.time работает на API 24+
- `material-icons-extended` подключён — все иконки доступны
- `pre-push-check.sh` проверяет баланс скобок и импорты перед пушем

---

*Создано для следующей сессии AI-ассистента.*
*Проект: dondgoklo-cyber/Floktask*
*Ветвь: vibe/taskmanager-scaffold-8c6512*
*Последний коммит: 6c79ac2 (Tags с цветами)*
