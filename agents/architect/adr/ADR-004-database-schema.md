# ADR-004: Схема Базы Данных для Floktask

## 📅 Методология
- **Статус:** Accepted
- **Дата:** 2026-09-10
- **Автор:** Architect
- **Участники:** Backend, Database Specialist

## 🎯 Контекст и проблема

### Контекст
Floktask требует надежной и масштабируемой базы данных для:
- Хранения задач, проектов, пользователей
- Поддержки offline-first синхронизации
- Обработки больших объемов данных
- Обеспечения высокой производительности

Текущая реализация использует Room для локального хранения на Android.

### Проблема
- ❌ Нет серверной базы данных
- ❌ Нет схемы для синхронизации
- ❌ Нет индексов для оптимизации запросов
- ❌ Нет механизма миграций

### Драйверы (что нас мотивирует)
- **Масштабируемость** — Поддержка миллионов пользователей
- **Производительность** — Быстрые запросы
- **Надежность** — Нет потери данных
- **Синхронизация** — Поддержка offline-first

## ⚖️ Варианты решения

### Вариант 1: PostgreSQL
Реляционная БД с поддержкой JSON.

**Плюсы:**
- ✅ Надежность и зрелость
- ✅ Хорошая производительность
- ✅ Поддержка сложных запросов
- ✅ JSON поддержка
- ✅ Хорошая документация

**Минусы:**
- ❌ Сложность масштабирования
- ❌ Вертикальное масштабирование

**Оценка:**
- **Сложность реализации:** Medium
- **Стоимость:** $$
- **Время:** 1 неделя
- **Риск:** Low

### Вариант 2: MongoDB
NoSQL БД.

**Плюсы:**
- ✅ Горизонтальное масштабирование
- ✅ Гибкая схема
- ✅ Хорошо для JSON данных

**Минусы:**
- ❌ Нет транзакций
- ❌ Сложность сложных запросов
- ❌ Проблемы с реляционными данными

**Оценка:**
- **Сложность реализации:** Medium
- **Стоимость:** $$$
- **Время:** 2 недели
- **Риск:** Medium

### Вариант 3: Cassandra
Распределенная NoSQL БД.

**Плюсы:**
- ✅ Очень высокая масштабируемость
- ✅ Высокая доступность
- ✅ Устойчивость к ошибкам

**Минусы:**
- ❌ Очень сложная
- ❌ Ограниченные возможности запросов
- ❌ Нет JOIN

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$$$
- **Время:** 4 недели
- **Риск:** High

### Вариант 4: Multi-Model (PostgreSQL + Redis)
Комбинация реляционной и key-value БД.

**Плюсы:**
- ✅ Лучшее из двух миров
- ✅ Высокая производительность
- ✅ Гибкость

**Минусы:**
- ❌ Сложность управления
- ❌ Необходимость синхронизации

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$$
- **Время:** 3 недели
- **Риск:** Medium

## 🎯 Выбранное решение
**PostgreSQL + Redis**

### Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                          │
└─────────────────────────────────────────────────────────────┘
                              │
    ┌─────────────────────────────────────────────────────────┐
    │                    PostgreSQL Cluster                     │
    │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
    │  │   Primary    │    │   Replica    │    │   Replica    │  │
    │  │  (Master)    │    │  (Read)     │    │  (Read)     │  │
    │  └─────────────┘    └─────────────┘    └─────────────┘  │
    │                                                       │
    │  ┌─────────────────────────────────────────────────────┐│
    │  │                    Databases                         ││
    │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ││
    │  │  │  User   │  │  Task   │  │ Project │  │  Sync   │  ││
    │  │  │   DB    │  │   DB    │  │    DB   │  │   DB    │  ││
    │  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  ││
    │  └─────────────────────────────────────────────────────┘│
    └─────────────────────────────────────────────────────────┘
                              │
                              ▼
    ┌─────────────────────────────────────────────────────────┐
    │                      Redis Cluster                         │
    │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
    │  │   Cache     │    │   Queue     │    │  Session    │  │
    │  │  (Tasks)    │    │  (Sync)     │    │  (Auth)     │  │
    │  └─────────────┘    └─────────────┘    └─────────────┘  │
    └─────────────────────────────────────────────────────────┘
