# Design Specification: Gantt Charts
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Status: Draft
# Related Task: DE-004

---

## 📋 Исполнительное резюме

Этот документ определяет **дизайн диаграмм Гантта** для Floktask — визуального инструмента для планирования и отслеживания задач во времени. Дизайн оптимизирован для **личного использования** (в отличие от корпоративных инструментов) и интегрирован с существующими фичами Floktask.

**Ключевые особенности:**
- Горизонтальная временная шкала с дневным/недельным/месячным масштабом
- Drag-and-drop для перемещения и изменения длительности задач
- Визуализация зависимостей между задачами
- Поддержка milestones (вехи)
- Выделение критического пути
- Интеграция с Pomodoro и Time Blocking
- Полная адаптивность для всех устройств

---

## 📊 Исследования

### Конкурентный анализ

| Конкурент | Фича | Дизайн-решение | Наше улучшение |
|-----------|-------|----------------|---------------|
| **Microsoft Project** | Full Gantt | Сложный, корпоративный | ✅ Упростим для личного использования |
| **ClickUp** | Gantt View | Чистый, современный | ✅ Адаптируем для Floktask |
| **Jira** | Gantt | Agile-ориентированный | ✅ Упростим интерфейс |
| **Monday.com** | Timeline | Визуальный, цветной | ✅ Используем нашу палитру |
| **Asana** | Timeline | Простой, интуитивный | ✅ Аналогичный подход |

**Основные выводы:**
- Большинство инструментов **слишком сложные** для личного использования
- Плохая **мобильная оптимизация** у всех конкурентов
- Отсутствует **интеграция с Pomodoro/Time Blocking**
- Можно улучшить **визуальную ясность**

### Пользовательские персоны

**Персона 1: Студент (Alexey)**
- **Цели:** Планировать учебные проекты, следить за дедлайнами
- **Болевые точки:** Сложно визуализировать зависимости между задачами
- **Как Gantt помогает:** Визуальное представление временных рамок проектов

**Персона 2: Профессионал (Maria)**
- **Цели:** Управлять рабочими проектами, координировать задачи
- **Болевые точки:** Нужно видеть зависимости и критический путь
- **Как Gantt помогает:** Визуализация зависимостей, выделение критического пути

**Персона 3: Предприниматель (Dmitry)**
- **Цели:** Планировать несколько проектов одновременно
- **Болевые точки:** Нужно видеть всю картину на месяцы вперед
- **Как Gantt помогает:** Месячный/годовой вид, milestones для важных дат

---

## 🎨 Дизайн-решение

### Информационная архитектура

```
Floktask Gantt Chart
├── Header
│   ├── Title: "Gantt Chart"
│   ├── View Mode Toggles: [Week] [Month] [Year]
│   ├── Action Button: [+ Add Task]
│   └── Toggle: [Critical Path] [Dependencies]
│
├── Time Scale (Horizontal)
│   ├── Level Selector: Day/Week/Month/Year
│   ├── Date Labels: Mon, Tue, Wed, etc.
│   └── Dividers: Vertical grid lines
│
├── Main Content
│   ├── Task List (Left, 25%)
│   │   ├── Task Rows: Title, Dates, Progress
│   │   └── Grouping: By category/priority
│   │
│   └── Gantt Area (Right, 75%)
│       ├── Task Bars: Horizontal bars for each task
│       ├── Dependencies: Arrow lines between tasks
│       ├── Milestones: Diamond shapes
│       ├── Critical Path: Highlighted tasks
│       └── Today Indicator: Vertical line
│
└── Bottom Actions (Mobile)
    ├── Zoom Controls
    └── Quick Actions
```

---

## 🎨 Визуальный дизайн

### Цветовая палитра

