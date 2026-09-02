package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 6 to 7.
 * Adds core module features.
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add createdAt and updatedAt fields to tasks if not exists
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
        """)
        
        // Add status field
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN status TEXT NOT NULL DEFAULT 'TODO'
        """)
        
        // Create index for status
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_status ON tasks(status)
        """)
    }
}
