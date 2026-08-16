# TaskManager

Productivity-приложение для Android: задачи, проекты, календарь с time blocking,
привычки, Pomodoro, матрица Эйзенхауэра и статистика.
Построено на **Clean Architecture + MVVM**, **Jetpack Compose**, **Room**, **Hilt**.

> Статус: активная разработка. Target API 34, min SDK 24.

## Возможности

### Реализовано
- **Задачи**: создание, редактирование, приоритеты, статусы, дедлайн, время начала,
  длительность, теги, подзадачи, повтор, оценка Pomodoro, квадрант Эйзенхауэра
- **Проекты**: создание, цвета, иконки, архивирование, связь с задачами
- **Календарь**: 5 режимов (День, 3 дня, Неделя, Месяц, Список), time blocking,
  drag&drop задач по временной шкале, двусторонняя связь Task↔Calendar
- **Привычки**: создание с цветом/частотой, streak (текущая/лучшая),
  процент выполнения за 30 дней, отметка выполнения
- **Pomodoro**: таймер (25/5/15), режимы Work/Short/Long break,
  автопереключение, привязка к задаче, статистика (день/неделя/месяц)
- **Матрица Эйзенхауэра**: 4 квадранта, авто-распределение по importance+urgency,
  ручное переопределение через drag&drop
- **Dashboard (Today)**: приветствие, прогресс дня, ближайшие задачи,
  фокус-время, привычки, сводка (всего/выполнено/просрочено)
- **Task Detail**: bottom sheet с progressive disclosure, подзадачи, запуск Pomodoro
- **Quick Add**: быстрое создание с парсингом ключевых слов
  («завтра», «15:00», «на час»)
- **Дизайн-система**: централизованные design tokens (цвета, spacing, radius,
  typography), light/dark темы, semantic colors (Success/Warning/Danger/Info)
- **Геймификация**: уровни, очки, достижения

## Технологии

| Категория        | Технология                |
|-----------------|---------------------------|
| Язык             | Kotlin 1.9.22             |
| UI               | Jetpack Compose (BOM 2024.02.00) |
| Локальная БД     | Room 2.6.1 (v5)           |
| DI               | Hilt 2.48                |
| Навигация        | Navigation Compose 2.7.3  |
| Архитектура      | Clean Architecture + MVVM |
| Асинхронность    | Coroutines + Flow        |

## Структура

```
app/src/main/java/com/taskmanager/
├── TaskManagerApp.kt          # @HiltAndroidApp + crash logger
├── di/                        # Hilt модули (DB, репозитории, сеть)
├── data/                      # Data layer
│   ├── local/                 # Room (dao, entity, database)
│   └── repository/            # RepositoryImpl + Mappers
├── domain/                    # Domain layer
│   ├── model/                 # Task, Project, Tag, Habit, Subtask, PomodoroSession
│   ├── repository/            # Repository interfaces
│   └── usecase/               # task/, project/, habit/, pomodoro/, eisenhower/, gamification/
└── presentation/              # Presentation layer
    ├── MainActivity.kt
    ├── theme/                 # DesignTokens, Theme (light/dark)
    ├── navigation/            # NavGraph, Screen
    ├── components/            # TaskCard, PriorityBadge, Skeleton, EmptyState
    └── screens/               # today, tasks, projects, calendar, habits, focus, eisenhower, more, profile
```

## Сборка

```bash
gradle assembleDebug        # debug APK
gradle testDebugUnitTest    # unit-тесты
```

APK публикуется автоматически в [Releases](https://github.com/dondgoklo-cyber/Floktask/releases).

## Лицензия

Proprietary — all rights reserved.