```

### PostgreSQL Schema

#### Users Database

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    timezone VARCHAR(50) DEFAULT 'UTC',
    language VARCHAR(10) DEFAULT 'ru',
    theme VARCHAR(10) DEFAULT 'system',
    
    -- Settings
    settings JSONB DEFAULT '{}',
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning for sync
    version INTEGER DEFAULT 1,
    last_sync_at TIMESTAMPTZ
);

-- User sessions
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    refresh_token VARCHAR(500) UNIQUE NOT NULL,
    device_id VARCHAR(100),
    device_info JSONB DEFAULT '{}',
    ip_address VARCHAR(50),
    user_agent TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OAuth tokens
CREATE TABLE oauth_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_uuid ON users(uuid);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_token ON user_sessions(token);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);
```

#### Tasks Database

```sql
-- Projects
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    color VARCHAR(20) DEFAULT '#808080',
    icon VARCHAR(50),
    is_archived BOOLEAN DEFAULT FALSE,
    is_favorite BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    
    -- Settings
    settings JSONB DEFAULT '{}',
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning
    version INTEGER DEFAULT 1,
    last_sync_at TIMESTAMPTZ
);

-- Tasks
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    project_id BIGINT REFERENCES projects(id) ON DELETE SET NULL,
    
    -- Content
    title VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'todo' CHECK (
        status IN ('todo', 'in_progress', 'done', 'archived', 'deleted')
    ),
    
    -- Priority
    priority VARCHAR(20) NOT NULL DEFAULT 'none' CHECK (
        priority IN ('none', 'low', 'medium', 'high')
    ),
    
    -- Dates
    due_date TIMESTAMPTZ,
    start_date TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    
    -- Time tracking
    duration_minutes INTEGER,
    actual_duration_minutes INTEGER,
    
    -- Recurrence
    recurrence_rule VARCHAR(100),
    recurrence_exception_dates TIMESTAMPTZ[],
    
    -- Estimates
    estimate_minutes INTEGER,
    
    -- Sorting
    sort_order INTEGER DEFAULT 0,
    
    -- Parent/child relationships
    parent_task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    
    -- Tags (Many-to-Many)
    tags VARCHAR(255)[],
    
    -- Reminders
    reminder_date TIMESTAMPTZ,
    reminder_repeat VARCHAR(20),
    
    -- Attachments
    attachment_count INTEGER DEFAULT 0,
    
    -- Comments
    comment_count INTEGER DEFAULT 0,
    
    -- Subtasks
    subtask_count INTEGER DEFAULT 0,
    completed_subtask_count INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning for sync
    version INTEGER DEFAULT 1,
    last_sync_at TIMESTAMPTZ,
    
    -- Device info (for offline-first)
    created_device_id VARCHAR(100),
    updated_device_id VARCHAR(100)
);

-- Task dependencies
CREATE TABLE task_dependencies (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    depends_on_task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    dependency_type VARCHAR(20) DEFAULT 'finish_to_start' CHECK (
        dependency_type IN ('finish_to_start', 'start_to_start', 'finish_to_finish', 'start_to_finish')
    ),
    lag_minutes INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(task_id, depends_on_task_id)
);

-- Task tags (for more complex tagging)
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20) DEFAULT '#808080',
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, name)
);

-- Task tag mapping
CREATE TABLE task_tags (
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY(task_id, tag_id)
);

-- Task comments
CREATE TABLE task_comments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    is_edited BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Task attachments
CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    thumbnail_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Task history (audit log)
CREATE TABLE task_history (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    device_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Tasks
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_parent_task_id ON tasks(parent_task_id);
CREATE INDEX idx_tasks_uuid ON tasks(uuid);
CREATE INDEX idx_tasks_deleted_at ON tasks(deleted_at);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_tasks_updated_at ON tasks(updated_at);
CREATE INDEX idx_tasks_version ON tasks(version);

CREATE INDEX idx_task_dependencies_task_id ON task_dependencies(task_id);
CREATE INDEX idx_task_dependencies_depends_on ON task_dependencies(depends_on_task_id);

CREATE INDEX idx_task_tags_task_id ON task_tags(task_id);
CREATE INDEX idx_task_tags_tag_id ON task_tags(tag_id);

CREATE INDEX idx_task_comments_task_id ON task_comments(task_id);
CREATE INDEX idx_task_attachments_task_id ON task_attachments(task_id);
CREATE INDEX idx_task_history_task_id ON task_history(task_id);
```

