# ТЗ ДЛЯ СЛЕДУЮЩЕЙ СЕССИИ
## TaskManager — MVP доработка: сделать так, чтобы всё работало

> **Обновление от сессии 4:** приоритеты 1–8 реализованы, `Android CI: Build & Test — success` (коммит 5b4289a).
> `Emulator Test` (Boot Test) падает на инфраструктуре эмулятора (было и на baseline b44495c) — не связано с кодом.

---

## 0. КАК НАЧАТЬ

### Репозиторий
- GitHub: `https://github.com/dondgoklo-cyber/Floktask`
- Ветка: `vibe/taskmanager-scaffold-8c6512` (активная)
- PR: `https://github.com/dondgoklo-cyber/Floktask/pull/1` (OPEN, draft)
- Локальный путь в sandbox: `/workspace/dondgoklo-cyber__Floktask`

### Первые команды
1. `cd /workspace/dondgoklo-cyber__Floktask`
2. `git pull origin vibe/taskmanager-scaffold-8c6512`
3. `git log --oneline -10` — изучить последние коммиты
4. Прочитать ЭТОТ файл полностью
5. Начать с приоритета 1 (см. раздел 3)

---

## 1. ЧТО БЫЛО СДЕЛАНО В ПРЕДЫДУЩИХ СЕССИЯХ

### Сессия 1 (scaffold)
- Создан скелет: Clean Architecture + MVVM, Compose, Room v4, Hilt
- 4 сущности: Task, Project, Tag, UserStats
- 4 вкладки: Tasks, Projects, Calendar, Profile
- Firebase удалён (был причиной крашей)
- Crash logger в TaskManagerApp.kt
- CI/CD: android.yml, emulator-test.yml, release.yml

### Сессия 2 (глубокая переработка)
- **Данные:** расширены модели (Task: startTime, duration, status, pomodoroEstimate, eisenhowerQuadrant, tags, subtasks; Project: icon, deadline)
- **Новые сущности:** Subtask, Habit, HabitLog, PomodoroSession + DAO + мапперы + репозитории
- **БД v5** (fallbackToDestructiveMigration сохранён)
- **Design tokens:** AppColors (light/dark), Spacing, Radius, Elevation, AppTypography
- **Навигация:** Today/Projects/Calendar/Habits/Focus + More (Inbox, Upcoming, Eisenhower, Settings, Profile)
- **Dashboard (Today):** приветствие, прогресс, ближайшие задачи, фокус-время, привычки
- **TaskEdit:** выбор проекта (dropdown), DatePicker, TimePicker, длительность, теги, Pomodoro estimate, статус, приоритет, повтор
- **TaskDetail:** ModalBottomSheet с progressive disclosure
- **QuickAdd:** bottom sheet с парсингом ключевых слов
- **Календарь:** 5 режимов, time blocking, drag&drop
- **Habits:** создание, streak, отметка
- **Pomodoro:** таймер 25/5/15, статистика
- **Eisenhower:** 4 квадранта, авто-распределение
- **UX:** SkeletonBox, EmptyState, design tokens

### Сессия 3 (MVP фикс — текущая)
- Удалён мёртвый TasksScreen/TasksViewModel
- NavGraph: добавлены Profile, Inbox, Upcoming, Settings, Eisenhower (раньше крашилось)
- TaskEditScreen: переписана секция date/time — кликабельные карточки с DatePicker/TimePicker
- TodayScreen: FAB открывает QuickAdd bottom sheet
- ProfileScreen: добавлен onBack + navigationIcon
- MoreScreen: добавлен Profile

**CI статус:** `Android CI: Build & Test — success` (коммит a3e84da)

