# 🗃️ Backend Agent Backlog
# **WOLFTASK / Floktask**
# Last Updated: 2026-09-05
# Assigned to: @backend

---

## 🔴 **P0: Критично (Инфраструктура)**

### **Немедленно (Срок: 2026-09-06 - 2026-09-11)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **BE-001** | **Sync Engine** | Реализация offline-first синхронизации, конфликты, queue | ⚪ To Do | ❌ Нет | `SyncManager.kt`, `ConflictResolver.kt` |
| **BE-002** | **Backup API** | JSON экспорт/импорт для всех сущностей | ⚪ To Do | ❌ Нет | `BackupApi.kt`, `BackupManager.kt` |

---

## 🟡 **P1: Высокий (API и сервисы)**

### **Для новых фич (Срок: после соответствующих AR задач)**
| ID | Задача | Описание | Статус | Зависимости | Выход |
|----|--------|----------|--------|-------------|-------|
| **BE-004** | **Analytics API** | Данные для финансовых графиков и статистики | ⚪ To Do | AR-001? | `AnalyticsApi.kt`, `AnalyticsService.kt` |
| **BE-003** | **AI Service Интеграция** | Подключение LLM для подсказок и генерации | ⚪ To Do | AR-003 | `AIService.kt`, `AIApi.kt` |

---

## 🟢 **P2: Средний (Улучшения)**

| ID | Задача | Описание | Статус | Срок |
|----|--------|----------|--------|------|
| **BE-005** | **Time Blocking API** | API для работы с временными блоками | ⚪ To Do | 2026-10-01 |
| **BE-006** | **Kanban API** | API для Канбан доски | ⚪ To Do | 2026-10-05 |
| **BE-007** | **Gamification API** | API для геймификации (badges, achievements) | ⚪ To Do | 2026-10-10 |
| **BE-008** | **Search API** | Улучшенный поиск по всем сущностям | ⚪ To Do | 2026-10-15 |

---

## 📅 **ПЛАН НА 2 НЕДЕЛИ**

### **Неделя 1 (2026-09-05 — 2026-09-11)**
- [ ] **BE-001**: Sync Engine
- [ ] **BE-002**: Backup API
- [ ] Координироваться с @architect по AR-004, AR-005

### **Неделя 2 (2026-09-12 — 2026-09-18)**
- [ ] **BE-004**: Analytics API (если AR-001 готово)
- [ ] Тестирование Sync Engine и Backup API
- [ ] Документация API

---

## 🎯 **ТЕХНИЧЕСКИЕ ТРЕБОВАНИЯ**

### **BE-001: Sync Engine**

**Требования:**
```kotlin
// Требуется реализовать:
- Offline-first подход
- Автоматическая синхронизация при возврате онлайн
- Ручная синхронизация по запросу
- Queue для изменений в оффлайн режиме
- Разрешение конфликтов (last write wins + custom rules)
- Уведомления о конфликтах
```

**Класс SyncManager:**
```kotlin
class SyncManager(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val conflictResolver: ConflictResolver,
    private val connectivityManager: ConnectivityManager
) {
    suspend fun syncAll()
    suspend fun syncEntity(entityType: EntityType)
    suspend fun resolveConflict(conflict: SyncConflict): ConflictResolution
    fun observeSyncStatus(): Flow<SyncStatus>
}
```

**Entity Types:**
- Tasks
- Projects
- Notes
- Finance (Transactions, Budgets, Goals)
- Habits
- Tags
- Categories

### **BE-002: Backup API**

**Требования:**
```kotlin
// Требуется реализовать:
- Экспорт всех данных в JSON
- Импорт данных из JSON
- Валидация данных при импорте
- Резервное копирование в облако (опционально)
- Локальное резервное копирование
- Восстановление из резервной копии
```

