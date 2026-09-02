package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 4 to 5.
 * Adds Focus mode related fields.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add focus mode fields to tasks
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN pomodoroEstimate INTEGER
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN timeEstimateMinutes INTEGER
        """)
        
        // Add deadline field if not exists
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN deadline INTEGER
        """)
        
        // Add startTime field
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN startTime INTEGER
        """)
        
        // Create indexes for new fields
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_deadline ON tasks(deadline)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_startTime ON tasks(startTime)
        """)
    }
}