### Сессия 4 (доработка по ТЗ — приоритеты 1–8)
- **P1 Inbox:** экран задач без проекта/даты, FAB→QuickAdd, клик→TaskDetailSheet, skeleton + empty state
- **P1 Upcoming:** невыполненные задачи с дедлайном/startTime в будущем, группировка по дням, FAB, skeleton
- **P2 Focus↔Task:** роут `focus/{taskId}`, `FocusViewModel.setTask` загружает название, FocusScreen показывает его, PomodoroSession с taskId
- **P3 Eisenhower drag&drop:** отслеживание pointer через onGloballyPositioned, подсветка целевого квадранта, moveTask при drop
- **P4 Календарь:** resize-handle (нижняя граница time block) меняет длительность (мин 15 мин); перенос времени и дня сохранён
- **P5 Подзадачи:** переупорядочивание (drag handle + drop zones), прогресс (X/Y + LinearProgressIndicator), inline-редактирование названия
- **P6 Поиск:** глобальный поиск (задачи/проекты/привычки), экран SearchScreen, вход из More
- **P7 Onboarding:** 4 слайда (HorizontalPager), один раз через SharedPreferences
- **P8 UX:** skeleton в Projects/Habits/Inbox/Upcoming, empty states

**Новые файлы:** inbox/, upcoming/, search/, onboarding/
**CI статус:** `Android CI: Build & Test — success` (коммит 5b4289a)

### Сессия 5 (улучшения из ТЗ «Focus»)
- **Локальные напоминания (ТЗ §4.6):** AlarmManager + уведомления с действиями
  Выполнить/Отложить (5/15/30/60 мин), BootReceiver для перерегистрации после
  перезагрузки, карточка напоминания в TaskEdit (DatePicker). Исправлено
  искажённое имя разрешения POST_NOTIFICATIONS в манифесте.
- **Kanban-доска (ТЗ §4.5):** новый вид отображения задач по статусам
  (К выполнению / В процессе / Выполнено) с горизонтальной прокруткой колонок
  и drag&drop между ними. Вход из More.
- **Подзадачи 5 уровней (ТЗ §4.1):** parentSubtaskId в Subtask + SubtaskEntity,
  БД v6, getSubtaskTree строит дерево, рекурсивный SubtaskSection с отступами
  по уровню и кнопкой + для вложенных подзадач.

**Новые файлы:** notification/ (AlarmScheduler, AlarmReceiver, BootReceiver),
kanban/ (KanbanScreen, KanbanViewModel)
**CI статус:** `Android CI: Build & Test — success` (коммит cc53b01)

---

## 2. ТЕКУЩЕЕ СОСТОЯНИЕ ПРОЕКТА

### Технологии
- Kotlin 1.9.22, AGP 8.1.2, Gradle 8.4
- Compose BOM 2024.02.00 (Material3 1.2.0)
- Room 2.6.1 (БД v5, 8 сущностей)
- Hilt 2.48, Navigation Compose 2.7.3
- minSdk 24, targetSdk 34
- coreLibraryDesugaring включён

### Структура (102 Kotlin-файла, ~6961 строк)
```
app/src/main/java/com/taskmanager/
├── TaskManagerApp.kt          # @HiltAndroidApp + crash logger
├── di/                        # 4 Hilt-модуля
├── data/
│   ├── local/dao/             # 8 DAO
│   ├── local/entity/          # 8 сущностей
│   ├── local/database/        # AppDatabase v5
│   └── repository/            # 8 RepositoryImpl + Mappers
├── domain/
│   ├── model/                 # Task, Project, Tag, Subtask, Habit, HabitLog, PomodoroSession, Achievement
│   ├── repository/           # 8 интерфейсов
│   └── usecase/              # task/, project/, habit/, pomodoro/, eisenhower/, gamification/
└── presentation/
    ├── MainActivity.kt
    ├── theme/                 # DesignTokens.kt, Theme.kt
    ├── navigation/            # NavGraph.kt, Screen.kt
    ├── components/            # TaskCard, PriorityBadge, Skeleton, EmptyState, PriorityColors
    └── screens/               # today, tasks, projects, calendar, habits, focus, eisenhower, more, profile
```

