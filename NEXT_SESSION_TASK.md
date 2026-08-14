# ТЕХНИЧЕСКОЕ ЗАДАНИЕ ДЛЯ СЛЕДУЮЩЕЙ СЕССИИ
## TaskManager — Глубокая переработка в полноценное productivity-приложение

---

## 0. КАК НАЧАТЬ

### Репозиторий
- GitHub: `https://github.com/dondgoklo-cyber/Floktask`
- Ветка: `vibe/taskmanager-scaffold-8c6512` (активная)
- PR: `https://github.com/dondgoklo-cyber/Floktask/pull/1` (OPEN, draft)
- Локальный путь в sandbox: `/workspace/dondgoklo-cyber__Floktask`

### Что сделать в начале сессии
1. `cd /workspace/dondgoklo-cyber__Floktask`
2. `git pull origin vibe/taskmanager-scaffold-8c6512`
3. `git log --oneline -10` — изучить последние коммиты
4. Прочитать ЭТОТ файл полностью
5. Начать с Итерации 1 (см. раздел 6)

---

## 1. ТЕКУЩЕЕ СОСТОЯНИЕ ПРОЕКТА

### Технологии
- Kotlin 1.9.22, AGP 8.1.2, Gradle 8.4
- Jetpack Compose BOM 2024.02.00 (Material3 1.2.0)
- Compose Compiler 1.5.8
- Room 2.6.1 (БД версия 4)
- Hilt 2.48
- Navigation Compose 2.7.3
- minSdk 24, targetSdk 34
- Core library desugaring включён (java.time на API 24-25)
- Firebase ПОЛНОСТЬЮ УДАЛЁН (был причиной крашей)
- Retrofit/Coil/Timber в зависимостях, но Retrofit не используется

### Структура (55 Kotlin-файлов, ~2741 строк)
```
app/src/main/java/com/taskmanager/
├── TaskManagerApp.kt          # @HiltAndroidApp + crash logger (crash_log.txt)
├── di/
│   ├── AppModule.kt           # ПУСТОЙ (раньше был Firebase)
│   ├── DatabaseModule.kt      # Room DB + DAO провайдеры
│   ├── NetworkModule.kt       # Retrofit (НЕ ИСПОЛЬЗУЕТСЯ)
│   └── RepositoryModule.kt    # 4 @Binds
├── data/
│   ├── local/
│   │   ├── dao/               # TaskDao, ProjectDao, TagDao, UserStatsDao
│   │   ├── entity/            # TaskEntity, ProjectEntity, TagEntity, UserStatsEntity
│   │   └── database/          # AppDatabase v4 (4 сущности)
│   └── repository/            # *RepositoryImpl + *Mappers
├── domain/
│   ├── model/                 # Task, Project, Tag, Achievement
│   ├── repository/            # 4 интерфейса
│   └── usecase/               # task/, project/, gamification/
└── presentation/
    ├── MainActivity.kt        # @AndroidEntryPoint, setContent
    ├── theme/Theme.kt          # Светлая (белый+оранжевый) + Тёмная (чёрный+оранжевый)
    ├── navigation/             # NavGraph + Screen
    ├── components/             # TaskCard, PriorityBadge
    └── screens/
        ├── tasks/              # TasksScreen, TasksViewModel, TaskEditScreen, TaskEditViewModel
        ├── projects/           # ProjectsScreen, ProjectsViewModel
        ├── calendar/           # CalendarScreen, CalendarViewModel
        └── profile/            # ProfileScreen, ProfileViewModel
```

### CI/CD
- `.github/workflows/android.yml` — сборка APK + unit-тесты
- `.github/workflows/release.yml` — публикация APK в GitHub Releases (тег `v1.0.0-debug`)
- `.github/workflows/emulator-test.yml` — запуск в эмуляторе API 29, проверка крашей
- APK публикуется автоматически: https://github.com/dondgoklo-cyber/Floktask/releases/tag/v1.0.0-debug

### Скрипты-помощники (в папке scripts/)
- `ci-check.sh [run_id]` — ждёт CI, показывает ошибки компиляции
- `commit-push.sh "сообщение"` — коммит+пуш+CI в один шаг
- `scan-broken-refs.sh` — поиск битых ссылок на удалённые пакеты
- `check-apk.sh` — проверка APK в Releases
- `ooda-test.sh` — OODA/PDCA цикл самотестирования

---

## 2. ТЕКУЩИЕ МОДЕЛИ ДАННЫХ

