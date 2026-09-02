package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 1 to 2.
 * Adds UserStatsEntity, SubtaskEntity, HabitEntity, HabitLogEntity, PomodoroSessionEntity.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Version 1 had: TaskEntity, ProjectEntity, TagEntity
        // Version 2 adds: UserStatsEntity, SubtaskEntity, HabitEntity, HabitLogEntity, PomodoroSessionEntity
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_stats (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                totalTasksCompleted INTEGER NOT NULL DEFAULT 0,
                totalTasksCreated INTEGER NOT NULL DEFAULT 0,
                longestStreak INTEGER NOT NULL DEFAULT 0,
                currentStreak INTEGER NOT NULL DEFAULT 0,
                totalFocusTimeMinutes INTEGER NOT NULL DEFAULT 0,
                lastActivityDate INTEGER,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS subtasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                taskId INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                isCompleted INTEGER NOT NULL DEFAULT 0,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                parentSubtaskId INTEGER,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE CASCADE,
                FOREIGN KEY(parentSubtaskId) REFERENCES subtasks(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subtasks_taskId ON subtasks(taskId)
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subtasks_parentSubtaskId ON subtasks(parentSubtaskId)
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS habits (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                frequencyType TEXT NOT NULL DEFAULT 'DAILY',
                targetCount INTEGER NOT NULL DEFAULT 1,
                color TEXT,
                icon TEXT,
                isActive INTEGER NOT NULL DEFAULT 1,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS habit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                habitId INTEGER NOT NULL,
                completionDate INTEGER NOT NULL,
                count INTEGER NOT NULL DEFAULT 1,
                notes TEXT,
                FOREIGN KEY(habitId) REFERENCES habits(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_habit_logs_habitId ON habit_logs(habitId)
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS pomodoro_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                taskId INTEGER,
                startTime INTEGER NOT NULL,
                endTime INTEGER,
                durationMinutes INTEGER NOT NULL DEFAULT 25,
                isCompleted INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_pomodoro_sessions_taskId ON pomodoro_sessions(taskId)
        """)
    }
}
