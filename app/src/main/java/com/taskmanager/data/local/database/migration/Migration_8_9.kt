package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 8 to 9.
 * Adds Notes & Knowledge Base + Haptic Feedback (modules 14 + 15).
 */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create NoteFolder table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS note_folders (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                color TEXT,
                icon TEXT,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                isArchived INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Create Note table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT,
                folderId INTEGER,
                projectId INTEGER,
                tag TEXT,
                color TEXT,
                isPinned INTEGER NOT NULL DEFAULT 0,
                isArchived INTEGER NOT NULL DEFAULT 0,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(folderId) REFERENCES note_folders(id) ON DELETE SET NULL,
                FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE
            )
        """)
        
        // Create indexes
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_notes_folderId ON notes(folderId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_notes_projectId ON notes(projectId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_notes_isPinned ON notes(isPinned)
        """)
    }
}
