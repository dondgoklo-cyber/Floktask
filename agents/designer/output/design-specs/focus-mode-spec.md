# Design Specification: Focus Mode
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Status: Draft
# Related Task: DE-005

---

## 📋 Исполнительное резюме

Этот документ определяет **дизайн режима фокуса (Focus Mode)** для Floktask — специализированного интерфейса для концентрации на одной задаче без отвлечений. Режим фокуса интегрирован с системой задач, Pomodoro таймером и статистикой производительности.

**Ключевые особенности:**
- Полноэкранный интерфейс без отвлекающих элементов
- Большой таймер с кольцевым индикатором прогресса
- Текущая задача отображается проминентно
- Интеграция с Pomodoro (работа/перерыв)
- Блокировка отвлечений (опционально)
- Статистика фокус-сессий
- Адаптивный дизайн для всех устройств

---

## 📊 Исследования

### Конкурентный анализ

| Конкурент | Фича | Дизайн-решение | Наше улучшение |
|-----------|-------|----------------|---------------|
| **Forest** | Геймифицированный фокус | Дерево растет во время сессии | ✅ Интеграция с задачами Floktask |
| **Focus To-Do** | Минималистский фокус | Чистый интерфейс, Pomodoro | ✅ Глубокая интеграция с задачами |
| **Apple Focus** | Системный режим | Блокировка уведомлений | ✅ На уровне задач, а не системы |
| **Notion** | Режим фокуса | Скрывает UI, только контент | ✅ Для задач, а не только для текста |
| **Brain.fm** | Audio фокус | Музыка для концентрации | ✅ Визуальный интерфейс + audio |

**Основные выводы:**
- Большинство приложений **не интегрированы с задачами**
- **Геймификация** работает для мотивации (Forest)
- **Минимализм** важен для фокуса (Focus To-Do)
- **Блокировка отвлечений** полезна, но должна быть опциональной

### Пользовательские персоны

**Персона 1: Студент (Alexey)**
- **Цели:** Сконцентрироваться на учебе, избегать отвлечений
- **Болевые точки:** Легко отвлекается на соцсети
- **Как Focus Mode помогает:** Блокировка отвлекающих приложений, таймер для контроля времени

**Персона 2: Профессионал (Maria)**
- **Цели:** Глубокая работа над сложными задачами
- **Болевые точки:** Постоянные уведомления, многозадачность
- **Как Focus Mode помогает:** Полноэкранный режим, Pomodoro сессии, статистика

**Персона 3: Предприниматель (Dmitry)**
- **Цели:** Эффективное использование времени
- **Болевые точки:** Много задач, сложно сконцентрироваться
- **Как Focus Mode помогает:** Выбор приоритетных задач, трекинг времени

---

## 🎨 Дизайн-решение

### Информационная архитектура

```
Floktask Focus Mode
├── Header
│   ├── Title: "Focus Mode"
│   ├── Close Button: [✕]
│   └── Settings Button: [⚙️] (optional)
│
├── Main Content
│   ├── Timer Area
│   │   ├── Progress Ring (200dp diameter)
│   │   ├── Timer Digits (64sp)
│   │   ├── Timer Label (Pomodoro #1, Break, etc.)
│   │   └── Status (Working..., Paused, Completed!)
│   │
│   ├── Task Display
│   │   ├── Task Title (20sp Semi-Bold)
│   │   ├── Task Subtitle (14sp Regular)
│   │   └── Task Info (Priority, Due Date, etc.)
│   │
│   └── Control Bar
│       ├── Start Button [▶️]
│       ├── Pause Button [⏸️]
│       └── Stop Button [⏹️]
│
├── Bottom Area
│   ├── Next Tasks Preview (3 items)
│   ├── Goal Progress
│   └── Statistics Preview
│
└── Bottom Bar
    ├── Distraction Blocking Toggle [🔒]
    └── Statistics Button [📈]
```

---

## 🎨 Визуальный дизайн

### Цветовая палитра

