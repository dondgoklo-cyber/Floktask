package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 13 to 14.
 * Adds SubprojectEntity (hierarchy 2-3 levels deep).
 */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create Subproject table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS subprojects (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                parentProjectId INTEGER NOT NULL,
                parentSubprojectId INTEGER,
                color TEXT,
                icon TEXT,
                deadline INTEGER,
                isArchived INTEGER NOT NULL DEFAULT 0,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(parentProjectId) REFERENCES projects(id) ON DELETE CASCADE,
                FOREIGN KEY(parentSubprojectId) REFERENCES subprojects(id) ON DELETE CASCADE
            )
        """)
        
        // Create indexes
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subprojects_parentProjectId ON subprojects(parentProjectId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subprojects_parentSubprojectId ON subprojects(parentSubprojectId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subprojects_color ON subprojects(color)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subprojects_isArchived ON subprojects(isArchived)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_subprojects_orderIndex ON subprojects(orderIndex)
        """)
    }
}