**Класс BackupManager:**
```kotlin
class BackupManager(
    private val jsonSerializer: JsonSerializer,
    private val fileManager: FileManager,
    private val cloudStorage: CloudStorage? = null
) {
    suspend fun exportToJson(): String
    suspend fun importFromJson(json: String): BackupResult
    suspend fun backupToFile(path: String): BackupResult
    suspend fun restoreFromFile(path: String): BackupResult
    suspend fun backupToCloud(): BackupResult
    suspend fun restoreFromCloud(): BackupResult
}
```

**Формат JSON:**
```json
{
  "version": "1.0",
  "exportedAt": "2026-09-05T12:00:00Z",
  "appVersion": "0.1.0",
  "data": {
    "tasks": [...],
    "projects": [...],
    "notes": [...],
    "finance": {
      "transactions": [...],
      "budgets": [...],
      "goals": [...]
    },
    "habits": [...],
    "tags": [...],
    "categories": [...]
  }
}
```

---

## 📁 **РЕСУРСЫ**

### **Существующий код**
- 📁 `app/data/` - Текущие репозитории и DAO
- 📁 `app/domain/` - Доменные модели и use cases
- 📁 `app/data/local/` - Room базы данных
- 📁 `app/data/remote/` - Сетевые запросы (если есть)

### **Техническая документация**
- 📄 [TZ.md](../TZ.md) - Техническое задание
- 📄 [NEXT_SESSION_TASK.md](../NEXT_SESSION_TASK.md) - Текущие задачи

### **Design Ресурсы**
- 📁 `shared/design-tokens/` - Токены дизайна
- 📁 `agents/designer/design-system/` - Дизайн-система

---

## ✅ **КРИТЕРИИ ПРИЕМКИ**

### Для всех задач:
- [ ] Код соответствует Clean Architecture
- [ ] Тесты покрывают >= 80%
- [ ] Обработка ошибок реализована
- [ ] Документация API обновлена
- [ ] Проходит CI проверки

### Для Sync Engine:
- [ ] Работает в оффлайн режиме
- [ ] Синхронизируется при возврате онлайн
- [ ] Разрешает конфликты
- [ ] Уведомляет о статусе синхронизации
- [ ] Оптимизировано по потреблению батареи

### Для Backup API:
- [ ] Экспортирует все данные
- [ ] Импортирует данные корректно
- [ ] Валидирует данные
- [ ] Работает с большими объемами данных
- [ ] Восстанавливает данные без потерь

---

## 🔗 **СВЯЗАННЫЕ ЗАДАЧИ**

### **Designer**
- [DE-002](DE-002) ✅ Design System v1
- [DE-003](DE-003) 🟡 Timeline View UI
- [DE-004](DE-004) ⚪ Gantt Charts UI

### **Architect**
- [AR-001](AR-001) ⚪ Timeline API
- [AR-002](AR-002) ⚪ Gantt API
- [AR-003](AR-003) ⚪ AI API
- [AR-004](AR-004) ⚪ Offline-First
- [AR-005](AR-005) ⚪ Modularization

### **Frontend**
- [FE-001](FE-001) ⚪ Token Integration
- [FE-005](FE-005) ⚪ Transaction Edit
- [FE-006](FE-006) ⚪ Budgets Module
- [FE-012](FE-012) ⚪ Timeline View

---

## 📞 **КОММУНИКАЦИЯ**

### **Ежедневно:**
- Проверить новые задачи в этом файле
- Обновить статус своих задач
- Закоммитить изменения в `feature/backend-[task-id]`

### **По завершении задачи:**
1. Закоммитить код
2. Запушить в ветку
3. Открыть PR с ссылкой на задачу
4. Упомянуть @architect и @frontend для ревью
5. Обновить статус задачи

---

## 🚀 **СЛЕДУЮЩИЕ ШАГИ**

1. **Начать с BE-001** (Sync Engine) — **не зависит от других!**
2. **Параллельно работать** над BE-002 (Backup API)
3. **Готовиться к BE-003** (AI Service) — ждать AR-003
4. **Координироваться с @architect** по API контрактам

---

**Вопросы?** Создай Issue с label: `backend` или напиши @backend

---

*Последнее обновление: 2026-09-05*
*Ответственный: @backend*