### Task (domain + entity)
```kotlin
// domain/model/Task.kt
data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val projectId: Long? = null,       // FK на Project, но НЕТ выбора проекта в UI
    val priority: Priority = Priority.NONE,
    val deadline: Instant? = null,    // Только дата, НЕТ времени начала/длительности
    val isCompleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val color: String? = null,
    val reminderDate: Instant? = null,
    val recurrenceRule: RecurrenceRule? = null
)
enum class Priority(val value: Int) { HIGH(1), MEDIUM(2), LOW(3), NONE(4) }
enum class RecurrenceRule { DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM }
```

**Чего не хватает (из ТЗ):**
- `startTime: Instant?` — время начала
- `duration: Long?` — длительность в минутах (для time blocking)
- `tags: List<String>` — теги
- `subtasks` — подзадачи (нужна отдельная сущность)
- `pomodoroEstimate: Int?` — оценка количества Pomodoro
- `eisenhowerQuadrant` — квадрант для матрицы Эйзенхауэра
- `status: TaskStatus` — статус (TODO, IN_PROGRESS, DONE)
- `taskStatus: String` — для Kanban

### Project
```kotlin
data class Project(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val color: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
```
**Чего не хватает:** прогресс, секции, иконка, deadline

### Tag
```kotlin
data class Tag(
    val id: Long? = null,
    val name: String,
    val color: String? = null
)
```

### UserStats / Achievement (геймификация)
```kotlin
data class UserStats(
    val id: Long = 1,
    val totalPoints: Long = 0,
    val level: Int = 1,
    val completedTasks: Int = 0,
    val streak: Int = 0,
    val unlockedAchievementIds: List<String> = emptyList(),
    val updatedAt: Instant = Instant.now()
)
```

### База данных
- Версия: 4
- Сущности: TaskEntity, ProjectEntity, TagEntity, UserStatsEntity
- `fallbackToDestructiveMigration()` включён (данные стираются при смене версии)

---

## 3. ТЕКУЩАЯ НАВИГАЦИЯ

```kotlin
// 4 вкладки bottom navigation
sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Tasks : Screen("tasks", R.string.tasks)
    data object Projects : Screen("projects", R.string.projects)
    data object Calendar : Screen("calendar", R.string.calendar)
    data object Profile : Screen("profile", R.string.profile)
    data object TaskEditNew : Screen("task/new", R.string.add_task)
    data object TaskEdit : Screen("task/{taskId}", R.string.add_task)
}
```

**Анимации:** только fade (enter 120ms, exit 80ms) — быстро, как в Todoist.

**Чего не хватает (из ТЗ):**
- Inbox, Today, Upcoming
- Habits
- Focus (Pomodoro)
- Eisenhower Matrix
- Statistics
- Dashboard
- Settings
- Task Detail (bottom sheet)

---

## 4. ТЕКУЩИЙ ДИЗАЙН

### Цвета (Theme.kt)
- **Светлая тема:** фон `Color.White`, primary `#FF6D00` (оранжевый), secondary teal `#00897B`
- **Тёмная тема:** фон `#000000` (чёрный), primary `#FF9100` (оранжевый), surface `#0A0A0A`

**Чего не хватает (из ТЗ):**
- Design tokens (централизованные цвета, не разбросанные по файлам)
- Success/Warning/Danger/Info цвета
- Surface Elevated
- Border цвет
- Единая система spacing/radius/typography

### Компоненты
- `TaskCard` — карточка задачи с иконкой выполнения, дедлайном, приоритетом
- `PriorityBadge` — цветной бейдж приоритета
- `SwipeToDismissBox` — свайп для удаления/выполнения

### Экраны
- **TasksScreen** — список задач + поиск + фильтры (статус, приоритет) + свайпы
- **TaskEditScreen** — форма (название, описание, приоритет, повтор). НЕТ выбора проекта, НЕТ date/time picker
- **ProjectsScreen** — список + диалог создания
- **CalendarScreen** — горизонтальная неделя + задачи по дням. НЕТ time blocking
- **ProfileScreen** — уровень, очки, достижения

---

## 5. ЧТО НУЖНО РЕАЛИЗОВАТЬ (полный список из master prompt)

### Приоритет 1 — ФУНДАМЕНТ
- [ ] Расширить Task: startTime, duration, tags, status, pomodoroEstimate
- [ ] Создать сущность Subtask (id, taskId, title, isCompleted)
- [ ] Создать сущность Habit (id, name, icon, color, frequency, targetCount, reminderTime)
- [ ] Создать сущность HabitLog (id, habitId, date, count)
- [ ] Создать сущность PomodoroSession (id, taskId, startTime, duration, completed)
- [ ] Миграция БД v4 → v5
- [ ] Design tokens (централизованные цвета, spacing, radius)
- [ ] Переработать навигацию: Inbox, Today, Upcoming, Projects, Calendar, Habits, Focus, Eisenhower, Statistics

