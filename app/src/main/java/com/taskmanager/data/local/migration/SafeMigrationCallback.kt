package com.taskmanager.data.local.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Outcome of a database migration attempt, surfaced to the UI (issue 26:
 * on migration failure the app crashed; need rollback + user notification).
 */
sealed class MigrationOutcome {
    data object Idle : MigrationOutcome()
    data object Started : MigrationOutcome()
    data object Success : MigrationOutcome()
    data class Failed(val error: Throwable) : MigrationOutcome()
}

/**
 * Singleton bus that exposes migration outcomes to the presentation layer.
 */
class MigrationStatusBus {
    private val _state = MutableStateFlow<MigrationOutcome>(MigrationOutcome.Idle)
    val state: StateFlow<MigrationOutcome> = _state.asStateFlow()

    fun started() { _state.value = MigrationOutcome.Started }
    fun succeeded() { _state.value = MigrationOutcome.Success }
    fun failed(error: Throwable) { _state.value = MigrationOutcome.Failed(error) }
    fun reset() { _state.value = MigrationOutcome.Idle }
}

/**
 * Wraps a [migration] in a transaction with rollback on failure, surfacing the
 * outcome via [statusBus]. Call from a [RoomDatabase.Callback] or migration
 * runner — the transaction ensures atomicity (issue 26).
 */
class SafeMigrationRunner(private val statusBus: MigrationStatusBus) {

    fun runMigration(db: SupportSQLiteDatabase, migration: () -> Unit) {
        statusBus.started()
        db.beginTransaction()
        try {
            migration()
            db.setTransactionSuccessful()
            statusBus.succeeded()
        } catch (t: Throwable) {
            // Transaction not marked successful → auto-rolled back by SQLite.
            statusBus.failed(t)
        } finally {
            db.endTransaction()
        }
    }
}
