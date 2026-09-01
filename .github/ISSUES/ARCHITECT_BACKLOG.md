# 🏗️ Architect Agent Backlog
# **WOLFTASK / Floktask**
# Last Updated: 2026-09-05
# Assigned to: @architect

---

## 🔴 **P0: Критично (Архитектура)**

### **Немедленно (Срок: 2026-09-06 - 2026-09-11)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **AR-004** | **Offline-First Архитектура** | Проектирование offline-first подхода, конфликты, queue | ⚪ To Do | ❌ Нет | `Architecture.md`, API контракты |
| **AR-005** | **Modularization** | Разделение на feature модули (tasks, finance, notes, etc.) | ⚪ To Do | ❌ Нет | `app/build.gradle.kts`, module structure |

---

## 🟡 **P1: Высокий (API Контракты)**

### **Для новых фич (Срок: после соответствующих DE задач)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **AR-001** | **API для Timeline View** | Контракты для Timeline View (задачи, время, блоки) | ⚪ To Do | DE-003 | `api/timeline/` |
| **AR-002** | **API для Gantt Charts** | Контракты для диаграмм Гантта | ⚪ To Do | DE-004 | `api/gantt/` |
| **AR-003** | **API для AI Ассистента** | Контракты для AI подсказок и генерации | ⚪ To Do | DE-006 | `api/ai/` |

---

## 🟢 **P2: Средний (Улучшения)**

| ID | Задача | Описание | Статус | Срок |
|----|--------|----------|--------|------|
| **AR-006** | **API для Time Blocking** | Контракты для блокировки времени | ⚪ To Do | 2026-10-01 |
| **AR-007** | **API для Kanban** | Контракты для Канбан доски | ⚪ To Do | 2026-10-05 |
| **AR-008** | **API для Геймификации** | Контракты для badges, achievements | ⚪ To Do | 2026-10-10 |
| **AR-009** | **Caching Strategy** | Стратегия кэширования для оффлайн работы | ⚪ To Do | 2026-10-15 |

---

## 📅 **ПЛАН НА 2 НЕДЕЛИ**

### **Неделя 1 (2026-09-05 — 2026-09-11)**
- [ ] **AR-004**: Offline-First Архитектура
- [ ] **AR-005**: Modularization
- [ ] Подготовить API контракты для Budget/Goal (для FE-006, FE-007)

### **Неделя 2 (2026-09-12 — 2026-09-18)**
- [ ] **AR-001**: Timeline API (если DE-003 готово)
- [ ] **AR-002**: Gantt API (если DE-004 готово)
- [ ] Координироваться с @backend по реализации

---

## 🎯 **ТЕХНИЧЕСКИЕ ТРЕБОВАНИЯ**

### **AR-004: Offline-First Архитектура**
```kotlin
// Требуется определить:
- Sync strategy (periodic, manual, on-demand)
- Conflict resolution (last write wins, custom rules)
- Data flow (local → remote → local)
- Error handling (retry, queue, notification)
- Offline queue management
```

**API Контракты:**
```kotlin
// Пример для Sync Engine
data class SyncRequest(
    val lastSync: Instant,
    val changes: List<EntityChange>,
    val deviceId: String
)

data class SyncResponse(
    val serverChanges: List<EntityChange>,
    val conflicts: List<SyncConflict>,
    val newToken: String
)
```

### **AR-005: Modularization**
```kotlin
// Требуется:
- Разделить на модули: core, data, domain, features/*
- Определить зависимости между модулями
- Создать feature модули: tasks, finance, notes, habits, kanban, timeline, gantt
- Настроить Gradle для multi-module проекта
```

**Структура модулей:**
```
app/
├── core/
│   ├── common/
│   ├── data/
│   ├── domain/
│   └── ui/
├── features/
│   ├── tasks/
│   ├── finance/
│   ├── notes/
│   ├── habits/
│   ├── kanban/
│   ├── timeline/
│   ├── gantt/
│   └── focus/
```

### **AR-001: Timeline API**
```kotlin
// Пример контракта
data class TimelineTask(
    val id: String,
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val categoryId: String?,
    val priority: Priority,
    val color: String,
    val isAllDay: Boolean,
    val isCompleted: Boolean
)

data class TimelineRequest(
    val dateRange: DateRange,
    val zoomLevel: ZoomLevel,
    val filters: TimelineFilters
)

data class TimelineResponse(
    val tasks: List<TimelineTask>,
    val timeBlocks: List<TimeBlock>,
    val currentTime: Instant,
    val allDayTasks: List<AllDayTask>
)
```

---

## 📁 **РЕСУРСЫ**

### **Существующая архитектура**
- 📄 [TZ.md](../TZ.md) - Техническое задание
- 📄 [NEXT_SESSION_TASK.md](../NEXT_SESSION_TASK.md) - Текущие задачи
- 📁 `app/` - Текущая структура проекта

### **Design Ресурсы**
- 📁 `agents/designer/design-system/` - Дизайн-система
- 📁 `shared/design-tokens/` - Токены дизайна
- 📄 [Timeline Analysis](../agents/designer/research/competitive-ui-analysis/ticktick-ui-analysis.md)

---

## ✅ **КРИТЕРИИ ПРИЕМКИ**

### Для всех задач:
- [ ] API контракты документированы
- [ ] Интерфейсы определены
- [ ] Обработка ошибок специфицирована
- [ ] Тесты на интерфейсы
- [ ] Документация обновлена

### Для архитектурных решений:
- [ ] Соответствует Clean Architecture
- [ ] Поддерживает offline-first
- [ ] Масштабируемое решение
- [ ] Легко тестируемое
- [ ] Документированное

---

## 🔗 **СВЯЗАННЫЕ ЗАДАЧИ**

### **Designer**
- [DE-002](DE-002) ✅ Design System v1
- [DE-003](DE-003) 🟡 Timeline View UI
- [DE-004](DE-004) ⚪ Gantt Charts UI
- [DE-005](DE-005) ⚪ Focus Mode UI
- [DE-006](DE-006) ⚪ AI Ассистент UI

### **Frontend**
- [FE-001](FE-001) ⚪ Token Integration
- [FE-005](FE-005) ⚪ Transaction Edit
- [FE-006](FE-006) ⚪ Budgets Module
- [FE-012](FE-012) ⚪ Timeline View
- [FE-013](FE-013) ⚪ Gantt Charts

### **Backend**
- [BE-001](BE-001) ⚪ Sync Engine
- [BE-002](BE-002) ⚪ Backup API
- [BE-003](BE-003) ⚪ AI Service

---

## 📞 **КОММУНИКАЦИЯ**

### **Ежедневно:**
- Проверить новые задачи в этом файле
- Обновить статус своих задач
- Закоммитить изменения в `feature/architect-[task-id]`

### **По завершении задачи:**
1. Закоммитить API контракты в `docs/api/`
2. Запушить в ветку
3. Открыть PR с ссылкой на задачу
4. Упомянуть @designer и @frontend для ревью
5. Обновить статус задачи

---

## 🚀 **СЛЕДУЮЩИЕ ШАГИ**

1. **Начать с AR-004** (Offline-First) — **не зависит от других!**
2. **Параллельно работать** над AR-005 (Modularization)
3. **Готовиться к AR-001** (Timeline API) — ждать DE-003
4. **Координироваться с @backend** по реализации API

---

**Вопросы?** Создай Issue с label: `architecture` или напиши @architect

---

*Последнее обновление: 2026-09-05*
*Ответственный: @architect*
