package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 10 to 11.
 * Adds Budgets (module 2).
 */
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create Budget table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS budgets (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                amount TEXT NOT NULL,
                currency TEXT NOT NULL DEFAULT 'USD',
                categoryId INTEGER,
                startDate INTEGER NOT NULL,
                endDate INTEGER NOT NULL,
                resetPeriod TEXT NOT NULL DEFAULT 'MONTHLY',
                color TEXT,
                icon TEXT,
                isActive INTEGER NOT NULL DEFAULT 1,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE SET NULL
            )
        """)
        
        // Create indexes
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_budgets_categoryId ON budgets(categoryId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_budgets_startDate ON budgets(startDate)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_budgets_endDate ON budgets(endDate)
        """)
    }
}
