# Design Specification: Timeline View
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Status: Draft
# Related Task: DE-003
# Related Issue: [Will be created]

---

## 📋 Исполнительное резюме

Этот документ определяет **дизайн Timeline View** для Floktask — интуитивного интерфейса для визуализации и управления задачами во временной шкале. Дизайн основан на анализе TickTick с улучшениями для интеграции с существующими фичами Floktask (Pomodoro, Finance, Tasks).

**Ключевые особенности:**
- Горизонтальная временная шкала с часами
- Drag-and-drop перетаскивание задач
- Поддержка нескольких режимов просмотра (День, 3 дня, Неделя, Месяц)
- Интеграция с Pomodoro таймером
- Поддержка Time Blocking
- Адаптивный дизайн для всех устройств

---

## 📊 Исследования

### Конкурентный анализ

| Конкурент | Фича | Дизайн-решение TickTick | Наше улучшение |
|-----------|-------|----------------------|---------------|
| **TickTick** | Timeline View | Горизонтальная шкала, цветные блоки | + Pomodoro интеграция, + Time Blocking |
| **TickTick** | Drag-and-drop | Плавное перетаскивание с snap-to-grid | + Smart suggestions, + Better visual feedback |
| **TickTick** | Zoom Levels | День/3 дня/Неделя/Месяц | + Кастомные временные блоки |
| **Todoist** | Timeline | Вертикальная шкала | Мы используем горизонтальную (как TickTick) |
| **Notion** | Calendar View | Календарная сетка | Мы фокусируемся на временной шкале |

### Пользовательские персоны

**Персона 1: Студент (Alexey)**
- **Цели:** Организовать учебное время, следить за дедлайнами
- **Болевые точки:** Сложно планировать время на задачи разной длительности
- **Как Timeline помогает:** Визуальное распределение времени между предметами и проектами

**Персона 2: Профессионал (Maria)**
- **Цели:** Управлять рабочими задачами, встречами, личными делами
- **Болевые точки:** Много задач накладываются, сложно перепланировать
- **Как Timeline помогает:** Быстрое перетаскивание, интеграция с Pomodoro для фокуса

**Персона 3: Предприниматель (Dmitry)**
- **Цели:** Управлять несколькими проектами, блокировать время
- **Болевые точки:** Нужно видеть всю картину на неделю вперед
- **Как Timeline помогает:** Недельный режим, Time Blocking, визуализация приоритетов

---

## 🎨 Дизайн-решение

### Информационная архитектура

```
Floktask Timeline View
├── Header
│   ├── Title: "Timeline"
│   ├── View Mode Toggles: [Today] [3-Day] [Week] [Month]
│   └── Action Button: [+ Add Task]
│
├── Time Axis (Left Side, 56dp)
│   ├── Hour Labels (00:00 - 23:00)
│   └── Current Time Indicator (Red Line)
│
├── Content Area
│   ├── Background: Today (Premium Subtle), Future (Surface), Past (Surface Variant)
│   ├── Hour Grid Lines
│   ├── Task Blocks
│   │   ├── Regular Tasks
│   │   ├── All-Day Tasks
│   │   ├── Time Blocks
│   │   └── Overlap Indicators
│   └── Current Time Label: "СЕЙЧАС"
│
└── Bottom Actions (Optional)
    ├── Pomodoro Controls
    └── Quick Actions
```

---

## 🎨 Визуальный дизайн

### Цветовая палитра

| Элемент | Цвет (Light) | Цвет (Dark) | Token |
|---------|--------------|------------|-------|
| **Today Background** | `#FFF3E0` | `#2D1B10` | `premium.subtle_background` |
| **Future Background** | `#FFFFFF` | `#121212` | `colors.surface` |
| **Past Background** | `#F5F5F5` | `#1E1E1E` | `colors.surface_variant` |
| **Current Time Line** | `#F44336` | `#FF6B6B` | `colors.error_500` |
| **Task Blocks** | Category Colors | Category Colors | See Task Colors |
| **All-Day Tasks** | `#E0F7FA` (12%) | `#003C49` (24%) | Custom |
| **Time Grid Lines** | `#E0E0E0` | `#333333` | `colors.outline_variant` |
| **Hour Labels** | `#666666` | `#CCCCCC` | `colors.on_surface_variant` |