#### Projects Database (Extended)

```sql
-- Project members (for collaboration)
CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'member' CHECK (
        role IN ('owner', 'admin', 'member', 'viewer')
    ),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(project_id, user_id)
);

-- Project invitations
CREATE TABLE project_invitations (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'member' CHECK (
        role IN ('admin', 'member', 'viewer')
    ),
    token VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (
        status IN ('pending', 'accepted', 'rejected', 'expired')
    ),
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Project views (Kanban, List, Calendar, etc.)
CREATE TABLE project_views (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    view_type VARCHAR(20) NOT NULL CHECK (
        view_type IN ('list', 'kanban', 'calendar', 'gantt', 'timeline')
    ),
    name VARCHAR(100) NOT NULL,
    settings JSONB NOT NULL DEFAULT '{}',
    sort_order INTEGER DEFAULT 0,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Kanban columns
CREATE TABLE kanban_columns (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    view_id BIGINT REFERENCES project_views(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20),
    sort_order INTEGER DEFAULT 0,
    task_status VARCHAR(20) REFERENCES tasks(status),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Projects
CREATE INDEX idx_project_members_project_id ON project_members(project_id);
CREATE INDEX idx_project_members_user_id ON project_members(user_id);
CREATE INDEX idx_project_invitations_project_id ON project_invitations(project_id);
CREATE INDEX idx_project_invitations_email ON project_invitations(email);
CREATE INDEX idx_project_invitations_token ON project_invitations(token);
CREATE INDEX idx_project_views_project_id ON project_views(project_id);
CREATE INDEX idx_kanban_columns_project_id ON kanban_columns(project_id);
CREATE INDEX idx_kanban_columns_view_id ON kanban_columns(view_id);
```

#### Sync Database

```sql
-- Sync metadata
CREATE TABLE sync_metadata (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(100) NOT NULL,
    last_sync_timestamp TIMESTAMPTZ NOT NULL,
    last_sync_version INTEGER NOT NULL DEFAULT 1,
    sync_status VARCHAR(20) NOT NULL DEFAULT 'success' CHECK (
        sync_status IN ('success', 'failed', 'in_progress', 'pending')
    ),
    last_error_message TEXT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, device_id)
);

-- Sync queue (for pending changes)
CREATE TABLE sync_queue (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (
        action IN ('create', 'update', 'delete')
    ),
    payload JSONB NOT NULL,
    version INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (
        status IN ('pending', 'syncing', 'synced', 'failed')
    ),
    retry_count INTEGER DEFAULT 0,
    last_error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Sync conflicts
CREATE TABLE sync_conflicts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    local_version INTEGER NOT NULL,
    server_version INTEGER NOT NULL,
    local_data JSONB NOT NULL,
    server_data JSONB NOT NULL,
    conflict_type VARCHAR(20) NOT NULL CHECK (
        conflict_type IN ('version', 'data', 'deleted')
    ),
    resolution_status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (
        resolution_status IN ('pending', 'resolved', 'ignored')
    ),
    resolution_strategy VARCHAR(20),
    resolved_data JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ
);

-- Sync history
CREATE TABLE sync_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(100),
    sync_type VARCHAR(20) NOT NULL CHECK (
        sync_type IN ('full', 'partial', 'manual')
    ),
    changes_sent INTEGER DEFAULT 0,
    changes_received INTEGER DEFAULT 0,
    conflicts_count INTEGER DEFAULT 0,
    duration_ms BIGINT,
    status VARCHAR(20) NOT NULL CHECK (
        status IN ('success', 'failed', 'partial')
    ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Sync
CREATE INDEX idx_sync_metadata_user_device ON sync_metadata(user_id, device_id);
CREATE INDEX idx_sync_queue_user_status ON sync_queue(user_id, status);
CREATE INDEX idx_sync_queue_created_at ON sync_queue(created_at);
CREATE INDEX idx_sync_conflicts_user_status ON sync_conflicts(user_id, resolution_status);
CREATE INDEX idx_sync_history_user ON sync_history(user_id);
```

