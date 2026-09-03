package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 15 to 16.
 * Migrates legacy TaskEntity.tags (String CSV) to normalized TaskTagEntity relationships.
 * This ensures data consistency between the old string-based tags and the new many-to-many system.
 */
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a temporary table to store tasks with legacy tags
        database.execSQL("""
            CREATE TEMP TABLE IF NOT EXISTS temp_tasks_with_tags AS
            SELECT id, tags FROM tasks WHERE tags IS NOT NULL AND tags != ''
        """)

        // For each task with legacy tags, parse and create normalized relationships
        // Note: This is a simplified migration that handles CSV format
        // Real implementation would need to handle various edge cases
        
        // Get all tasks with tags
        val cursor = database.query("SELECT id, tags FROM temp_tasks_with_tags")
        
        while (cursor.moveToNext()) {
            val taskId = cursor.getLong(0)
            val tagsString = cursor.getString(1) ?: ""
            
            if (tagsString.isNotBlank()) {
                // Parse tags (assuming CSV format)
                val tagNames = tagsString.split(",").map { it.trim() }.filter { it.isNotBlank() }
                
                for (tagName in tagNames) {
                    // Find or create the tag
                    var tagId: Long = -1
                    val tagCursor = database.query(
                        "SELECT id FROM tags WHERE name = ?",
                        arrayOf(tagName)
                    )
                    
                    if (tagCursor.moveToNext()) {
                        tagId = tagCursor.getLong(0)
                    } else {
                        // Create new tag
                        database.execSQL(
                            "INSERT INTO tags (name) VALUES (?)",
                            arrayOf(tagName)
                        )
                        val idStatement = database.compileStatement("SELECT last_insert_rowid()")
                        tagId = idStatement.simpleQueryForLong()
                    }
                    tagCursor.close()
                    
                    if (tagId > 0) {
                        // Create relationship in task_tags
                        try {
                            database.execSQL(
                                "INSERT OR IGNORE INTO task_tags (taskId, tagId) VALUES (?, ?)",
                                arrayOf(taskId.toString(), tagId.toString())
                            )
                        } catch (e: Exception) {
                            // Already exists, skip
                        }
                    }
                }
            }
        }
        cursor.close()
        
        // Drop temp table
        database.execSQL("DROP TABLE IF EXISTS temp_tasks_with_tags")
    }
}
