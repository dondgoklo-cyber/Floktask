package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 11 to 12.
 * Adds Financial Goals (module 3).
 */
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create Goal table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                targetAmount TEXT NOT NULL,
                currentAmount TEXT NOT NULL DEFAULT '0',
                currency TEXT NOT NULL DEFAULT 'USD',
                deadline INTEGER,
                startDate INTEGER NOT NULL DEFAULT 0,
                isCompleted INTEGER NOT NULL DEFAULT 0,
                color TEXT,
                icon TEXT,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Create indexes
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_goals_deadline ON goals(deadline)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_goals_isCompleted ON goals(isCompleted)
        """)
    }
}