| Элемент | Цвет (Light) | Цвет (Dark) | Token |
|---------|--------------|------------|-------|
| **Task Bars (Default)** | Category Colors | Category Colors | `gantt.bar.default` |
| **Task Bars (Hover)** | Category Colors (90%) | Category Colors (90%) | `gantt.bar.hover` |
| **Task Bars (Selected)** | Category Colors (80%) | Category Colors (80%) | `gantt.bar.selected` |
| **Progress Fill** | `#8BC34A` | `#8BC34A` | `gantt.progress.fill` |
| **Dependencies** | `#9E9E9E` | `#424242` | `gantt.dependency.line` |
| **Dependencies (Critical)** | `#F44336` | `#FF6B6B` | `gantt.dependency.arrow` |
| **Milestones** | `#9C27B0` | `#BA68C8` | `gantt.milestone.background` |
| **Critical Path** | `#FFEBEE` (bg) + `#F44336` (border) | `#2D1B10` (bg) + `#FF6B6B` (border) | `gantt.critical_path` |
| **Time Scale Background** | `#FAFAFA` | `#1E1E1E` | `gantt.time_scale.background` |
| **Time Scale Text** | `#424242` | `#CACACA` | `gantt.time_scale.text` |
| **Grid Lines** | `#E0E0E0` | `#333333` | `gantt.grid.line` |
| **Major Grid Lines** | `#BDBDBD` | `#424242` | `gantt.grid.major_line` |
| **Today Indicator** | `#FF9800` | `#FF8C00` | `gantt.today` |

**Цвета задач по категории:**
```yaml
gantt:
  bar:
    category:
      work: "#4CAF50"      # Green
      personal: "#FF9800"   # Orange (Brand)
      study: "#2196F3"     # Blue
      shopping: "#9C27B0"   # Purple
      health: "#F44336"    # Red
      finance: "#FFC107"   # Amber
      other: "#9E9E9E"     # Gray
```

**Цвета задач по приоритету:**
```yaml
gantt:
  bar:
    priority:
      low: "#9E9E9E"       # Gray
      medium: "#FFC107"    # Amber
      high: "#FF9800"      # Orange
      critical: "#F44336"   # Red
```

---

### Типографика

| Элемент | Стиль | Token |
|---------|-------|-------|
| **Screen Title** | Title Large (22sp, Bold) | `typography.title.large` |
| **Task Name (List)** | Body Medium (14sp, Regular) | `typography.body.medium` |
| **Task Name (Gantt)** | 12sp, Semi-Bold | Custom |
| **Task Duration** | 10sp, Regular | Custom |
| **Time Scale (Day)** | 11sp, Medium | `typography.timeline.time` |
| **Time Scale (Week/Month)** | 10sp, Medium | Custom |
| **Milestone Label** | 10sp, Bold | Custom |
| **Progress Text** | 8sp, Medium | Custom |

---

### Отступы и размеры

| Элемент | Размер | Token |
|---------|--------|-------|
| **Time Scale Height (Day)** | 40dp | Custom |
| **Time Scale Height (Week)** | 48dp | Custom |
| **Time Scale Height (Month)** | 56dp | Custom |
| **Time Scale Height (Year)** | 64dp | Custom |
| **Task List Width** | 25% | Custom |
| **Task List Min Width** | 200dp | Custom |
| **Task List Max Width** | 300dp | Custom |
| **Task Bar Height** | 32dp | Custom |
| **Task Bar Selected Height** | 40dp | Custom |
| **Task Bar Border Radius** | 4dp | `border_radius.xxs` |
| **Task Bar Padding** | 8dp (h) x 4dp (v) | `spacing.sm` x `spacing.xs` |
| **Task Bar Margin** | 4dp | `spacing.xs` |
| **Milestone Size** | 24dp x 24dp | Custom |
| **Milestone Border Radius** | 2dp | `border_radius.xs` |
| **Dependency Line Width** | 2dp | Custom |
| **Today Indicator Width** | 2dp | Custom |
| **Grid Line Height** | 1dp | Custom |
| **Major Grid Line Height** | 2dp | Custom |

---

### Border Radius

| Элемент | Radius | Token |
|---------|--------|-------|
| **Task Bars** | 4dp | `border_radius.xxs` |
| **Milestones** | 2dp | `border_radius.xs` |
| **Time Scale** | 0dp | `border_radius.none` |
| **Dialogs/Modals** | 12dp | `border_radius.md` |
| **Buttons** | 12dp | `border_radius.md` |

---

### Тени

| Элемент | Shadow | Token |
|---------|--------|-------|
| **Task Bars (Default)** | Level 1 | `shadows.level_1` |
| **Task Bars (Hover)** | Level 2 | `shadows.level_2` |
| **Task Bars (Selected)** | Level 3 | `shadows.level_3` |
| **Dialogs** | Level 5 | `shadows.level_5` |
| **Context Menu** | Level 3 | `shadows.level_3` |