**Цвета задач по категории:**
```yaml
task:
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
task:
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
| **Title (Header)** | Title Large (22sp, Bold) | `typography.title.large` |
| **Day Header** | Title Medium (18sp, Semi-Bold) | `typography.title.medium` |
| **Time Axis Labels** | 11sp, Medium | `typography.timeline.time` |
| **Task Title** | 14sp, Semi-Bold | `typography.task.title` |
| **Task Time** | 12sp, Regular | `typography.body.small` |
| **Current Time Label** | 10sp, Medium | Custom |
| **Overlap Indicator** | 12sp, Medium | `typography.label.medium` |

---

### Отступы и размеры

| Элемент | Размер | Token |
|---------|--------|-------|
| **Time Axis Width** | 56dp | Custom |
| **Task Block Min Width** | 100dp | Custom |
| **Task Block Min Height** | 40dp | Custom |
| **Task Block Padding** | 8dp (h) x 4dp (v) | `spacing.sm` x `spacing.xs` |
| **Task Block Margin** | 4dp | `spacing.xs` |
| **Current Time Line Height** | 2dp | Custom |
| **Hour Grid Line Height** | 1dp | Custom |
| **Between Tasks (no overlap)** | 16dp | `spacing.md` |
| **Between Tasks (overlap)** | 4dp | `spacing.xs` |

---

### Border Radius

| Элемент | Radius | Token |
|---------|--------|-------|
| **Task Blocks** | 8dp | `border_radius.sm_2` |
| **All-Day Tasks** | 4dp | `border_radius.xxs` |
| **Time Blocks** | 12dp | `border_radius.md` |
| **Current Time Indicator** | 0dp | `border_radius.none` |

---

### Тени

| Элемент | Shadow | Token |
|---------|--------|-------|
| **Task Blocks (Default)** | Level 1 | `shadows.level_1` |
| **Task Blocks (Hover)** | Level 2 | `shadows.level_2` |
| **Task Blocks (Selected)** | Level 3 | `shadows.level_3` |
| **Time Blocks** | Level 2 | `shadows.level_2` |

---

## 📐 Спецификации

### Размеры и отступы

#### Time Axis
| Свойство | Значение | Token |
|----------|----------|-------|
| Width | 56dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Label Color | On Surface Variant | `colors.on_surface_variant` |
| Label Font | 11sp Medium | `typography.timeline.time` |
| Label Padding | 4dp | `spacing.xs` |
| Hour Height (Daily) | 60dp | Custom |
| Hour Height (3-Day) | 30dp | Custom |
| Hour Height (Weekly) | 15dp | Custom |

#### Task Blocks
| Свойство | Значение | Token |
|----------|----------|-------|
| Min Width | 100dp | Custom |
| Min Height | 40dp | Custom |
| Padding | 8dp x 4dp | `spacing.sm` x `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Elevation (Default) | Level 1 | `shadows.level_1` |
| Elevation (Hover) | Level 2 | `shadows.level_2` |

#### Current Time Indicator
| Свойство | Значение | Token |
|----------|----------|-------|
| Height | 2dp | Custom |
| Color | Error 500 | `colors.error_500` |
| Label | "СЕЙЧАС" | - |
| Label Font | 10sp Medium | Custom |
| Label Color | Error 500 | `colors.error_500` |
| Animation | Smooth transition (1000ms) | Custom |

---

### Состояния

#### Task Block States

| Состояние | Background | Border | Elevation | Opacity |
|-----------|------------|--------|-----------|---------|
| **Default** | Category Color | None | Level 1 | 100% |
| **Hover** | Category Color | None | Level 2 | 100% |
| **Selected** | Category Color | Primary 500 (2dp) | Level 3 | 100% |
| **Pressed** | Category Color (80%) | None | Level 0 | 100% |
| **Completed** | Category Color (40%) | None | Level 1 | 40% |
| **Overdue** | Error 50 (12%) | Error 500 (1dp) | Level 1 | 100% |
| **Dragging** | Category Color | Primary 500 (2dp) | Level 4 | 80% |

#### View Mode States

| Режим | Описание | Масштаб |
|-------|----------|---------|
| **Daily** | Показывает один день | 1 час = 60dp |
| **3-Day** | Показывает 3 дня | 1 час = 30dp |
| **Weekly** | Показывает 7 дней | 1 час = 15dp |
| **Monthly** | Календарный вид | 1 день = 1 ячейка |

