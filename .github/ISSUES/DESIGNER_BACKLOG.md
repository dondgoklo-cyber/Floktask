# 🎨 Designer Agent Backlog
# **WOLFTASK / Floktask**
# Last Updated: 2026-09-05
# Assigned to: @designer

---

## ✅ **COMPLETED TASKS**

| ID | Задача | Статус | Срок | Выход |
|----|--------|--------|------|-------|
| **DE-001** | **Анализ UI багов** | ✅ Done | 2026-09-03 | `research/ui-bug-analysis.md` |
| **DE-002** | **Design System v1** | ✅ Done | 2026-09-05 | `design-system/tokens/`, `shared/design-tokens/` |

---

## 🔴 **P0: Критично (Новые фичи)**

### **В процессе (Срок: 2026-09-08)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **DE-003** | **Timeline View UI** | Дизайн Timeline View как в TickTick | 🟡 In Progress | ❌ Нет | `ui-design/screens/timeline.fig`, `output/design-specs/timeline-spec.md` |

**Подзадачи:**
- [x] Проанализировать TickTick Timeline
- [x] Создать low-fidelity wireframes
- [ ] Создать high-fidelity mockups
- [ ] Создать интерактивный прототип
- [ ] Написать design specification
- [ ] Закоммитить в `output/design-specs/`

---

## 🟡 **P1: Высокий (Следующие фичи)**

### **Ожидают (Срок: 2026-09-10 - 2026-09-15)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **DE-004** | **Gantt Charts UI** | Дизайн диаграмм Гантта | ⚪ To Do | ❌ Нет | `ui-design/screens/gantt.fig`, `output/design-specs/gantt-spec.md` |
| **DE-005** | **Focus Mode UI** | Дизайн режима фокуса | ⚪ To Do | ❌ Нет | `ui-design/screens/focus-mode.fig`, `output/design-specs/focus-mode-spec.md` |

---

## 🟢 **P1: Высокий (Продолжение)**

### **Срок: 2026-09-15 - 2026-09-25**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **DE-006** | **AI Ассистент UI** | Чат-интерфейс + инлайн-подсказки | ⚪ To Do | ❌ Нет | `ui-design/screens/ai-assistant.fig` |
| **DE-007** | **Темная тема** | Полная палитра для dark mode | ⚪ To Do | ❌ Нет | `design-system/tokens/colors-dark.yaml`, `guidelines/dark-mode.md` |
| **DE-008** | **Оффлайн-режим UI** | Индикаторы статуса, sync status | ⚪ To Do | ❌ Нет | `ui-design/screens/offline-mode.fig` |
| **DE-009** | **Адаптивность** | Breakpoints, responsive guidelines | ⚪ To Do | ❌ Нет | `design-system/guidelines/responsive.md` |
| **DE-010** | **Kanban доска UI** | Улучшение текущей Kanban доски | ⚪ To Do | ❌ Нет | `ui-design/screens/kanban.fig` |

---

## 🟢 **P2: Средний (Улучшения существующего)**

### **Срок: 2026-09-28 - 2026-10-15**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **DE-011** | **Eisenhower Matrix UI** | Улучшение текущей матрицы | ⚪ To Do | ❌ Нет | `ui-design/screens/eisenhower.fig` |
| **DE-012** | **Pomodoro UI** | Улучшение интерфейса Pomodoro | ⚪ To Do | ❌ Нет | `ui-design/screens/pomodoro.fig` |
| **DE-013** | **Геймификация UI** | Badges, progress bars, achievements | ⚪ To Do | ❌ Нет | `ui-design/screens/gamification.fig` |
| **DE-014** | **iOS/Web адаптация** | Платформенные гайдлайны | ⚪ To Do | ❌ Нет | `design-system/guidelines/platforms.md` |

---

## 📅 **ПЛАН НА 2 НЕДЕЛИ**

### **Неделя 1 (2026-09-05 — 2026-09-11)**
- [x] **DE-001**: Анализ UI багов
- [x] **DE-002**: Design System v1 (токены + компоненты + гайдлайны)
- [ ] **DE-003**: Завершить Timeline View (high-fidelity + spec)

### **Неделя 2 (2026-09-12 — 2026-09-18)**
- [ ] **DE-003**: Завершить Timeline View (прототип + final spec)
- [ ] **DE-004**: Начать Gantt Charts (wireframes)
- [ ] **DE-005**: Начать Focus Mode (wireframes)

---

## 🎯 **ТЕКУЩИЙ ФОКУС**

### **DE-003: Timeline View UI**

**Цель:** Создать интуитивный Timeline View интерфейс, похожий на TickTick, но с улучшениями.

**Выполнено:**
- ✅ Анализ конкурентов (TickTick)
- ✅ Low-fidelity wireframes
- ✅ Token system ready

**Осталось:**
- [ ] **High-fidelity mockups** (Figma)
  - Daily View
  - 3-Day View
  - Weekly View
  - Monthly View
  - Task detail in timeline
  - Context menu
  - Quick add
  - Pomodoro integration
  - Time blocking mode