| Элемент | Цвет (Light) | Цвет (Dark) | Token |
|---------|--------------|------------|-------|
| **Background** | `#FAFAFA` | `#1A1A1A` | `focus_mode.background` |
| **Timer Active** | `#FF9800` | `#FF8C00` | `focus_mode.timer.active` |
| **Timer Paused** | `#FFC107` | `#FFB300` | `focus_mode.timer.paused` |
| **Timer Completed** | `#4CAF50` | `#4CAF50` | `focus_mode.timer.completed` |
| **Timer Text** | `#FFFFFF` | `#FFFFFF` | `focus_mode.timer.text` |
| **Task Title** | `#FFFFFF` | `#FFFFFF` | `focus_mode.task.title` |
| **Task Subtitle** | `#CACACA` | `#CACACA` | `focus_mode.task.subtitle` |
| **Task Background** | `rgba(255,255,255,0.1)` | `rgba(0,0,0,0.2)` | `focus_mode.task.background` |
| **Controls Start** | `#4CAF50` | `#4CAF50` | `focus_mode.controls.start` |
| **Controls Pause** | `#FFC107` | `#FFB300` | `focus_mode.controls.pause` |
| **Controls Stop** | `#F44336` | `#F44336` | `focus_mode.controls.stop` |
| **Progress Ring** | `#FF9800` | `#FF8C00` | `focus_mode.progress.ring` |
| **Progress Background** | `rgba(255,152,0,0.2)` | `rgba(255,140,0,0.2)` | `focus_mode.progress.background` |
| **Statistics Background** | `rgba(255,255,255,0.1)` | `rgba(0,0,0,0.2)` | `focus_mode.statistics.background` |
| **Statistics Text** | `#CACACA` | `#CACACA` | `focus_mode.statistics.text` |
| **Statistics Progress** | `#FF9800` | `#FF8C00` | `focus_mode.statistics.progress` |
| **Distraction Blocked** | `#FF9800` | `#FF8C00` | `focus_mode.distraction_blocked.indicator` |

---

### Типографика

| Элемент | Стиль | Token |
|---------|-------|-------|
| **Timer Digits** | 64sp, Bold | Custom |
| **Timer Label** | 14sp, Medium | `typography.body.medium` |
| **Task Title** | 20sp, Semi-Bold | `typography.title.medium` |
| **Task Subtitle** | 14sp, Regular | `typography.body.medium` |
| **Control Labels** | 12sp, Medium | `typography.label.medium` |
| **Statistics** | 12sp, Regular | `typography.body.small` |

---

### Отступы и размеры

| Элемент | Размер | Token |
|---------|--------|-------|
| **Timer Diameter** | 200dp | Custom |
| **Timer Digit Size** | 64sp | Custom |
| **Progress Ring Width** | 4dp | Custom |
| **Task Display Width** | 80% | Custom |
| **Task Display Max Width** | 400dp | Custom |
| **Task Display Padding** | 24dp | `spacing.lg_2` |
| **Task Display Border Radius** | 12dp | `border_radius.md` |
| **Control Bar Height** | 48dp | `spacing.xxl_2` |
| **Control Button Size** | 48dp x 48dp | Custom |
| **Control Button Spacing** | 16dp | `spacing.md` |
| **Control Icon Size** | 24dp | `spacing.lg_2` |
| **Next Tasks Height** | 64dp | Custom |
| **Next Tasks Padding** | 16dp | `spacing.md` |
| **Goal Progress Height** | 40dp | `spacing.xxl_2` |
| **Goal Progress Padding** | 16dp | `spacing.md` |
| **Progress Bar Height** | 8dp | Custom |

---

### Border Radius

| Элемент | Radius | Token |
|---------|--------|-------|
| **Task Display** | 12dp | `border_radius.md` |
| **Control Buttons** | 12dp | `border_radius.md` |
| **Next Tasks** | 8dp | `border_radius.sm_2` |
| **Goal Progress** | 8dp | `border_radius.sm_2` |
| **Progress Ring** | 9999dp (circle) | `border_radius.full` |

