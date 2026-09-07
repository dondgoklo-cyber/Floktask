# CHANGELOG

Все заметные изменения проекта WOLFTASK (Floktask).

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
