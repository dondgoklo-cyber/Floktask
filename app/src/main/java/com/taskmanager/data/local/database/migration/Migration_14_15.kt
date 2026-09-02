package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 14 to 15.
 * Removes SubprojectEntity (cleanup - was causing issues).
 * Adds eisenhowerQuadrant to tasks.
 */
val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add eisenhowerQuadrant field to tasks
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN eisenhowerQuadrant TEXT
        """)
        
        // Create index for eisenhowerQuadrant
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_eisenhowerQuadrant ON tasks(eisenhowerQuadrant)
        """)
        
        // Add isCompleted index if not exists
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_isCompleted ON tasks(isCompleted)
        """)
        
        // Add priority index if not exists
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_priority ON tasks(priority)
        """)
        
        // Add projectId index if not exists
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_projectId ON tasks(projectId)
        """)
    }
}