---

### Тени

| Элемент | Shadow | Token |
|---------|--------|-------|
| **Task Display** | Level 2 | `shadows.level_2` |
| **Control Buttons** | Level 1 | `shadows.level_1` |
| **Next Tasks** | Level 1 | `shadows.level_1` |
| **Dialogs** | Level 5 | `shadows.level_5` |

---

## 📐 Спецификации

### Размеры и отступы

#### Timer Area
| Свойство | Значение | Token |
|----------|----------|-------|
| Diameter | 200dp | Custom |
| Digit Size | 64sp | Custom |
| Digit Weight | Bold | - |
| Digit Color | Timer Text | `focus_mode.timer.text` |
| Progress Ring Width | 4dp | Custom |
| Progress Ring Color | Timer Active | `focus_mode.timer.active` |
| Progress Ring Background | Timer Active (20%) | Custom |
| Label Size | 14sp | `typography.body.medium` |
| Label Color | Timer Text | `focus_mode.timer.text` |

#### Task Display
| Свойство | Значение | Token |
|----------|----------|-------|
| Width | 80% | Custom |
| Max Width | 400dp | Custom |
| Padding | 24dp | `spacing.lg_2` |
| Background | Task Background | `focus_mode.task.background` |
| Border Radius | 12dp | `border_radius.md` |
| Title Size | 20sp | `typography.title.medium` |
| Title Color | Task Title | `focus_mode.task.title` |
| Subtitle Size | 14sp | `typography.body.medium` |
| Subtitle Color | Task Subtitle | `focus_mode.task.subtitle` |

#### Control Bar
| Свойство | Значение | Token |
|----------|----------|-------|
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16dp | `spacing.md` |
| Button Size | 48dp x 48dp | Custom |
| Button Spacing | 16dp | `spacing.md` |
| Icon Size | 24dp | `spacing.lg_2` |
| Button Border Radius | 12dp | `border_radius.md` |

---

### Состояния

#### Timer States

| Состояние | Цвет цифр | Кольцо прогресса | Иконка | Кнопки |
|-----------|-----------|------------------|--------|---------|
| **Active (Work)** | White | Orange (движется) | ⏱️ | Pause visible |
| **Paused** | White | Orange (заморожено) | ⏸️ | Resume visible |
| **Completed** | White | Orange (полное) | ✅ | Next visible |
| **Short Break** | White | Blue (движется) | ☕ | Skip visible |
| **Long Break** | White | Purple (движется) | 🛌 | Skip visible |

#### Task Display States

| Состояние | Фон | Текст | Элевация |
|-----------|-----|-------|-----------|
| **Default** | Task Background | Normal | Level 2 |
| **Hover** | Task Background (lighter) | Normal | Level 3 |
| **Selected** | Task Background (lighter) | Normal | Level 3 |

#### Control Button States

| Кнопка | Состояние | Цвет | Иконка |
|--------|-----------|------|--------|
| **Start** | Default | Green | ▶️ |
| **Start** | Hover | Green (lighter) | ▶️ |
| **Start** | Pressed | Green (darker) | ▶️ |
| **Pause** | Default | Amber | ⏸️ |
| **Pause** | Hover | Amber (lighter) | ⏸️ |
| **Pause** | Pressed | Amber (darker) | ⏸️ |
| **Stop** | Default | Red | ⏹️ |
| **Stop** | Hover | Red (lighter) | ⏹️ |
| **Stop** | Pressed | Red (darker) | ⏹️ |

---

### Анимации

| Анимация | Длительность | Easing | Триггер |
|----------|-------------|--------|---------|
| **Timer Countdown** | 1000ms | Linear | Every second |
| **Progress Ring** | 1000ms | Linear | Continuous |
| **Task Fade In** | 300ms | Ease-out | Task change |
| **Control Button Press** | 150ms | Ease-out | Button tap |
| **Focus Mode Enter** | 250ms | Ease-in-out | Activation |
| **Focus Mode Exit** | 200ms | Ease-in | Deactivation |
| **Session Complete** | 500ms | Ease-out | Timer reaches 0 |
| **Break Start** | 300ms | Ease-out | Auto-transition |

