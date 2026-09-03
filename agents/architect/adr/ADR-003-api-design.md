# ADR-003: API Дизайн для Floktask

## 📅 Методология
- **Статус:** Accepted
- **Дата:** 2026-09-08
- **Автор:** Architect
- **Участники:** Product Manager, Backend, Frontend

## 🎯 Контекст и проблема

### Контекст
Floktask требует REST API для:
- Синхронизации данных между устройствами
- Интеграции с AI-сервисами
- Поддержки веб-клиента
- Интеграции с сторонними сервисами

API должен быть:
- Простым и интуитивным
- Хорошо документированным
- Версионируемым
- Масштабируемым
- Безопасным

### Проблема
- ❌ Нет API для синхронизации
- ❌ Нет контрактов между клиентом и сервером
- ❌ Нет стандартного способа обрабатывать ошибки
- ❌ Нет механизма версионирования

### Драйверы (что нас мотивирует)
- **Синхронизация** — Multi-device sync
- **Кроссплатформенность** — Поддержка всех клиентов
- **Интеграции** — AI, Calendar, Telegram
- **Масштабируемость** — Поддержка роста системы

## ⚖️ Варианты решения

### Вариант 1: REST API
Классический REST с JSON.

**Плюсы:**
- ✅ Стандартный подход
- ✅ Хорошая поддержка инструментами
- ✅ Простота кэширования
- ✅ Легко тестировать

**Минусы:**
- ❌ Избыточность для простых операций
- ❌ Ограниченные возможности для real-time

**Оценка:**
- **Сложность реализации:** Medium
- **Стоимость:** $$
- **Время:** 2 недели
- **Риск:** Low

### Вариант 2: GraphQL
Гибкий запрос данных.

**Плюсы:**
- ✅ Гибкость запросов
- ✅ Один endpoint для всех операций
- ✅ Хорошо для сложных UI

**Минусы:**
- ❌ Сложность реализации
- ❌ Проблемы с кэшированием
- ❌ Избыточные запросы

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4 недели
- **Риск:** Medium

### Вариант 3: gRPC
Высокопроизводительный RPC.

**Плюсы:**
- ✅ Высокая производительность
- ✅ Компактные данные (Protocol Buffers)
- ✅ Хорошо для микросервисов

**Минусы:**
- ❌ Сложность для веб-клиентов
- ❌ Плохая поддержка в браузерах
- ❌ Сложность отладки

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 3 недели
- **Риск:** Medium

### Вариант 4: Hybrid (REST + WebSockets)
REST для CRUD, WebSockets для real-time.

**Плюсы:**
- ✅ Лучшее из двух миров
- ✅ Поддержка real-time обновлений
- ✅ Стандартный REST для CRUD

**Минусы:**
- ❌ Сложность реализации
- ❌ Два протокола для поддержки

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4 недели
- **Риск:** Medium

## 🎯 Выбранное решение
**Hybrid: REST API + WebSockets**

