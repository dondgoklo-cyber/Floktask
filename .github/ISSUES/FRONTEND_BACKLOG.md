# 📱 Frontend Agent Backlog
# **WOLFTASK / Floktask**
# Last Updated: 2026-09-05
# Assigned to: @frontend

---

## 🔴 **P0: Критично (Блокируют release)**

### **Немедленно (Срок: 2026-09-06)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **FE-001** | **Интеграция Design Tokens** | Интегрировать токены из `shared/design-tokens/` в кодовую базу | ⚪ To Do | DE-002 ✅ | `Theme.kt`, `Color.kt`, `Typography.kt` |
| **FE-002** | **Theme System (Light/Dark)** | Создать систему тем с поддержкой light/dark mode | ⚪ To Do | FE-001 | `Theme.kt` |
| **FE-003** | **Обновить UI компоненты** | Обновить Button, Card, TextField, Modal по новым токенам | ⚪ To Do | FE-002 | `Button.kt`, `Card.kt`, `TextField.kt` |

### **Высокий приоритет (Срок: 2026-09-08 - 2026-09-22)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **FE-005** | **Transaction Edit** | Редактирование транзакций (AddTransactionSheet → edit mode) | ⚪ To Do | ❌ Нет | `AddTransactionSheet.kt` |
| **FE-006** | **Budgets Module** | BudgetEntity, BudgetDao, UI в FinanceScreen | ⚪ To Do | ❌ Нет | `BudgetEntity.kt`, `FinanceScreen.kt` |
| **FE-007** | **Financial Goals** | GoalEntity, progress bars, UI в FinanceScreen | ⚪ To Do | ❌ Нет | `GoalEntity.kt`, `FinanceScreen.kt` |
| **FE-008** | **Finance Analytics** | Bar charts, pie charts (Compose Canvas) | ⚪ To Do | ❌ Нет | `FinanceAnalytics.kt` |
| **FE-009** | **Notes Markdown Import** | `NoteExportManager.importFromMarkdown()` | ⚪ To Do | ❌ Нет | `NoteExportManager.kt` |
| **FE-010** | **Dashboard Redesign** | Today → Progress → Finance → Notes → Next Tasks → Habits | ⚪ To Do | ❌ Нет | `DashboardScreen.kt` |
| **FE-011** | **Today/Habits Separation** | Разделение экранов Today и Habits | ⚪ To Do | ❌ Нет | `TodayScreen.kt`, `HabitsScreen.kt` |

---

## 🟡 **P1: Высокий (Новые фичи)**

### **Зависимые от Design (Срок: после DE-003, DE-004, DE-005)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **FE-012** | **Timeline View (Compose)** | Реализация Timeline View по спецификации DE-003 | ⚪ To Do | DE-003 | `TimelineScreen.kt` |
| **FE-013** | **Gantt Charts (Canvas)** | Диаграммы Гантта с drag-and-drop | ⚪ To Do | DE-004 | `GanttScreen.kt` |
| **FE-014** | **Focus Mode Screen** | Полноэкранный режим фокуса с таймером | ⚪ To Do | DE-005 | `FocusModeScreen.kt` |

---

## 🟢 **P2: Средний (Улучшения)**

| ID | Задача | Описание | Статус | Срок |
|----|--------|----------|--------|------|
| **FE-015** | **Time Blocking** | Календарь с блокировкой времени | ⚪ To Do | 2026-10-01 |
| **FE-016** | **Канбан доска** | Drag-and-drop, column management | ⚪ To Do | 2026-10-05 |
| **FE-017** | **Геймификация** | Badges, achievements, progress tracking | ⚪ To Do | 2026-10-10 |
| **FE-018** | **AI Интеграция** | API клиент для AI ассистента | ⚪ To Do | 2026-10-15 |
| **FE-019** | **Eisenhower Matrix** | Улучшение текущей матрицы | ⚪ To Do | 2026-10-20 |
| **FE-020** | **Pomodoro UI** | Circular progress, control buttons | ⚪ To Do | 2026-10-25 |

---

## 📅 **ПЛАН НА 2 НЕДЕЛИ**

### **Неделя 1 (2026-09-05 — 2026-09-11)**
- [ ] **FE-001**: Интеграция Design Tokens
- [ ] **FE-002**: Theme System (Light/Dark)
- [ ] **FE-003**: Обновить UI компоненты
- [ ] **FE-005**: Transaction Edit
- [ ] **FE-006**: Budgets Module

