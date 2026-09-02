package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 9 to 10.
 * Adds Multi-currency personal finance + TRANSFER type (module 16).
 */
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create Category table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                color TEXT,
                icon TEXT,
                parentCategoryId INTEGER,
                isIncome INTEGER NOT NULL DEFAULT 1,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(parentCategoryId) REFERENCES categories(id) ON DELETE SET NULL
            )
        """)
        
        // Create Account table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                type TEXT NOT NULL DEFAULT 'CASH',
                balance TEXT NOT NULL DEFAULT '0',
                currency TEXT NOT NULL DEFAULT 'USD',
                color TEXT,
                icon TEXT,
                isActive INTEGER NOT NULL DEFAULT 1,
                orderIndex INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Create Transaction table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                accountId INTEGER NOT NULL,
                categoryId INTEGER,
                amount TEXT NOT NULL,
                currency TEXT NOT NULL DEFAULT 'USD',
                type TEXT NOT NULL DEFAULT 'EXPENSE',
                title TEXT NOT NULL,
                description TEXT,
                transactionDate INTEGER NOT NULL,
                recurrenceRule TEXT,
                tag TEXT,
                receiptPath TEXT,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(accountId) REFERENCES accounts(id) ON DELETE CASCADE,
                FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE SET NULL
            )
        """)
        
        // Create indexes
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_accountId ON transactions(accountId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_type ON transactions(type)
        """)
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_transactions_transactionDate ON transactions(transactionDate)
        """)
    }
}