- [ ] **Interactive prototype** (Figma)
  - Drag-and-drop tasks
  - Zoom in/out
  - Switch views
  - Current time animation

- [ ] **Design specification**
  - Component specs
  - Interaction specs
  - State specs
  - Responsive specs
  - Accessibility specs

**Срок:** 2026-09-08

---

## 📁 **СТРУКТУРА ВЫХОДНЫХ ДАННЫХ**

```
agents/designer/
├── design-system/
│   ├── tokens/              # ✅ Готово
│   │   ├── colors.yaml
│   │   ├── typography.yaml
│   │   ├── spacing.yaml
│   │   ├── shadows.yaml
│   │   └── border-radius.yaml
│   ├── components/          # ✅ Готово
│   │   ├── buttons.md
│   │   ├── cards.md
│   │   ├── inputs.md
│   │   └── modals.md
│   └── guidelines/          # ✅ Готово
│       ├── accessibility.md
│       ├── dark-mode.md
│       └── responsive.md
├── research/
│   └── competitive-ui-analysis/
│       └── ticktock-ui-analysis.md  # ✅ Готово
├── wireframes/
│   └── low-fidelity/
│       └── timeline-view-wireframe.md  # ✅ Готово
├── ui-design/
│   └── screens/
│       ├── timeline.fig         # 🟡 В процессе
│       ├── gantt.fig            # ⚪ Ожидает
│       └── focus-mode.fig       # ⚪ Ожидает
└── output/
    ├── design-specs/            # ⚪ Ожидает
    │   ├── timeline-spec.md
    │   ├── gantt-spec.md
    │   └── focus-mode-spec.md
    └── shared/
        └── ui-kit/              # ⚪ Планируется
            ├── components.json
            └── styles.json

shared/
└── design-tokens/               # ✅ Готово
    ├── colors.yaml
    ├── typography.yaml
    ├── spacing.yaml
    ├── shadows.yaml
    └── border-radius.yaml
```

---

## 🔗 **СВЯЗАННЫЕ ЗАДАЧИ ДРУГИХ АГЕНТОВ**

### **Frontend (Зависимости)**
| Задача | Статус | Блокирует |
|--------|--------|-----------|
| FE-001 | ⚪ To Do | ❌ Нет (мои токены готовы) |
| FE-002 | ⚪ To Do | ❌ Нет |
| FE-003 | ⚪ To Do | ❌ Нет |
| FE-012 | ⚪ To Do | ✅ **DE-003** |
| FE-013 | ⚪ To Do | ✅ **DE-004** |
| FE-014 | ⚪ To Do | ✅ **DE-005** |

### **Architect (Зависимости)**
| Задача | Статус | Блокирует |
|--------|--------|-----------|
| AR-001 | ⚪ To Do | ✅ **DE-003** |
| AR-002 | ⚪ To Do | ✅ **DE-004** |
| AR-003 | ⚪ To Do | ✅ **DE-006** |

### **Backend (Нет зависимостей)**
| Задача | Статус |
|--------|--------|
| BE-001 | ⚪ To Do |
| BE-002 | ⚪ To Do |
| BE-003 | ⚪ To Do |
| BE-004 | ⚪ To Do |

---

## 📞 **КОММУНИКАЦИЯ**

### **Ежедневно:**
- [ ] Проверить `.github/ISSUES/FRONTEND_BACKLOG.md` на новые задачи
- [ ] Проверить `.github/ISSUES/ARCHITECT_BACKLOG.md` на новые задачи
- [ ] Проверить `.github/ISSUES/BACKEND_BACKLOG.md` на новые задачи
- [ ] Обновить статус своих задач в этом файле
- [ ] Закоммитить изменения в `vibe/design-[task-id]`

### **По завершении задачи:**
1. Закоммитить все артефакты (figma файлы, markdown, yaml)
2. Запушить в ветку
3. Открыть PR с ссылкой на задачу
4. Упомянуть @frontend, @architect, @backend для ревью
5. Обновить статус задачи в этом файле

---

## 🚀 **СЛЕДУЮЩИЕ ШАГИ**

1. **Завершить DE-003** (Timeline View) — **текущий фокус**
2. **Начать DE-004** (Gantt Charts) — после DE-003
3. **Начать DE-005** (Focus Mode) — после DE-003
4. **Подготовить DE-006-010** — параллельно

---

## 📊 **МЕТРИКИ**

### **DE-002: Design System v1**
- ✅ 5 token files created
- ✅ 4 component docs created
- ✅ 3 guideline docs created
- ✅ Tokens shared with Frontend
- ✅ Merged to main

### **DE-003: Timeline View UI**
- ✅ Competitor analysis complete
- ✅ Low-fidelity wireframes complete
- 🟡 High-fidelity in progress
- ⚪ Prototype pending
- ⚪ Design spec pending

---

**Вопросы?** Создай Issue с label: `design` или напиши @designer

---

*Последнее обновление: 2026-09-05*
*Ответственный: @designer*
