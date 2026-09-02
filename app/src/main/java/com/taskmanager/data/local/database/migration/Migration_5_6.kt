package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 5 to 6.
 * Adds 5-level priority improvements.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add durationMinutes field to tasks
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN durationMinutes INTEGER
        """)
        
        // Add isArchived field to projects
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0
        """)
        
        // Create index for isArchived
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_projects_isArchived ON projects(isArchived)
        """)
    }
}