#### Habits Database

```sql
-- Habits
CREATE TABLE habits (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Frequency
    frequency_type VARCHAR(20) NOT NULL CHECK (
        frequency_type IN ('daily', 'weekly', 'monthly', 'custom')
    ),
    frequency_value INTEGER DEFAULT 1,
    frequency_days INTEGER[] DEFAULT '{}', -- For weekly: [1,3,5] (Mon, Wed, Fri)
    
    -- Target
    target_type VARCHAR(20) NOT NULL DEFAULT 'count' CHECK (
        target_type IN ('count', 'duration', 'binary')
    ),
    target_value INTEGER DEFAULT 1,
    
    -- Time
    time_of_day TIMESTAMPTZ,
    duration_minutes INTEGER,
    
    -- Streak
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    last_completion_date DATE,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_archived BOOLEAN DEFAULT FALSE,
    
    -- Sorting
    sort_order INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning
    version INTEGER DEFAULT 1
);

-- Habit completions
CREATE TABLE habit_completions (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE,
    completion_date DATE NOT NULL,
    count INTEGER DEFAULT 1,
    duration_minutes INTEGER,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(habit_id, completion_date)
);

-- Habit reminders
CREATE TABLE habit_reminders (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE,
    reminder_time TIMESTAMPTZ NOT NULL,
    repeat_days INTEGER[] NOT NULL DEFAULT '{1,2,3,4,5,6,7}',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Habits
CREATE INDEX idx_habits_user_id ON habits(user_id);
CREATE INDEX idx_habits_is_active ON habits(is_active);
CREATE INDEX idx_habit_completions_habit_date ON habit_completions(habit_id, completion_date);
CREATE INDEX idx_habit_reminders_habit_id ON habit_reminders(habit_id);
```

#### Finance Database

```sql
-- Accounts
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL CHECK (
        account_type IN ('cash', 'card', 'bank', 'investment', 'other')
    ),
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'RUB',
    color VARCHAR(20),
    icon VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Categories
CREATE TABLE finance_categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    category_type VARCHAR(20) NOT NULL CHECK (
        category_type IN ('income', 'expense')
    ),
    parent_category_id BIGINT REFERENCES finance_categories(id) ON DELETE CASCADE,
    color VARCHAR(20),
    icon VARCHAR(50),
    sort_order INTEGER DEFAULT 0,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Transactions
CREATE TABLE finance_transactions (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    account_id BIGINT REFERENCES accounts(id) ON DELETE SET NULL,
    category_id BIGINT REFERENCES finance_categories(id) ON DELETE SET NULL,
    
    -- Transaction type
    transaction_type VARCHAR(20) NOT NULL CHECK (
        transaction_type IN ('income', 'expense', 'transfer')
    ),
    
    -- Amount
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'RUB',
    
    -- For transfers
    to_account_id BIGINT REFERENCES accounts(id) ON DELETE SET NULL,
    destination_amount NUMERIC(15, 2),
    destination_currency VARCHAR(3),
    exchange_rate NUMERIC(10, 6),
    
    -- Description
    title VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Date
    transaction_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    -- Tags
    tags VARCHAR(255)[],
    
    -- Attachments
    attachment_count INTEGER DEFAULT 0,
    
    -- Status
    is_reconciled BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning
    version INTEGER DEFAULT 1
);

-- Budgets
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES finance_categories(id) ON DELETE SET NULL,
    account_id BIGINT REFERENCES accounts(id) ON DELETE SET NULL,
    
    -- Budget info
    name VARCHAR(255) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'RUB',
    
    -- Period
    period_type VARCHAR(20) NOT NULL CHECK (
        period_type IN ('daily', 'weekly', 'monthly', 'quarterly', 'yearly')
    ),
    start_date DATE NOT NULL,
    end_date DATE,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Goals
CREATE TABLE finance_goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    
    -- Goal info
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_amount NUMERIC(15, 2) NOT NULL,
    saved_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'RUB',
    
    -- Deadline
    deadline_date DATE,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (
        status IN ('active', 'completed', 'cancelled')
    ),
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Finance
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_finance_categories_user_id ON finance_categories(user_id);
CREATE INDEX idx_finance_transactions_user_id ON finance_transactions(user_id);
CREATE INDEX idx_finance_transactions_account_id ON finance_transactions(account_id);
CREATE INDEX idx_finance_transactions_category_id ON finance_transactions(category_id);
CREATE INDEX idx_finance_transactions_type ON finance_transactions(transaction_type);
CREATE INDEX idx_finance_transactions_date ON finance_transactions(transaction_date);
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_finance_goals_user_id ON finance_goals(user_id);
```