### Что работает и нажимается
- ✅ Today (Dashboard): прогресс, ближайшие задачи, сводка дня
- ✅ QuickAdd: создание задачи с парсингом ключевых слов
- ✅ TaskDetail: bottom sheet, подзадачи, кнопка Pomodoro → переход на Focus
- ✅ TaskEdit: выбор проекта, DatePicker, TimePicker, длительность, теги, приоритет, статус, повтор
- ✅ Projects: список, создание
- ✅ Calendar: 5 режимов, time blocking, drag&drop
- ✅ Habits: список, создание, отметка, streak
- ✅ Focus (Pomodoro): таймер, переключение режимов, статистика
- ✅ Eisenhower: 4 квадранта, авто-распределение
- ✅ More: Inbox, Upcoming, Eisenhower, Profile, Settings
- ✅ Profile: уровни, очки, достижения

---

## 3. ЧТО НУЖНО ДОРАБОТАТЬ (приоритеты)

### Приоритет 1 — Inbox и Upcoming экраны
Сейчас Inbox и Upcoming — пустые placeholder'ы (`SimplePlaceholder` в NavGraph.kt).
Нужно:
- [ ] Inbox: список задач без проекта и без даты (быстрый захват)
- [ ] Upcoming: задачи на ближайшие дни (сортировка по deadline/startTime)
- [ ] Оба экрана: клик по задаче → TaskDetailSheet
- [ ] FAB для быстрого добавления

### Приоритет 2 — Связь Focus ↔ Task
Сейчас FocusScreen не принимает taskId из навигации.
Нужно:
- [ ] TaskDetailSheet "Запустить Pomodoro" → передаёт taskId в FocusScreen через навигацию
- [ ] Добавить роут `focus/{taskId}` в NavGraph
- [ ] FocusScreen: при получении taskId показывает название задачи
- [ ] FocusViewModel.setTask(taskId) — привязка сессии к задаче
- [ ] Сохранение PomodoroSession с taskId

### Приоритет 3 — Drag&drop в Eisenhower
Сейчас drag&drop в Eisenhower — заглушка (onDrag пустой).
Нужно:
- [ ] Отслеживать позицию drop → определять квадрант
- [ ] Вызывать moveTask(taskId, quadrant) при drop
- [ ] Визуальная подсветка целевого квадранта при drag

### Приоритет 4 — Drag&drop в календаре
Сейчас drag в Day view перемещает задачу, но drop-логика упрощённая.
Нужно:
- [ ] Корректный расчёт нового времени при drop (с учётом часа и минуты)
- [ ] Resize (растягивание нижней границы) для изменения длительности
- [ ] Перенос задачи на другой день (long-press + drag в Week view)

### Приоритет 5 — Подзадачи в TaskDetail
Сейчас подзадачи добавляются/удаляются в TaskDetailSheet, но:
- [ ] Нет переупорядочивания (drag to reorder)
- [ ] Нет прогресса подзадач в карточке задачи (X/Y выполнено)
- [ ] Нет редактирования названия подзадачи

### Приоритет 6 — Поиск и фильтры
- [ ] Глобальный поиск (задачи, проекты, привычки, теги)
- [ ] Фильтры: Today, Upcoming, Overdue, Priority, Project, Tag, Date, Status
- [ ] Сортировка списка задач

### Приоритет 7 — Onboarding
- [ ] При первом запуске: краткое руководство (3-4 слайда)
- [ ] Создание первого проекта при первом запуске
- [ ] Подсказки для пустых состояний

### Приоритет 8 — Полировка UX
- [ ] Skeleton loading на всех экранах (компонент готов, нужно подключить)
- [ ] Empty states на всех экранах (компонент готов, нужно подключить)
- [ ] Micro-interactions (анимация выполнения задачи, удаления)
- [ ] Проверка контрастности (accessibility)
- [ ] Touch targets минимум 48dp
- [ ] Проверка dark/light mode

---

## 4. ТЕХНИЧЕСКИЙ ДОЛГ

### NetworkModule
- `NetworkModule.kt` создаёт Retrofit с фейковым URL `https://api.taskmanager.com/`
- Retrofit не используется нигде, но зависимость в build.gradle.kts
- Можно удалить модуль и зависимость, либо оставить как задел