---

## 📐 Спецификации

### Размеры и отступы

#### Time Scale
| Свойство | Значение | Token |
|----------|----------|-------|
| Height (Day View) | 40dp | Custom |
| Height (Week View) | 48dp | Custom |
| Height (Month View) | 56dp | Custom |
| Height (Year View) | 64dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Text Color | On Surface Variant | `colors.on_surface_variant` |
| Divider Height | 1dp | Custom |
| Divider Color | Outline Variant | `colors.outline_variant` |

#### Task List
| Свойство | Значение | Token |
|----------|----------|-------|
| Width | 25% | Custom |
| Min Width | 200dp | Custom |
| Max Width | 300dp | Custom |
| Background | Surface | `colors.surface` |
| Row Height | 40dp | `spacing.xxl_2` |
| Padding | 12dp | `spacing.sm_2` |
| Typography | Body Medium | `typography.body.medium` |

#### Task Bars
| Свойство | Значение | Token |
|----------|----------|-------|
| Height | 32dp | Custom |
| Selected Height | 40dp | Custom |
| Border Radius | 4dp | `border_radius.xxs` |
| Padding | 8dp x 4dp | `spacing.sm` x `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Elevation (Default) | Level 1 | `shadows.level_1` |
| Elevation (Hover) | Level 2 | `shadows.level_2` |

#### Milestones
| Свойство | Значение | Token |
|----------|----------|-------|
| Size | 24dp x 24dp | Custom |
| Border Radius | 2dp | `border_radius.xs` |
| Background | Milestone Color | `gantt.milestone.background` |
| Border | White, 2dp | Custom |
| Text Color | White | `gantt.milestone.text` |

#### Dependencies
| Свойство | Значение | Token |
|----------|----------|-------|
| Line Width | 2dp | Custom |
| Line Color | Gray | `gantt.dependency.line` |
| Line Style | Dashed | Custom |
| Arrow Size | 8dp | Custom |
| Arrow Color | Gray (Red for critical) | `gantt.dependency.arrow` |

---

### Состояния

#### Task Bar States

| Состояние | Background | Border | Elevation | Opacity |
|-----------|------------|--------|-----------|---------|
| **Default** | Category Color | None | Level 1 | 100% |
| **Hover** | Category Color (90%) | None | Level 2 | 100% |
| **Selected** | Category Color (80%) | Primary 500 (2dp) | Level 3 | 100% |
| **Pressed** | Category Color | None | Level 0 | 100% |
| **Completed** | Category Color (40%) | None | Level 1 | 40% |
| **Overdue** | Error 50 (12%) | Error 500 (1dp) | Level 1 | 100% |
| **Dragging** | Category Color | Primary 500 (2dp) | Level 4 | 80% |

#### Dependency States

| Состояние | Line Color | Line Style | Arrow Color |
|-----------|------------|------------|-------------|
| **Default** | Gray | Dashed | Gray |
| **Hover** | Primary 500 | Solid | Primary 500 |
| **Selected** | Primary 500 | Solid | Primary 500 |
| **Critical** | Red | Solid | Red |

---

### Анимации

| Анимация | Длительность | Easing | Триггер |
|----------|-------------|--------|---------|
| **Task Drag Start** | 0ms | Linear | Drag start |
| **Task Drag** | Direct | Linear | During drag |
| **Task Drop** | 200ms | Ease-out | Drag end |
| **Task Resize** | 150ms | Ease-out | Resize end |
| **Task Hover** | 100ms | Linear | Mouse over |
| **Dependency Create** | 200ms | Ease-out | Drop on target |
| **Zoom In/Out** | 250ms | Ease-in-out | Pinch/Scroll |
| **Pan** | 0ms | Linear | Drag |
| **View Switch** | 200ms | Ease-in-out | Click |
| **Context Menu Open** | 150ms | Ease-out | Right-click |
| **Context Menu Close** | 100ms | Ease-in | Click outside |

---

## 🔗 Зависимости

### От Product Manager
- ✅ Требования к Gantt Charts (из TZ.md)
- ✅ Приоритеты фич

### От Architect
- ⏳ API контракты для Gantt Charts (**AR-002**)
- ⏳ Определение моделей данных

### От Backend
- ⏳ Sync Engine для синхронизации (**BE-001**)
- ⏳ Backup API для экспорта/импорта (**BE-002**)

### От Frontend
- ⏳ Интеграция токенов (**FE-001**)
- ⏳ Theme System (**FE-002**)
- ⏳ Обновленные компоненты (**FE-003**)

---

## ✅ Критерии приемки

### Дизайн
- [ ] Дизайн соответствует бренд-гайдлайнам Floktask
- [ ] Все состояния компонентов проработаны
- [ ] Цветовая палитра соответствует токенам
- [ ] Типографика соответствует токенам
- [ ] Отступы соответствуют токенам
- [ ] Дизайн адаптирован для всех размеров экранов
- [ ] Учтены требования доступности (контраст 4.5:1+)
- [ ] Поддержка темной темы

### Прототип
- [ ] Прототип тестирован на основных сценариях
- [ ] Drag-and-drop работает плавно
- [ ] Resize задач работает корректно
- [ ] Создание зависимостей работает
- [ ] Zoom и pan работают
- [ ] Анимации плавные и интуитивные

### Документация
- [ ] Design spec завершен
- [ ] Все компоненты документированы
- [ ] Взаимодействия документированы
- [ ] Токены обновлены в `shared/design-tokens/`

### Интеграция
- [ ] Токены интегрированы в Frontend (**FE-001**)
- [ ] API контракты готовы (**AR-002**)
- [ ] Backend поддерживает синхронизацию (**BE-001**)

---

## 📁 Файлы

### Design Files (Figma)
- **Main File:** `ui-design/screens/gantt.fig` (планируется)
- **Components:** `ui-design/components/gantt-components.fig` (планируется)
- **Prototype:** `prototypes/interactive/gantt-prototype.fig` (планируется)

### Documentation Files
- **Design Spec:** `output/design-specs/gantt-spec.md` (этот файл)
- **Wireframes:** `wireframes/low-fidelity/gantt-charts-wireframe.md`
- **Research:** `research/competitive-ui-analysis/gantt-charts-ui-analysis.md`

### Shared Files
- **UI Kit:** `output/shared/ui-kit/` (обновлено для Gantt)

---

## 📅 Сроки

| Этап | Срок | Статус |
|------|------|--------|
| **Исследование** | 2026-09-05 | ✅ Готово |
| **Wireframes** | 2026-09-05 | ✅ Готово |
| **High-Fidelity Design** | 2026-09-10 | 🟡 В процессе |
| **Prototype** | 2026-09-12 | ⚪ Планируется |
| **Design Spec Final** | 2026-09-13 | ⚪ Планируется |
| **Frontend Integration** | 2026-09-20 | ⚪ Ожидает FE-013 |
| **Testing & Iteration** | 2026-09-25 | ⚪ Планируется |

---

## 🎯 Следующие шаги

### Для меня (Designer):
1. [ ] Завершить high-fidelity дизайн Gantt Charts
2. [ ] Создать интерактивный прототип
3. [ ] Написать final design spec
4. [ ] Отдать на ревью @frontend и @product-manager

### Для Frontend:
1. [ ] Дождаться final design spec
2. [ ] Реализовать GanttScreen по спецификации
3. [ ] Интегрировать drag-and-drop
4. [ ] Интегрировать zoom и pan
5. [ ] Интегрировать зависимости

### Для Architect:
1. [ ] Дождаться final design spec
2. [ ] Разработать API контракты для Gantt
3. [ ] Определить модели данных

---

## 📞 Контакты и коммуникация

### Для вопросов по дизайну:
- Создать Issue с label: `design`
- Упомянуть @designer
- Ссылка на этот документ

### Для координации:
- **Frontend:** @frontend
- **Architect:** @architect
- **Backend:** @backend
- **Product Manager:** @product-manager

---

## 🔗 Ссылки

- [Gantt Charts UI Analysis](../../research/competitive-ui-analysis/gantt-charts-ui-analysis.md)
- [Gantt Wireframes](../../wireframes/low-fidelity/gantt-charts-wireframe.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Timeline Spec](../timeline-spec.md)
- [TZ.md](../../../TZ.md)
- [NEXT_SESSION_TASK.md](../../../NEXT_SESSION_TASK.md)

---

*Документ подготовлен для задачи DE-004*
*Последнее обновление: 2026-09-05*
*Статус: Draft (в процессе)*