### **Неделя 2 (2026-09-12 — 2026-09-18)**
- [ ] **FE-007**: Financial Goals
- [ ] **FE-008**: Finance Analytics
- [ ] **FE-009**: Notes Markdown Import
- [ ] **FE-010**: Dashboard Redesign
- [ ] **FE-011**: Today/Habits Separation

---

## 🎯 **ТЕХНИЧЕСКИЕ ТРЕБОВАНИЯ**

### **FE-001: Интеграция Design Tokens**
```kotlin
// Требуется создать:
- Color.kt (из colors.yaml)
- Typography.kt (из typography.yaml)
- Dimensions.kt (из spacing.yaml)
- Shadows.kt (из shadows.yaml)
- BorderRadius.kt (из border-radius.yaml)
```

### **FE-002: Theme System**
```kotlin
// Требуется:
- Поддержка light/dark mode
- Динамическое переключение тем
- Сохранение предпочтений пользователя
- Интеграция с MaterialTheme
```

### **FE-005: Transaction Edit**
```kotlin
// Требуется:
- Режим редактирования в AddTransactionSheet
- Предзаполнение полей при редактировании
- Сохранение изменений в базу
- Валидация полей
```

---

## 📁 **РЕСУРСЫ**

### **Design Tokens**
- 📁 `shared/design-tokens/colors.yaml`
- 📁 `shared/design-tokens/typography.yaml`
- 📁 `shared/design-tokens/spacing.yaml`
- 📁 `shared/design-tokens/shadows.yaml`
- 📁 `shared/design-tokens/border-radius.yaml`

### **Документация**
- 📄 [Design System Components](agents/designer/design-system/components/)
- 📄 [Design Guidelines](agents/designer/design-system/guidelines/)
- 📄 [TickTick Analysis](agents/designer/research/competitive-ui-analysis/ticktick-ui-analysis.md)

---

## ✅ **КРИТЕРИИ ПРИЕМКИ**

### Для всех задач:
- [ ] Код соответствует Clean Architecture
- [ ] Тесты покрывают >= 80%
- [ ] UI соответствует дизайн-спецификациям
- [ ] Работает на всех размерах экранов
- [ ] Соответствует гайдлайнам доступности
- [ ] Проходит CI проверки

### Для новых фич:
- [ ] Интеграция с существующими данными
- [ ] Обработка ошибок
- [ ] Состояния загрузки
- [ ] Пустые состояния
- [ ] Документация

---

## 🔗 **СВЯЗАННЫЕ ZАДАЧИ**

### **Designer**
- [DE-002](DE-002) ✅ Design System v1
- [DE-003](DE-003) 🟡 Timeline View UI
- [DE-004](DE-004) ⚪ Gantt Charts UI
- [DE-005](DE-005) ⚪ Focus Mode UI

### **Architect**
- [AR-001](AR-001) ⚪ Timeline API
- [AR-002](AR-002) ⚪ Gantt API
- [AR-003](AR-003) ⚪ AI API
- [AR-004](AR-004) ⚪ Offline-First
- [AR-005](AR-005) ⚪ Modularization

### **Backend**
- [BE-001](BE-001) ⚪ Sync Engine
- [BE-002](BE-002) ⚪ Backup API
- [BE-003](BE-003) ⚪ AI Service
- [BE-004](BE-004) ⚪ Analytics API

---

## 📞 **КОММУНИКАЦИЯ**

### **Ежедневно:**
- Проверить новые задачи в этом файле
- Обновить статус своих задач
- Закоммитить изменения в `feature/frontend-[task-id]`

### **По завершении задачи:**
1. Закоммитить код
2. Запушить в ветку
3. Открыть PR с ссылкой на задачу
4. Упомянуть @designer для ревью UI
5. Обновить статус задачи

---

## 🚀 **СЛЕДУЮЩИЕ ШАГИ**

1. **Начать с FE-001** (Интеграция токенов)
2. **Параллельно работать** над FE-005-FE-011 (независимые задачи)
3. **Ждать DE-003** для FE-012 (Timeline View)
4. **Координироваться с @architect** по API контрактам

---

**Вопросы?** Создай Issue с label: `frontend` или напиши @designer

---

*Последнее обновление: 2026-09-05*
*Ответственный: @frontend*