### Приоритет 2 — PROJECT → TASK
- [ ] В TaskEditScreen добавить выбор проекта (dropdown из списка)
- [ ] Добавить date/time picker (DatePickerDialog + TimePicker)
- [ ] Добавить поле длительности (duration в минутах)
- [ ] Добавить теги (chips с autocomplete)
- [ ] Добавить подзадачи (список с чекбоксами)
- [ ] Добавить Pomodoro estimate
- [ ] Quick Add (быстрое создание с клавиатуры)

### Приоритет 3 — TASK DETAIL
- [ ] Bottom sheet с деталями задачи
- [ ] Progressive disclosure (не все настройки сразу)
- [ ] Показывать проект, дату, время, длительность, приоритет, теги, подзадачи, заметки
- [ ] Кнопка запуска Pomodoro прямо из детали задачи

### Приоритет 4 — КАЛЕНДАРЬ
- [ ] Day / 3 Days / Week / Month / Agenda виды
- [ ] Time blocking — задачи с временем на временной шкале
- [ ] Drag & Drop задач по дням
- [ ] Изменение времени и длительности перетаскиванием
- [ ] Двусторонняя связь Task ↔ Calendar
- [ ] Задачи без времени — отдельный список на день

### Приоритет 5 — HABITS
- [ ] Список привычек с цветами и иконками
- [ ] Создание/редактирование привычки
- [ ] Календарь выполнения (сетка как в GitHub contributions)
- [ ] Текущая серия (streak), лучшая серия
- [ ] Процент выполнения
- [ ] Недельная/месячная статистика
- [ ] Отметка выполнения (тап по привычке)

### Приоритет 6 — POMODORO
- [ ] Экран фокус-таймера (круговой прогресс)
- [ ] Настройки: 25/5/15 (работа/короткий/длинный перерыв)
- [ ] Привязка к конкретной задаче
- [ ] Статистика: Pomodoros сегодня/неделю/месяц
- [ ] Focus Mode — минимальный отвлекающий UI
- [ ] Звуковое уведомление по завершении

### Приоритет 7 — EISENHOWER MATRIX
- [ ] 4 квадранта (DO NOW, SCHEDULE, DELEGATE, ELIMINATE)
- [ ] Авто-распределение задач по priority + deadline
- [ ] Drag & drop между квадрантами
- [ ] Обновление priority/urgency при переносе

### Приоритет 8 — DASHBOARD
- [ ] "Доброе утро, [имя]"
- [ ] Today's Progress (прогресс-бар)
- [ ] Next tasks (ближайшие по времени)
- [ ] Focus time today
- [ ] Habits today (точки)
- [ ] Projects progress
- [ ] Today summary (всего/выполнено/просрочено)

### Приоритет 9 — ПОИСК И ФИЛЬТРЫ
- [ ] Глобальный поиск (задачи, проекты, привычки, теги)
- [ ] Фильтры: Today, Upcoming, Overdue, Priority, Project, Tag, Date, Status

### Приоритет 10 — UX AUDIT
- [ ] Проверить все 15 вопросов из п.25 master prompt
- [ ] Skeleton loading на всех экранах
- [ ] Empty states с иконками и текстом
- [ ] Micro-interactions (анимация выполнения, удаления)
- [ ] Проверить контрастность (accessibility)
- [ ] Проверить touch targets (минимум 48dp)

---

## 6. ПЛАН ИТЕРАЦИЙ (по циклу Деминга PDCA)

Каждая итерация = Plan → Do → Check → Act:
- **Plan:** изучить что нужно, составить список изменений
- **Do:** реализовать, закоммитить
- **Check:** `./scripts/ci-check.sh` + эмулятор-тест
- **Act:** исправить ошибки, улучшить

### Итерация 1: Фундамент данных
- Расширить TaskEntity (startTime, duration, status, pomodoroEstimate)
- Создать SubtaskEntity, HabitEntity, HabitLogEntity, PomodoroSessionEntity
- БД v5, DAO, мапперы, репозитории, use cases
- Design tokens (DesignSystem.kt с цветами, spacing, radius, typography)

### Итерация 2: Навигация + Dashboard
- Новая навигация: Today, Projects, Calendar, Habits, Focus, More
- Dashboard экран
- Bottom sheet Task Detail

### Итерация 3: TaskEdit — полный
- Выбор проекта, date/time picker, длительность, теги, подзадачи
- Quick Add (bottom sheet)

### Итерация 4: Календарь с time blocking
- Day/Week виды с временной шкалой
- Drag & drop

### Итерация 5: Habits
### Итерация 6: Pomodoro
### Итерация 7: Eisenhower Matrix
### Итерация 8: Финальный UX-аудит

---

## 7. ТЕХНИЧЕСКИЕ РЕШЕНИЯ