### Архитектура API

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                       │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Android   │    │     iOS     │    │     Web         │  │
│  └──────┬──────┘    └──────┬──────┘    └───────┬─────────┘  │
└─────────┼─────────────────┼─────────────────┼───────────────┘
          │                 │                 │
          ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  REST API (HTTP/HTTPS)      WebSocket (wss://)           ││
│  │  ┌─────────┐  ┌─────────┐    ┌─────────────────────────┐ ││
│  │  │  /api   │  │  /sync  │    │  /ws/sync               │ ││
│  │  │  /v1/   │  │  /v1/   │    │  /ws/notifications      │ ││
│  │  └─────────┘  └─────────┘    └─────────────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
    ┌─────────────────┬─────────────────┬─────────────────┐
    ▼                 ▼                 ▼                 ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  User        │ │  Task       │ │  Project    │ │  Sync       │
│  Service     │ │  Service    │ │  Service    │ │  Service    │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

### REST API Structure

#### Base URL
```
Production: https://api.floktask.com
Staging: https://staging-api.floktask.com
Development: http://localhost:8080
```

#### Versioning
```
/api/v1/...  # Текущая версия
/api/v2/...  # Будущая версия
```

#### Endpoints

##### Authentication
```
POST   /api/v1/auth/register          # Регистрация
POST   /api/v1/auth/login             # Вход
POST   /api/v1/auth/refresh           # Обновление токена
POST   /api/v1/auth/logout            # Выход
POST   /api/v1/auth/forgot-password   # Восстановление пароля
POST   /api/v1/auth/reset-password    # Сброс пароля
GET    /api/v1/auth/me                # Текущий пользователь
```

##### Users
```
GET    /api/v1/users/{id}             # Получение пользователя
PUT    /api/v1/users/{id}             # Обновление пользователя
DELETE /api/v1/users/{id}             # Удаление пользователя
```

##### Tasks
```
GET    /api/v1/tasks                  # Список задач (с фильтрацией)
POST   /api/v1/tasks                  # Создание задачи
GET    /api/v1/tasks/{id}             # Получение задачи
PUT    /api/v1/tasks/{id}             # Обновление задачи
DELETE /api/v1/tasks/{id}             # Удаление задачи
POST   /api/v1/tasks/{id}/complete    # Отметка выполненной
POST   /api/v1/tasks/{id}/uncomplete  # Снятие отметки
POST   /api/v1/tasks/batch            # Batch операции
```

##### Projects
```
GET    /api/v1/projects               # Список проектов
POST   /api/v1/projects               # Создание проекта
GET    /api/v1/projects/{id}          # Получение проекта
PUT    /api/v1/projects/{id}          # Обновление проекта
DELETE /api/v1/projects/{id}          # Удаление проекта
GET    /api/v1/projects/{id}/tasks    # Задачи проекта
```

##### Sync
```
GET    /api/v1/sync                   # Получение изменений
POST   /api/v1/sync                   # Отправка изменений
GET    /api/v1/sync/status            # Статус синхронизации
POST   /api/v1/sync/resolve           # Разрешение конфликтов
```

##### AI
```
POST   /api/v1/ai/suggest             # Генерация подсказок
POST   /api/v1/ai/summarize           # Суммаризация текста
POST   /api/v1/ai/analyze             # Анализ задач
```

##### Notifications
```
GET    /api/v1/notifications          # Список уведомлений
PUT    /api/v1/notifications/{id}     # Отметка прочитанным
POST   /api/v1/notifications/read-all # Отметка всех прочитанными
```

### WebSocket API

#### Connection
```
wss://api.floktask.com/ws/sync?token={jwt}
```

#### Events

##### Sync Events
```json
// Client → Server: Sync request
{
  "type": "SYNC_REQUEST",
  "data": {
    "since": "2026-09-01T00:00:00Z",
    "deviceId": "abc123"
  }
}

// Server → Client: Sync response
{
  "type": "SYNC_RESPONSE",
  "data": {
    "changes": [...],
    "version": "X",
    "hasMore": false
  }
}

// Client → Server: Send changes
{
  "type": "CHANGES_SEND",
  "data": {
    "changes": [...],
    "version": "Y"
  }
}

// Server → Client: Changes applied
{
  "type": "CHANGES_APPLIED",
  "data": {
    "applied": [...],
    "conflicts": [...]
  }
}
```

##### Notification Events
```json
// Server → Client: New notification
{
  "type": "NOTIFICATION",
  "data": {
    "id": 123,
    "title": "Напоминание",
    "message": "Задача через 30 минут",
    "taskId": 456
  }
}

// Server → Client: Reminder
{
  "type": "REMINDER",
  "data": {
    "taskId": 456,
    "dueDate": "2026-09-01T10:00:00Z"
  }
}
```

### Request/Response Format

#### Request Headers
```
Content-Type: application/json
Authorization: Bearer {jwt_token}
X-Device-Id: {device_id}
X-App-Version: {app_version}
X-Platform: android/ios/web
Accept-Language: ru-RU/en-US
```

#### Response Format

**Success (200/201):**
```json
{
  "data": {...},
  "meta": {
    "version": "1.0",
    "timestamp": "2026-09-01T10:00:00Z",
    "requestId": "abc123"
  }
}
```

**Error (4xx/5xx):**
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request body",
    "details": {
      "field": "title",
      "error": "Title is required"
    }
  },
  "meta": {
    "version": "1.0",
    "timestamp": "2026-09-01T10:00:00Z",
    "requestId": "abc123"
  }
}
```

### Error Codes

| Код | HTTP Status | Описание |
|-----|-------------|----------|
| `VALIDATION_ERROR` | 400 | Ошибка валидации |
| `UNAUTHORIZED` | 401 | Не авторизован |
| `FORBIDDEN` | 403 | Нет прав |
| `NOT_FOUND` | 404 | Ресурс не найден |
| `CONFLICT` | 409 | Конфликт данных |
| `RATE_LIMITED` | 429 | Превышен лимит запросов |
| `INTERNAL_ERROR` | 500 | Внутренняя ошибка |
| `SERVICE_UNAVAILABLE` | 503 | Сервис недоступен |

### Rate Limiting

- **100 запросов/минуту** для аутентифицированных пользователей
- **20 запросов/минуту** для неаутентифицированных
- **Заголовки:**
  - `X-RateLimit-Limit` — Лимит
  - `X-RateLimit-Remaining` — Осталось
  - `X-RateLimit-Reset` — Время сброса

### Pagination

```json
GET /api/v1/tasks?page=0&pageSize=20

Response:
{
  "data": {
    "tasks": [...],
    "pagination": {
      "page": 0,
      "pageSize": 20,
      "total": 100,
      "totalPages": 5
    }
  }
}
```

### Filtering

```
GET /api/v1/tasks?status=todo&priority=high&dueDateFrom=2026-09-01&dueDateTo=2026-09-30
```

### Sorting

```
GET /api/v1/tasks?sortBy=dueDate&sortOrder=asc
```

## 📊 Последствия

### Положительные
- ✅ Стандартный и предсказуемый API
- ✅ Хорошая документация (OpenAPI)
- ✅ Поддержка всех клиентов
- ✅ Масштабируемость
- ✅ Безопасность

### Отрицательные
- ⚠️ Сложность реализации
- ⚠️ Необходимость поддержки нескольких версий
- ⚠️ Затраты на документацию

### Нейтральные
- 🔹 Необходимость тестирования всех endpoints
- 🔹 Мониторинг API

## 🔗 Связанные решения
- [ADR-001: Микросервисная архитектура](ADR-001-microservices.md) — API Gateway
- [ADR-002: Offline-first подход](ADR-002-offline-first.md) — Sync API
- [ADR-005: Система аутентификации](ADR-005-auth-system.md) — JWT

## 📝 Примечания
- **OpenAPI Specification** — Полная спецификация в формате OpenAPI 3.0
- **SDK Generation** — Автоматическая генерация SDK для клиентов
- **API Documentation** — Интерактивная документация (Swagger UI)
- **Versioning** — Поддержка нескольких версий API
- **Deprecation** — Постепенное удаление старых версий

## ✅ Статус
- [ ] Proposed
- [ ] Under Discussion
- [x] Accepted
- [ ] Deprecated
- [ ] Replaced by [ADR-ZZZ]
