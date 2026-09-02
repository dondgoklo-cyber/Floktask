package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 3 to 4.
 * Adds design tokens and UI improvements.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add design-related fields to tasks and projects
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN color TEXT
        """)
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN color TEXT
        """)
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN icon TEXT
        """)
        
        // Add reminderDate field
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN reminderDate INTEGER
        """)
        
        // Create index for reminderDate
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_reminderDate ON tasks(reminderDate)
        """)
    }
}
