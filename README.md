# TaskManager

Smart Android task manager with an AI assistant, geolocation reminders, and gamification.
Built with **Clean Architecture + MVVM**, **Jetpack Compose**, **Room**, **Hilt**, and **Firebase**.

> Status: Stage 2 (MVP) — scaffolding complete. Target API 34, min SDK 24.

## Features

### MVP (current scaffold)
- Tasks and subtasks model
- Projects and tags
- Priorities (HIGH / MEDIUM / LOW / NONE)
- Deadlines and reminders
- Recurring tasks (`RecurrenceRule`)
- Search and filtering
- Local storage (Room)
- Cloud sync scaffold (Firebase Auth + Realtime Database)

### Planned
- Kanban board, Gantt diagrams, Pomodoro timer, habit tracker
- Natural-language input, collaboration, statistics
- **AI assistant** (auto prioritization)
- **Geolocation reminders**
- **Gamification** (achievements, points, leaderboard)
- Integrations (Google Calendar, Telegram, Notion)

## Tech stack

| Category        | Technology                |
|-----------------|---------------------------|
| Language        | Kotlin 1.9.10            |
| UI              | Jetpack Compose (BOM)     |
| Local DB        | Room 2.5.2               |
| DI              | Hilt 2.48               |
| Auth            | Firebase Auth            |
| Cloud DB        | Firebase Realtime DB     |
| HTTP            | Retrofit 2 + Gson        |
| Async           | Coroutines + Flow        |
| Navigation      | Navigation Compose 2.7.3 |
| Logging         | Timber                    |

## Project structure

```
app/src/main/java/com/taskmanager/
├── TaskManagerApp.kt          # @HiltAndroidApp Application
├── di/                        # Hilt modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── data/                      # Data layer
│   ├── local/                # Room (dao, entity, database)
│   ├── remote/               # FirebaseService, AuthService
│   └── repository/           # Repository implementations + mappers
├── domain/                    # Domain layer
│   ├── model/                # Task, Project, Tag, enums
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Use cases (task, project)
└── presentation/              # Presentation layer
    ├── MainActivity.kt
    ├── theme/                # Material3 light/dark
    ├── navigation/           # NavGraph, Screen
    ├── components/           # TaskCard, PriorityBadge
    └── screens/              # tasks, projects, calendar
```

## Getting started

### Prerequisites
- Android Studio Hedgehog (or newer)
- JDK 17
- Android SDK 34

### Setup
1. Clone the repository.
2. Create a project in the [Firebase console](https://console.firebase.google.com/),
   enable **Authentication** (Email/Password) and **Realtime Database**.
3. Download `google-services.json` and place it at `app/google-services.json`.
   A redacted template is provided at `app/google-services.json.example`.
4. Open the project in Android Studio and let Gradle sync.
5. Run the `app` configuration on a device or emulator (API 24+).

### Build
```bash
./gradlew assembleDebug        # debug APK
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests
```

## Roadmap

| Stage | Period          | Status   |
|-------|-----------------|----------|
| 1. Analysis & planning | Aug 2026        | Done     |
| 2. MVP development     | Sep–Nov 2026    | In progress |
| 3. Extended features   | Dec 2026–Feb 2027 | Pending  |
| 4. Unique features     | Mar–May 2027    | Pending  |
| 5. Release             | Jun 2027        | Pending  |

## Code quality

The project uses **Detekt** (static analysis) and **ktlint** (formatting) configured via
`detekt.yml` and `.editorconfig`.

### Install git hooks (one-time setup)

```bash
git config core.hooksPath .githooks
```

This enables a `pre-commit` hook that runs `ktlintCheck` + `detekt` on staged Kotlin
files, and a `commit-msg` hook that appends co-author metadata. Bypass once with
`git commit --no-verify`.

### Run checks manually

```bash
./gradlew detekt          # static analysis
./gradlew ktlintCheck     # format check
./gradlew ktlintFormat    # auto-format
```

CI (`.github/workflows/lint.yml`, `build.yml`) runs these checks on every PR and push to `main`.

## License

Proprietary — all rights reserved.
