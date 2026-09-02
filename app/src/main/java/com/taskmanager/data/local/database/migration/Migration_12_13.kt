package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 12 to 13.
 * Adds Many-to-Many tags (TaskTagEntity).
 */
val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create TaskTag table (junction table for many-to-many relationship)
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS task_tags (
                taskId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                PRIMARY KEY (taskId, tagId),
                FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE CASCADE,
                FOREIGN KEY(tagId) REFERENCES tags(id) ON DELETE CASCADE
            )
        """)
        
        // Create indexes for the junction table
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_task_tags_taskId ON task_tags(taskId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_task_tags_tagId ON task_tags(tagId)
        """)
        
        // Migrate existing tags from TaskEntity.tags (legacy String field) to TaskTagEntity
        // This is a best-effort migration for existing data
        database.execSQL("""
            CREATE TEMP TABLE IF NOT EXISTS temp_task_tags AS
            SELECT id, tags FROM tasks WHERE tags IS NOT NULL AND tags != ''
        """)
    }
}
