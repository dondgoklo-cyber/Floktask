# Прогресс стабилизации Floktask

## Статус: В работе
## Последнее обновление: 6 сентября 2026 года

---

## 🚨 P0 Задачи (8 штук) - КРИТИЧЕСКИЕ

### ✅ P0-1: Замерджить PR #88 (ошибки компиляции)
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Изменения:** NavGraph.kt, SearchScreen.kt, TodayTasksWidget.kt, ValidationResult.kt
- **Коммит:** f559cae
- **Проверка:** PR успешно замерджен в main

### ✅ P0-2: Замерджить PR #87 (Logger абстракция)
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Изменения:** Исправлен порядок параметров в 24 UseCase файлах (logger после repository)
- **Коммит:** 4a73b09
- **Проверка:** Все UseCase файлы имеют корректный порядок параметров

### ✅ P0-3: Исправить зависимости ViewModel → Repository (9 файлов)
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Файлы:** InboxViewModel, CalendarViewModel, ProjectsViewModel, SearchViewModel, FinanceViewModel, KanbanViewModel, UpcomingViewModel, NotesViewModel, FocusViewModel, ProjectDetailViewModel, NoteEditViewModel, TaskDetailViewModel
- **Изменения:** Создано 30+ UseCase классов для изоляции Repository от Presentation
- **Коммиты:** 995a9cc, e0be6bf, d45d6ba, 83107c5, a3af8e2
- **Проверка:** `grep -r "Repository" app/src/main/java/com/taskmanager/presentation --include="*.kt" → ПУСТО`

### ✅ P0-4: Удалить auto-apk-upload.yml
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Проверка:** Файл отсутствует в .github/workflows/

### ✅ P0-5: Восстановить тесты в CI
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Изменения:** Тесты уже были включены в ci-checks.yml
- **Коммит:** 29792eb
- **Проверка:** ci-checks.yml содержит тесты

### ✅ P0-6: Исправить signingConfig
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Изменения:** Удалены hardcoded пароли, добавлена валидация переменных окружения
- **Коммит:** 85e58de
- **Проверка:** Нет hardcoded паролей в build.gradle.kts

### ✅ P0-7: Убрать android.app.Application из FinanceViewModel
- **Статус:** Завершено
- *
*Дата:** 6 сентября 2026
- **Изменения:** Заменен на UserPreferences интерфейс + UserPreferencesImpl реализацию
- **Коммит:** 17f64e7
- **Проверка:** FinanceViewModel больше не зависит от Android Application

### ✅ P0-8: Добавить обработку ошибок в FinanceViewModel
- **Статус:** Завершено
- **Дата:** 6 сентября 2026
- **Изменения:** Все вызовы repository обернуты в try-catch блоки
- **Коммит:** 17f64e7
- **Проверка:** Все операции имеют обработку ошибок

---

## 🟡 P1 Задачи (20 штук) - ВЫСОКИЙ ПРИОРИТЕТ

- ⏳ P1-1: Убрать UserPrefs из presentation слоя
- ⏳ P1-2: Удалить дубликат TimeInterval
- ⏳ P1-3: Исправить Hardcoded ZoneId.of("UTC")
- ⏳ P1-4: Заменить зависимости от Repository в FinanceViewModel
- ⏳ P1-5: Разбить FinanceViewModel на меньшие
- ⏳ P1-6: Удалить дублирование логики в FinanceViewModel
- ⏳ P1-7: Исправить зависимости в NotesViewModel
- ⏳ P1-8: Исправить зависимости в FocusViewModel
- ⏳ P1-9: Исправить зависимости в UpcomingViewModel
- ⏳ P1-10: Исправить зависимости в KanbanViewModel
- ⏳ P1-11: Исправить Hardcoded версию в build.yml
- ⏳ P1-12: Улучшить проверку Log в CI

---

## 🟡 P2 Задачи (35 штук) - СРЕДНИЙ ПРИОРИТЕТ

- ⏳ P2-1: Удалить закомментированный код
- ⏳ P2-2: Разбить большие файлы (>500 строк)
- ⏳ P2-3: Сделать настройки Pomodoro настраиваемыми
- ⏳ P2-4: Вынести hardcoded константы в constants
- ⏳ P2-5: Улучшить обработку ошибок в TaskRepositoryImpl
- ⏳ P2-6: Стандартизировать сообщения об ошибках

---

## 📊 Известные проблемы

- **Issue:** Конфликты при мердже PR #87 (Logger абстракция)
- **Влияние:** Блокирует P0-2 и зависимые задачи
- **Решение:** Применить изменения вручную или разрешить конфликты

- **Issue:** Отсутствует JDK для сборки в текущей среде
- **Влияние:** Нельзя проверить компиляцию локально
- **Решение:** Работать с кодом напрямую, проверять синтаксис вручную

---

## 🎯 План на текущий сеанс

1. ✅ Завершить P0-1 (уже сделано)
2. 🔄 Разрешить P0-2 (PR #87 Logger абстракция)
3. ⏳ Начать P0-3 (ViewModel → UseCase рефакторинг)


---

## 📝 Заметки

- 2026-09-06: Начата работа по ТЗ
- 2026-09-06: PR #88 успешно замерджен
- 2026-09-06: Обнаружены конфликты при мердже PR #87


---

## 🔄 Сессия 2026-09-08 (Vibe Agent)

### ✅ Выполнено:
- **CI/CD починен:** build.yml — убраны хардкод release notes и хардкод удаления версий
- **TodayScreen:** русская локализация (Today→Сегодня, Overdue→Просрочено, Due today→На сегодня, Backlog→Бэклог)
- **TodayScreen:** тап по задаче открывает TaskDetailSheet (ранее не работал)
- **TodayScreen:** добавлен EmptyState для пустого экрана
- **KanbanScreen:** цветные фоны колонок (синий/оранжевый/зелёный)
- **FinanceScreen:** цветовое кодирование баланса (красный/зелёный/серый)
- **Todoist:** закрыто 27 задач (уже реализованных в коде)
- **Релиз:** v1.3.0 — push в main запускает build.yml для сборки APK

### Коммиты:
- 48fdbd9 — fix: Russian localization, colored kanban columns, CI/CD improvements
- 6f1a4bb — feat: color-coded finance balance