---

### Анимации

| Анимация | Длительность | Easing | Триггер |
|----------|-------------|--------|---------|
| **Task Drag Start** | 0ms | Linear | Drag start |
| **Task Drag** | Direct | Linear | During drag |
| **Task Drop** | 200ms | Ease-out | Drag end |
| **Task Snap** | 150ms | Ease-out | Auto-align |
| **Task Hover** | 100ms | Linear | Mouse over |
| **Zoom In/Out** | 300ms | Ease-in-out | Pinch gesture |
| **View Switch** | 250ms | Ease-in-out | Swipe/Click |
| **Current Time Update** | 1000ms | Linear | Every minute |
| **Pomodoro Timer** | 1000ms | Linear | Every second |

---

## 🔗 Зависимости

### От Product Manager
- ✅ Требования к Timeline View (из TZ.md и NEXT_SESSION_TASK.md)
- ✅ Приоритеты фич

### От Architect
- ⏳ API контракты для Timeline View (**AR-001**)
- ⏳ Определение моделей данных

### От Backend
- ⏳ Sync Engine для оффлайн работы (**BE-001**)
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

### Прототип
- [ ] Прототип тестирован на основных сценариях
- [ ] Drag-and-drop работает плавно
- [ ] Zoom работает корректно
- [ ] Переключение режимов работает
- [ ] Анимации плавные и интуитивные

### Документация
- [ ] Design spec завершен
- [ ] Все компоненты документированы
- [ ] Взаимодействия документированы
- [ ] Токены обновлены в `shared/design-tokens/`

### Интеграция
- [ ] Токены интегрированы в Frontend (**FE-001**)
- [ ] API контракты готовы (**AR-001**)
- [ ] Backend поддерживает синхронизацию (**BE-001**)

---

## 📁 Файлы

### Design Files (Figma)
- **Main File:** `ui-design/screens/timeline.fig`
- **Components:** `ui-design/components/timeline-components.fig`
- **Prototype:** `prototypes/interactive/timeline-prototype.fig`

### Documentation Files
- **Design Spec:** `output/design-specs/timeline-spec.md` (этот файл)
- **Wireframes:** `wireframes/low-fidelity/timeline-view-wireframe.md`
- **Research:** `research/competitive-ui-analysis/ticktock-ui-analysis.md`

### Shared Files
- **Tokens:** `shared/design-tokens/`
- **UI Kit:** `output/shared/ui-kit/components.json` (планируется)

---

## 📅 Сроки

| Этап | Срок | Статус |
|------|------|--------|
| **Исследование** | 2026-09-03 | ✅ Готово |
| **Wireframes** | 2026-09-05 | ✅ Готово |
| **High-Fidelity Design** | 2026-09-08 | 🟡 В процессе |
| **Prototype** | 2026-09-09 | ⚪ Планируется |
| **Design Spec Final** | 2026-09-10 | ⚪ Планируется |
| **Frontend Integration** | 2026-09-15 | ⚪ Ожидает FE-012 |
| **Testing & Iteration** | 2026-09-20 | ⚪ Планируется |

---

## 🎯 Следующие шаги

### Для меня (Designer):
1. [ ] Завершить high-fidelity дизайн Timeline View
2. [ ] Создать интерактивный прототип
3. [ ] Написать final design spec
4. [ ] Отдать на ревью @frontend и @product-manager

### Для Frontend:
1. [ ] Дождаться final design spec
2. [ ] Реализовать TimelineScreen по спецификации
3. [ ] Интегрировать drag-and-drop
4. [ ] Интегрировать zoom функционал

### Для Architect:
1. [ ] Дождаться final design spec
2. [ ] Разработать API контракты для Timeline
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

- [TickTick UI Analysis](../research/competitive-ui-analysis/ticktick-ui-analysis.md)
- [Timeline Wireframes](../wireframes/low-fidelity/timeline-view-wireframe.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [TZ.md](../../../TZ.md)
- [NEXT_SESSION_TASK.md](../../../NEXT_SESSION_TASK.md)

---

*Документ подготовлен для задачи DE-003*
*Последнее обновление: 2026-09-05*
*Статус: Draft (в процессе)*
