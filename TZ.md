# WOLFTASK - Оптимизированное Техническое Задание для AI Code

**Версия**: 3.0 | **Дата**: 16 августа 2026 | **Репозиторий**: https://github.com/dondgoklo-cyber/Floktask
**Ветка**: vibe/taskmanager-scaffold-8c6512

---

## КРАТКОЕ ОПИСАНИЕ

WOLFTASK (ранее TaskManager) — это Personal Life OS для Android с функционалом:
- Задачи (приоритеты, статусы, дедлайны, теги, подзадачи, повтор)
- Проекты (цвета, иконки, архивирование)
- Календарь (5 режимов + Time Blocking)
- Привычки (streak, частота, прогресс)
- Pomodoro (таймер, статистика)
- Матрица Эйзенхауэра (4 квадранта, drag and drop)
- Финансы (транзакции, категории, счета, мультивалютность)
- Заметки (Markdown, папки, экспорт/импорт)
- Геймификация (уровни, очки, достижения)
- Голосовой ввод (rule-based, без AI)

Технологии: Kotlin 1.9.22, Jetpack Compose, Room 2.6.1 (v10), Hilt 2.48, Clean Architecture + MVVM

---

## ИНСТРУКЦИЯ ДЛЯ AI CODE

### Принципы работы:
1. Offline-First
2. Без AI
3. Clean Code
4. Тесты (покрытие >=80%)
5. Документация

### Как работать:
1. Читайте NEXT_SESSION_TASK.md
2. Читайте это ТЗ
3. Следуйте чек-листам
4. Тестируйте изменения

---

## ТЕКУЩИЙ СТАТУС

### Уже реализовано:
- Модуль 14: Notes & Knowledge Base
- Модуль 15: Haptic Feedback
- Модуль 16: Multi-currency Finance
- Модуль 17: Premium Visual System (частично)
- Модуль 18: Voice Task Creation
- Модуль 19: Russian Language

### Текущие проблемы:
- БД v10 (нужно мигрировать)
- Теги хранятся как JSON
- Нет редактирования транзакций
- Бюджеты не реализованы
- Финансовые цели не реализованы
- Dashboard нужно переработать

---

## ПРИОРИТЕТЫ

### ПРИОРИТЕТ 1: MVP

#### 1.1 Рефакторинг тегов
Проблема: Теги хранятся как JSON -> неэффективный поиск
Решение: Заменить на Many-to-Many связь

Чек-лист:
- [x] Создать TaskTagEntity ✅
- [x] TaskEntity обновлён (JSON сохранён для совместимости) ✅
- [x] Создать TaskTagDao ✅
- [x] Миграция v12 -> v13 (fallbackToDestructiveMigration) ✅
- [x] Обновить TaskRepository ✅
- [x] CI Build & Test pass ✅

#### 1.2 Редактирование транзакций
Проблема: Только создание/удаление
Решение: Добавить метод update

Чек-лист:
- [x] Добавить update в TransactionDao ✅
- [x] Обновить TransactionRepository ✅
- [x] Обновить FinanceViewModel ✅
- [x] Обновить AddTransactionSheet (editingTransaction) ✅

#### 1.3 Бюджеты
Чек-лист:
- [x] Создать BudgetEntity/BudgetDao ✅
- [x] Создать BudgetRepository ✅
- [x] Обновить FinanceViewModel ✅
- [x] Добавить UI (BudgetCard) ✅

#### 1.4 Финансовые цели
Чек-лист:
- [x] Создать GoalEntity/GoalDao ✅
- [x] Создать GoalRepository ✅
- [x] Обновить FinanceViewModel ✅
- [x] Добавить UI (BudgetCard) ✅

#### 1.5 Переработка Dashboard
Структура: Приветствие -> Прогресс -> Финансы -> Заметки -> Задачи -> Привычки

Чек-лист:
- [x] TodayViewModel обновлён (inbox, finance, notes) ✅
- [x] TodayScreen переработан ✅
- [x] Progress → Inbox → Finance → Notes → NextTasks → Habits ✅

#### 1.6 Улучшение BackupManager
Чек-лист:
- [ ] Добавить шифрование
- [ ] Добавить проверку целостности
- [x] CI Build & Test pass ✅

---

### ПРИОРИТЕТ 2: Расширенные фичи

#### 2.1 Time Blocking
#### 2.2 Естественный язык
#### 2.3 Kanban
#### 2.4 Интеграция с календарями
#### 2.5 Графики
#### 2.6 Шаблоны задач
#### 2.7 Улучшенный голосовой ввод
#### 2.8 Geofencing
#### 2.9 Импорт Markdown

---

### ПРИОРИТЕТ 3: Дизайн и UX

#### 3.1 Premium Visual System
#### 3.2 Настраиваемость
#### 3.3 Адаптивность
#### 3.4 Доступность

---

## АРХИТЕКТУРНЫЕ УЛУЧШЕНИЯ

#### 4.1 Разделение на модули
#### 4.2 Оптимизация Subtask
#### 4.3 Unit-тесты
#### 4.4 Кэширование

---

## РЕКОМЕНДУЕМОЕ МЕСТО

Floktask/
├── TZ.md          # <- ОПТИМАЛЬНО
├── NEXT_SESSION_TASK.md
├── README.md
└── app/

---

## ИНСТРУКЦИЯ

1. Клонируйте репозиторий: git clone https://github.com/dondgoklo-cyber/Floktask.git
2. Скопируйте этот файл как TZ.md в корень
3. git add TZ.md && git commit -m "docs: Add TZ for AI Code"
4. git push origin vibe/taskmanager-scaffold-8c6512

---

## СТАТУС

Все задачи: ⏳ В процессе или ⏳ Планируется
Отмечайте выполненные: ✅

---

## КОНТАКТЫ

Репозиторий: https://github.com/dondgoklo-cyber/Floktask
Ветка: vibe/taskmanager-scaffold-8c6512

---

ГОТОВО! AI Code может работать с этим ТЗ.
