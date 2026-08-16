package com.taskmanager.domain.repository

import com.taskmanager.domain.model.BackupFile
import java.io.InputStream
import java.io.OutputStream

interface BackupRepository {

    /**
     * Build a [BackupFile] snapshot of all current data.
     */
    suspend fun export(): BackupFile

    /**
     * Serialize a [BackupFile] snapshot to JSON, writing to [output].
     */
    suspend fun serialize(file: BackupFile, output: OutputStream)

    /**
     * Parse JSON from [input] into a [BackupFile], validating the schema version.
     * @throws IllegalArgumentException if schema version is unsupported.
     */
    suspend fun deserialize(input: InputStream): BackupFile

    /**
     * Replace all local data with the contents of [file]. Returns the counts
     * of restored entities.
     */
    suspend fun restore(file: BackupFile): RestoreResult

    /**
     * Save a fresh snapshot to the app-private backup directory and return its name.
     */
    suspend fun saveToLocal(): String

    /**
     * Load a backup previously saved by [saveToLocal] by [fileName].
     */
    suspend fun loadFromLocal(fileName: String): BackupFile

    /**
     * List all backups stored in the app-private backup directory, newest first.
     */
    suspend fun listLocalBackups(): List<LocalBackup>

    /**
     * Delete a local backup by [fileName]. Returns true if deleted.
     */
    suspend fun deleteLocalBackup(fileName: String): Boolean
}

data class RestoreResult(
    val tasksRestored: Int,
    val projectsRestored: Int,
    val tagsRestored: Int
)

data class LocalBackup(
    val fileName: String,
    val createdAtMillis: Long,
    val sizeBytes: Long
)
