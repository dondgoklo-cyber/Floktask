package com.taskmanager.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Явные миграции Room-схемы между последовательными версиями базы данных.
 *
 * Ранее в проекте использовался `fallbackToDestructiveMigration()`, что приводило к
 * ПОЛНОЙ ПОТЕРЕ данных пользователя при любом изменении схемы. Эти миграции заменяют
 * деструктивную стратегию на реальные ALTER/CREATE операции, сохраняющие данные.
 *
 * История версий схемы (по git history AppDatabase):
 *  v1  — базовый scaffold (tasks, projects, tags)
 *  v2  — gamification (user_stats)
 *  v3  — geofencing (без изменений схемы, учётных таблиц нет в текущей БД)
 *  v4  — редизайн (без изменений схемы)
 *  v5  — tasks: +status, +startTime, +durationMinutes, +pomodoroEstimate,
 *        +timeEstimateMinutes, +eisenhowerQuadrant; +index startTime/eisenhowerQuadrant
 *  v6  — subtasks (5 уровней)
 *  v7  — habits, habit_logs, pomodoro_sessions; tags в tasks
 *  v8  — transactions, finance_categories, accounts
 *  v9  — notes, note_folders
 *  v10 — multi-currency: transactions +toAccountId/+destinationAmount/+destinationCurrency/+currency
 *  v11 — budgets
 *  v12 — goals
 *  v13 — task_tags (many-to-many), projects +deadline/+icon, projects +index isArchived
 *  v14 — subprojects, tasks +subprojectId (+FK cascade), +index subprojectId
 *
 * Примечание: точные DDL для ранних версий восстановлены по git-истории сущностей.
 * Миграции CREATE для новых таблиц используют определения, соответствующие финальной
 * схеме (v14); при добавлении таблицы создаётся сразу её финальная структура, что
 * эквивалентно «новая таблица появилась» и не требует последующих ALTER для неё.
 *
 * TODO (требует Robolectric/эмулятора, недоступно в статической среде):
 *   покрыть MigrationTestHelper тестами v1->v14 для каждой стартовой версии.
 */
object Migrations {

