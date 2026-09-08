# CHANGELOG

Все заметные изменения проекта WOLFTASK (Floktask).

## [v1.3.0] - 2026-09-08

### Features
- TodayScreen: полная русская локализация (Сегодня / Просрочено / На сегодня / Бэклог)
- TodayScreen: тап по задаче открывает TaskDetailSheet (ранее не работало)
- TodayScreen: добавлен EmptyState для пустого экрана
- KanbanScreen: цветные фоны колонок (синий=TODO, оранжевый=IN_PROGRESS, зелёный=DONE)
- KanbanScreen: карточки задач используют surface-цвет для читаемости
- FinanceScreen: цветовое кодирование баланса (красный=отрицательный, зелёный=положительный, серый=нулевой)
- FinanceScreen: добавлен "+" префикс для положительного баланса
- FinanceScreen: покредитная цветовая индикация для мультивалюты

### CI/CD
- build.yml: убраны хардкод release notes (заменены на универсальный шаблон)
- build.yml: убрано хардкод удаление v1.2.0 и v1.3.0
- build.yml: упрощена логика удаления старых prerelease (только prerelease, limit 30)

### Todoist
- Закрыто 27 задач (уже реализованных в коде): темы, аналитика, привычки, голосовой ввод, виджеты,
  фокус-режим, геймификация, drag&drop канбан, Markdown, поиск заметок, шаблоны, повторяющиеся
  задачи, подзадачи, напоминания, массовые действия, Pomodoro+задачи, вложения, экспорт заметок,
  офлайн-режим, карточки задач, фоны канбана, пустые состояния, экран Сегодня, свайп выполнения,
  календарный вид, кнопка удаления входящих, цветной баланс финансов

---

## [v1.2.0] - 2026-09-07

### Features
- Переподключены все 18 экранов к навигации (Today, Inbox, Calendar, Habits, More,
  Focus, Insights, Projects, Finance, Notes, Kanban, Eisenhower, Search, Upcoming,
  Tags, Profile, Settings, ProjectDetail)
- Нижнее меню: Сегодня - Входящие - Календарь - Привычки - Ещё
- FocusScreen: реальный Pomodoro-таймер с круговым прогрессом
- InsightsScreen: Scaffold + EmptyState вместо заглушки
- MoreScreen: 3 секции (Обзор / Рабочее пространство / Аккаунт)

### CI/CD
- build.yml: release-APK (assembleRelease) в GitHub Releases на каждый push в main
- release-build.yml: удалён (вызывал красные воркфлоу без keystore secrets)
- build.yml: автоматическая очистка старых prerelease-релизов
- ci-checks.yml: unit-тесты + сборка + проверка зависимостей на PR/push

### Documentation
- README обновлён: правильный стек (Kotlin 2.0.20, Hilt 2.51.1, AGP 8.5.2),
  навигация, все модули, ссылка на releases/latest
- proguard-rules.pro: расширены keep-правила (Coroutines, Coil, Compose, domain models)

### Build
- build.gradle.kts: release buildType с debug-подписью (без keystore secrets),
  isMinifyEnabled=false, версия 1.2.0 (versionCode 10200)

---

## [v1.1.0] - 2026-09-01

### Added
- Core functionality improvements

## [v1.0.0] - 2026-09-01

### Added
- Initial release