---

## 🔗 Зависимости

### От Product Manager
- ✅ Требования к Focus Mode (из TZ.md)
- ✅ Приоритеты фич

### От Architect
- ⏳ API контракты для Focus Mode (**AR-???**)
- ⏳ Интеграция с Pomodoro

### От Backend
- ⏳ Sync Engine для синхронизации сессий (**BE-001**)
- ⏳ Statistics API для статистики (**BE-???**)

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
- [ ] Минимальное количество отвлекающих элементов

### Прототип
- [ ] Прототип тестирован на основных сценариях
- [ ] Таймер работает корректно
- [ ] Анимации плавные
- [ ] Переключение между состояниями работает
- [ ] Блокировка отвлечений работает (если реализована)

### Документация
- [ ] Design spec завершен
- [ ] Все компоненты документированы
- [ ] Взаимодействия документированы
- [ ] Токены обновлены в `shared/design-tokens/`

### Интеграция
- [ ] Токены интегрированы в Frontend (**FE-001**)
- [ ] API контракты готовы
- [ ] Backend поддерживает синхронизацию

---

## 📁 Файлы

### Design Files (Figma)
- **Main File:** `ui-design/screens/focus-mode.fig` (планируется)
- **Components:** `ui-design/components/focus-components.fig` (планируется)
- **Prototype:** `prototypes/interactive/focus-prototype.fig` (планируется)

### Documentation Files
- **Design Spec:** `output/design-specs/focus-mode-spec.md` (этот файл)
- **Wireframes:** `wireframes/low-fidelity/focus-mode-wireframe.md`
- **Research:** `research/competitive-ui-analysis/focus-mode-ui-analysis.md`

### Shared Files
- **UI Kit Update:** `output/shared/ui-kit/` (планируется обновление)

---

## 📅 Сроки

| Этап | Срок | Статус |
|------|------|--------|
| **Исследование** | 2026-09-05 | ✅ Готово |
| **Wireframes** | 2026-09-05 | ✅ Готово |
| **High-Fidelity Design** | 2026-09-10 | 🟡 В процессе |
| **Prototype** | 2026-09-12 | ⚪ Планируется |
| **Design Spec Final** | 2026-09-13 | ⚪ Планируется |
| **Frontend Integration** | 2026-09-20 | ⚪ Ожидает FE-014 |
| **Testing & Iteration** | 2026-09-25 | ⚪ Планируется |

---

## 🎯 Следующие шаги

### Для меня (Designer):
1. [ ] Завершить high-fidelity дизайн Focus Mode
2. [ ] Создать интерактивный прототип
3. [ ] Написать final design spec
4. [ ] Отдать на ревью @frontend и @product-manager

### Для Frontend:
1. [ ] Дождаться final design spec
2. [ ] Реализовать FocusModeScreen по спецификации
3. [ ] Интегрировать таймер
4. [ ] Интегрировать Pomodoro
5. [ ] Интегрировать блокировку отвлечений (опционально)

### Для Architect:
1. [ ] Дождаться final design spec
2. [ ] Разработать API контракты для Focus Mode

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

- [Focus Mode UI Analysis](../../research/competitive-ui-analysis/focus-mode-ui-analysis.md)
- [Focus Mode Wireframes](../../wireframes/low-fidelity/focus-mode-wireframe.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Timeline Spec](../timeline-spec.md)
- [Gantt Spec](../gantt-spec.md)
- [TZ.md](../../../TZ.md)
- [NEXT_SESSION_TASK.md](../../../NEXT_SESSION_TASK.md)

---

*Документ подготовлен для задачи DE-005*
*Последнее обновление: 2026-09-05*
*Статус: Draft (в процессе)*
