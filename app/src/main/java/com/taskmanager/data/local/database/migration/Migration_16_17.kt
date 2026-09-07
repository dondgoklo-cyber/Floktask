package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 16 to 17.
 * Fixes schema mismatch between migrations and entities for finance/notes tables.
 *
 * The original migrations (8->9, 9->10, 10->11, 11->12) created tables with
 * different names and columns than what the entity classes expect.
 * This migration drops and recreates all affected tables with the correct schema
 * matching the current entity definitions.
 */
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop old tables with wrong schema (created by earlier migrations)
        database.execSQL("DROP TABLE IF EXISTS transactions")
        database.execSQL("DROP TABLE IF EXISTS categories")
        database.execSQL("DROP TABLE IF EXISTS finance_categories")
        database.execSQL("DROP TABLE IF EXISTS accounts")
        database.execSQL("DROP TABLE IF EXISTS notes")
        database.execSQL("DROP TABLE IF EXISTS note_folders")
        database.execSQL("DROP TABLE IF EXISTS budgets")
        database.execSQL("DROP TABLE IF EXISTS goals")
        database.execSQL("DROP TABLE IF EXISTS subprojects")

        // Recreate note_folders with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS note_folders (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_note_folders_name ON note_folders(name)")

        // Recreate notes with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                contentMarkdown TEXT NOT NULL,
                folderId INTEGER,
                tags TEXT,
                pinned INTEGER NOT NULL,
                archived INTEGER NOT NULL,
                projectId INTEGER,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """)
        database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_folderId ON notes(folderId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_projectId ON notes(projectId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_pinned ON notes(pinned)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_archived ON notes(archived)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_updatedAt ON notes(updatedAt)")

        // Recreate finance_categories with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS finance_categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                color TEXT,
                icon TEXT,
                isDefault INTEGER NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_finance_categories_name_type ON finance_categories(name, type)")

        // Recreate accounts with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                initialBalance TEXT NOT NULL,
                currency TEXT NOT NULL
            )
        """)

        // Recreate transactions with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                amount TEXT NOT NULL,
                type TEXT NOT NULL,
                currency TEXT NOT NULL,
                categoryId INTEGER,
                accountId INTEGER,
                date INTEGER NOT NULL,
                note TEXT,
                toAccountId INTEGER,
                destinationAmount TEXT,
                destinationCurrency TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """)
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_type ON transactions(type)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_accountId ON transactions(accountId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(date)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_currency ON transactions(currency)")

        // Recreate budgets with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS budgets (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                categoryId INTEGER NOT NULL,
                amount TEXT NOT NULL,
                currency TEXT NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_categoryId ON budgets(categoryId)")

        // Recreate goals with correct schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                targetAmount TEXT NOT NULL,
                savedAmount TEXT NOT NULL,
                currency TEXT NOT NULL,
                deadline INTEGER,
                createdAt INTEGER NOT NULL
            )
        """)
    }
}