### Миграция БД
- `fallbackToDestructiveMigration()` включён — данные стираются при смене версии
- Перед release написать явные Migration(v5 → v6) для сохранения данных

### Тесты
- 3 unit-теста: TaskMappersTest, PriorityTest, QuickAddParserTest
- Нет instrumented тестов
- Нет тестов для репозиториев, use cases, ViewModels

---

## 5. ВАЖНЫЕ ЗАМЕЧАНИЯ

### Что НЕ ломать
- Crash logger в TaskManagerApp.kt
- CI/CD workflows (.github/workflows/)
- Скрипты в scripts/
- Design tokens (DesignTokens.kt, Theme.kt)
- Существующие модели и БД (только расширять)

### Частые проблемы при компиляции
- `@OptIn(ExperimentalMaterial3Api::class)` нужен для Card(onClick), DatePicker, TimePicker, SwipeToDismissBox
- `@OptIn(ExperimentalLayoutApi::class)` нужен для FlowRow
- `coreLibraryDesugaring` включён — java.time работает на API 24+
- Compose BOM 2024.02.00: `menuAnchor()` БЕЗ аргументов, `ExposedDropdownMenu` → использовать `DropdownMenu`
- Файлы часто сохраняются на одной строке — `search_replace` не работает, использовать `write_file` с `overwrite=True`
- `object Foo : DataClass()` — НЕ компилируется (final), использовать `val Foo = DataClass(...)`
- `detectDragGesturesAfterLongPress` требует параметр `onDrag` (не только onDragStart/onDragEnd)
- Внутри `Canvas { }` нельзя вызывать `@Composable` функции (AppTheme.colors) — передавать как параметр

### Цветовая палитра (зафиксирована)
- Основной: оранжевый `#FF6D00` (light) / `#FF9100` (dark)
- Light: чисто белый фон
- Dark: глубокий чёрный фон + ярко-оранжевые акценты
- Semantic: success `#2E7D32`, warning `#ED6C02`, danger `#C62828`, info `#0277BD`
- Design tokens: `AppTheme.colors` из любого composable

### Язык интерфейса
- ТОЛЬКО РУССКИЙ — все строки, заголовки, кнопки, пустые состояния

### APK
- Публикуется автоматически в Releases при пуше
- https://github.com/dondgoklo-cyber/Floktask/releases/tag/v1.0.0-debug

### Скрипты-помощники (в scripts/)
- `ci-check.sh [run_id]` — ждёт CI, показывает ошибки компиляции
- `commit-push.sh "сообщение"` — коммит+пуш+CI в один шаг
- `scan-broken-refs.sh` — поиск битых ссылок на удалённые пакеты
- `check-apk.sh` — проверка APK в Releases
- `ooda-test.sh` — OODA/PDCA цикл самотестирования

---

## 6. ПРОВЕРОЧНЫЙ ЛИСТ (UX Audit)

1. Понятно ли, что делать после открытия приложения?
2. Можно ли создать задачу менее чем за 5 секунд? (QuickAdd)
3. Можно ли легко привязать её к проекту? (TaskEdit dropdown)
4. Можно ли назначить дату и время? (DatePicker, TimePicker)
5. Видно ли её в календаре? (Day view time blocking)
6. Можно ли изменить её drag&drop? (Calendar drag)
7. Можно ли запустить Pomodoro прямо из задачи? (TaskDetail → Focus)
8. Можно ли понять прогресс привычек? (Habits streak)
9. Можно ли быстро определить важные задачи через Eisenhower?
10. Не перегружен ли интерфейс?
11. Хорошо ли выглядит dark mode?
12. Хорошо ли выглядит light mode?
13. Выглядит ли приложение как профессиональный коммерческий продукт?
14. Есть ли визуальная система? (design tokens)
15. Есть ли единый язык интерфейса?

---

*Создано для следующей сессии AI-ассистента.*
*Проект: dondgoklo-cyber/Floktask*
*Ветка: vibe/taskmanager-scaffold-8c6512*
*Последний коммит: a3e84da (CI: success)*