#### Notes Database

```sql
-- Notes
CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    project_id BIGINT REFERENCES projects(id) ON DELETE SET NULL,
    task_id BIGINT REFERENCES tasks(id) ON DELETE SET NULL,
    
    -- Content
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    content_markdown TEXT,
    
    -- Format
    content_type VARCHAR(20) NOT NULL DEFAULT 'markdown' CHECK (
        content_type IN ('markdown', 'plain', 'rich')
    ),
    
    -- Status
    is_pinned BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    
    -- Sorting
    sort_order INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    
    -- Versioning
    version INTEGER DEFAULT 1
);

-- Note folders
CREATE TABLE note_folders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20),
    icon VARCHAR(50),
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Note to folder mapping
CREATE TABLE note_folder_mapping (
    note_id BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    folder_id BIGINT REFERENCES note_folders(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY(note_id, folder_id)
);

-- Note tags
CREATE TABLE note_tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, name)
);

-- Note to tag mapping
CREATE TABLE note_tag_mapping (
    note_id BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES note_tags(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY(note_id, tag_id)
);

-- Note attachments
CREATE TABLE note_attachments (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    thumbnail_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for Notes
CREATE INDEX idx_notes_user_id ON notes(user_id);
CREATE INDEX idx_notes_project_id ON notes(project_id);
CREATE INDEX idx_notes_task_id ON notes(task_id);
CREATE INDEX idx_notes_is_pinned ON notes(is_pinned);
CREATE INDEX idx_notes_is_archived ON notes(is_archived);
CREATE INDEX idx_note_folders_user_id ON note_folders(user_id);
CREATE INDEX idx_note_folder_mapping_note_id ON note_folder_mapping(note_id);
CREATE INDEX idx_note_folder_mapping_folder_id ON note_folder_mapping(folder_id);
CREATE INDEX idx_note_tags_user_id ON note_tags(user_id);
CREATE INDEX idx_note_tag_mapping_note_id ON note_tag_mapping(note_id);
CREATE INDEX idx_note_attachments_note_id ON note_attachments(note_id);
```

### Redis Usage

#### Cache
```
- User sessions (TTL: 24h)
- Frequently accessed tasks (TTL: 5min)
- Project data (TTL: 10min)
- Finance summaries (TTL: 1h)
```

#### Queue
```
- Sync queue (for background processing)
- Notification queue
- AI processing queue
```

#### Rate Limiting
```
- API rate limiting per user
- IP-based rate limiting
```

## 📊 Последствия

### Положительные
- ✅ Надежное хранение данных
- ✅ Высокая производительность запросов
- ✅ Поддержка сложных запросов
- ✅ Масштабируемость
- ✅ Поддержка синхронизации

### Отрицательные
- ⚠️ Сложность управления несколькими БД
- ⚠️ Необходимость синхронизации между БД
- ⚠️ Затраты на инфраструктуру

### Нейтральные
- 🔹 Необходимость оптимизации запросов
- 🔹 Мониторинг производительности БД

## 🔗 Связанные решения
- [ADR-001: Микросервисная архитектура](ADR-001-microservices.md) — Service structure
- [ADR-002: Offline-first подход](ADR-002-offline-first.md) — Sync requirements
- [ADR-003: API дизайн](ADR-003-api-design.md) — API endpoints

## 📝 Примечания
- **Migrations** — Используем Flyway или Liquibase для управления миграциями
- **Backup** — Регулярное резервирование данных
- **Replication** — Master-Slave репликация для читающих запросов
- **Partitioning** — Разделение больших таблиц по пользователям
- **Connection Pooling** — Используем HikariCP для управления соединениями

## ✅ Статус
- [ ] Proposed
- [ ] Under Discussion
- [x] Accepted
- [ ] Deprecated
- [ ] Replaced by [ADR-ZZZ]
