package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 7 to 8.
 * Adds core module features (part 4).
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add createdAt and updatedAt to projects
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0
        """)
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
        """)
        
        // Add deadline to projects
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN deadline INTEGER
        """)
        
        // Add description to projects
        database.execSQL("""
            ALTER TABLE projects ADD COLUMN description TEXT
        """)
    }
}