    /** v1 -> v5: расширение таблицы tasks новыми колонками (статус, планирование). */
    val MIGRATION_1_5 = object : Migration(1, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN status TEXT NOT NULL DEFAULT 'TODO'")
            db.execSQL("ALTER TABLE tasks ADD COLUMN startTime INTEGER")
            db.execSQL("ALTER TABLE tasks ADD COLUMN durationMinutes INTEGER")
            db.execSQL("ALTER TABLE tasks ADD COLUMN pomodoroEstimate INTEGER")
            db.execSQL("ALTER TABLE tasks ADD COLUMN timeEstimateMinutes INTEGER")
            db.execSQL("ALTER TABLE tasks ADD COLUMN eisenhowerQuadrant TEXT")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_startTime ON tasks(startTime)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_eisenhowerQuadrant ON tasks(eisenhowerQuadrant)")
        }
    }

    /** v5 -> v6: таблица подзадач. */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS subtasks (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    taskId INTEGER NOT NULL,
                    parentSubtaskId INTEGER,
                    title TEXT NOT NULL,
                    isCompleted INTEGER NOT NULL,
                    orderIndex INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    FOREIGN KEY(taskId) REFERENCES tasks(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_subtasks_taskId ON subtasks(taskId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_subtasks_parentSubtaskId ON subtasks(parentSubtaskId)")
        }
    }

    /** v6 -> v7: привычки, журналы привычек, помодоро-сессии; legacy CSV-теги в tasks. */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN tags TEXT")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS habits (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    icon TEXT,
                    color TEXT,
                    frequency TEXT NOT NULL,
                    daysOfWeek TEXT NOT NULL,
                    targetCount INTEGER NOT NULL,
                    reminderTime INTEGER,
                    isArchived INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_habits_isArchived ON habits(isArchived)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS habit_logs (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    habitId INTEGER NOT NULL,
                    date INTEGER NOT NULL,
                    count INTEGER NOT NULL,
                    completedAt INTEGER NOT NULL,
                    FOREIGN KEY(habitId) REFERENCES habits(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_habit_logs_habitId ON habit_logs(habitId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_habit_logs_date ON habit_logs(date)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS pomodoro_sessions (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    taskId INTEGER,
                    startTime INTEGER NOT NULL,
                    durationMinutes INTEGER NOT NULL,
                    isCompleted INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    FOREIGN KEY(taskId) REFERENCES tasks(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_pomodoro_sessions_taskId ON pomodoro_sessions(taskId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_pomodoro_sessions_createdAt ON pomodoro_sessions(createdAt)")
        }
    }

    /** v7 -> v8: финансы — транзакции, категории, счета. */
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS finance_categories (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    color TEXT,
                    icon TEXT,
                    isDefault INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_finance_categories_name_type ON finance_categories(name, type)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    initialBalance REAL NOT NULL,
                    currency TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    amount REAL NOT NULL,
                    type TEXT NOT NULL,
                    currency TEXT NOT NULL,
                    categoryId INTEGER,
                    accountId INTEGER,
                    date INTEGER NOT NULL,
                    note TEXT,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            for (col in listOf("type", "categoryId", "accountId", "date", "currency")) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_$col ON transactions($col)")
            }
        }
    }

    /** v8 -> v9: заметки и папки заметок. */
    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_folders (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_note_folders_name ON note_folders(name)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    contentMarkdown TEXT NOT NULL,
                    folderId INTEGER,
                    tags TEXT,
                    pinned INTEGER NOT NULL,
                    archived INTEGER NOT NULL,
                    projectId INTEGER,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            for (col in listOf("folderId", "projectId", "pinned", "archived", "updatedAt")) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_$col ON notes($col)")
            }
        }
    }

    /** v9 -> v10: мульти-валютные переводы в транзакциях. */
    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN toAccountId INTEGER")
            db.execSQL("ALTER TABLE transactions ADD COLUMN destinationAmount REAL")
            db.execSQL("ALTER TABLE transactions ADD COLUMN destinationCurrency TEXT")
        }
    }

    /** v10 -> v11: бюджеты по категориям. */
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    categoryId INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    currency TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_categoryId ON budgets(categoryId)")
        }
    }

    /** v11 -> v12: финансовые цели. */
    val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS goals (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    targetAmount REAL NOT NULL,
                    savedAmount REAL NOT NULL,
                    currency TEXT NOT NULL,
                    deadline INTEGER,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /** v12 -> v13: many-to-many теги, расширение projects (deadline, icon, index isArchived). */
    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE projects ADD COLUMN deadline INTEGER")
            db.execSQL("ALTER TABLE projects ADD COLUMN icon TEXT")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_projects_isArchived ON projects(isArchived)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS task_tags (
                    taskId INTEGER NOT NULL,
                    tagId INTEGER NOT NULL,
                    PRIMARY KEY(taskId, tagId),
                    FOREIGN KEY(taskId) REFERENCES tasks(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(tagId) REFERENCES tags(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_task_tags_taskId ON task_tags(taskId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_task_tags_tagId ON task_tags(tagId)")
        }
    }

    /** v13 -> v14: подпроекты, tasks.subprojectId (+FK cascade, +index). */
    val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN subprojectId INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_subprojectId ON tasks(subprojectId)")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS subprojects (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    parentProjectId INTEGER,
                    parentSubprojectId INTEGER,
                    color TEXT,
                    icon TEXT,
                    deadline INTEGER,
                    isArchived INTEGER NOT NULL,
                    orderIndex INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    FOREIGN KEY(parentProjectId) REFERENCES projects(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(parentSubprojectId) REFERENCES subprojects(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            for (col in listOf("parentProjectId", "parentSubprojectId", "color", "isArchived", "orderIndex")) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_subprojects_$col ON subprojects($col)")
            }
        }
    }

    /** Все миграции в порядке возрастания версий для регистрации в Room.databaseBuilder. */
    val ALL: Array<Migration> = arrayOf(
        MIGRATION_1_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8,
        MIGRATION_8_9,
        MIGRATION_9_10,
        MIGRATION_10_11,
        MIGRATION_11_12,
        MIGRATION_12_13,
        MIGRATION_13_14
    )
}