### БД миграция (v4 → v5)
```kotlin
// AppDatabase: entities = [..., SubtaskEntity, HabitEntity, HabitLogEntity, PomodoroSessionEntity], version = 5
// Использовать fallbackToDestructiveMigration() (уже включён)
// ИЛИ написать Migration(4, 5) если нужно сохранить данные
```

### Design tokens
```kotlin
// presentation/theme/DesignTokens.kt
object Spacing { val xs = 4.dp; val sm = 8.dp; val md = 16.dp; val lg = 24.dp; val xl = 32.dp }
object Radius { val sm = 8.dp; val md = 12.dp; val lg = 16.dp; val xl = 24.dp }
object AppColors {
    // Light
    val lightPrimary = Color(0xFFFF6D00)
    val lightBackground = Color.White
    val lightSurface = Color.White
    val lightSurfaceElevated = Color(0xFFF5F5F5)
    val lightTextPrimary = Color(0xFF1A1A1A)
    val lightTextSecondary = Color(0xFF616161)
    val lightBorder = Color(0xFFE0E0E0)
    val lightSuccess = Color(0xFF4CAF50)
    val lightWarning = Color(0xFFFF9800)
    val lightDanger = Color(0xFFEF5350)
    val lightInfo = Color(0xFF2196F3)
    // Dark аналогично
}
```

### Drag & Drop
- Использовать Compose `pointerInput` + `detectDragGesturesAfterLongPress`
- Или библиотеку `reorderable` (github.com/aclassen/ComposeReorderable)

### Time blocking в календаре
- LazyColumn с часами (00:00-23:59), каждый час = 60dp
- Задачи — Box с offset по времени начала + высота по длительности
- Drag изменяет offset/height → обновляет startTime/duration в Task

### Quick Add
- Bottom sheet с одним TextField
- Парсинг: "позвонить завтра в 15:00 на час" → title + date + time + duration
- Без NLP — ручной разбор ключевых слов ("завтра", "сегодня", время в формате ЧЧ:ММ)

---

## 8. ВАЖНЫЕ ЗАМЕЧАНИЯ

### Что НЕ ломать
- Crash logger в TaskManagerApp.kt (запись в crash_log.txt) — ПОЛЕЗЕН для отладки
- CI/CD workflows (android.yml, release.yml, emulator-test.yml)
- Скрипты в scripts/
- Существующие модели и БД (только расширять, не переписывать)

### Частые проблемы
- `kotlin.code.style=official` в gradle.properties
- Compose BOM 2024.02.00 + Material3 1.2.0 (SwipeToDismissBox, HorizontalDivider доступны)
- `@OptIn(ExperimentalMaterial3Api::class)` нужен для Card(onClick), DatePicker, TimePicker, SwipeToDismissBox
- `coreLibraryDesugaring` включён — java.time работает на API 24+
- `BuildConfig.DEBUG` доступен (buildConfig = true в buildFeatures)
- Приложение: package `com.taskmanager`, debug suffix `.debug` → `com.taskmanager.debug`

### Цветовая палитра (зафиксирована пользователем)
- **Основной цвет:** оранжевый `#FF6D00` (светлый) / `#FF9100` (тёмный)
- **Светлая тема:** чисто белый фон
- **Тёмная тема:** глубокий чёрный фон + ярко-оранжевые акценты

### Язык интерфейса
- **ТОЛЬКО РУССКИЙ** — все строки, заголовки, кнопки, пустые состояния

### APK
- Публикуется автоматически в Releases при пуше
- https://github.com/dondgoklo-cyber/Floktask/releases/tag/v1.0.0-debug

---

## 9. ПРОВЕРОЧНЫЙ ЛИСТ (UX Audit из п.25 master prompt)

После реализации проверить:
1. Понятно ли, что делать после открытия приложения?
2. Можно ли создать задачу менее чем за 5 секунд?
3. Можно ли легко привязать её к проекту?
4. Можно ли назначить дату и время?
5. Видно ли её в календаре?
6. Можно ли изменить её drag & drop?
7. Можно ли запустить Pomodoro прямо из задачи?
8. Можно ли понять прогресс привычек?
9. Можно ли быстро определить важные задачи через Eisenhower Matrix?
10. Не перегружен ли интерфейс?
11. Хорошо ли выглядит dark mode?
12. Хорошо ли выглядит light mode?
13. Выглядит ли приложение как профессиональный коммерческий продукт?
14. Есть ли визуальная система?
15. Есть ли единый язык интерфейса?

---

*Создано для следующей сессии AI-ассистента.*
*Проект: dondgoklo-cyber/Floktask*
*Ветка: vibe/taskmanager-scaffold-8c6512*
