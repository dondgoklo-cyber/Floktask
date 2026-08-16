package com.taskmanager.domain.model

import java.time.Instant

/**
 * Snapshot of all user data at a point in time, used for backup/restore.
 * Instant fields are serialized as epoch millis on the data layer.
 */
data class BackupData(
    val tasks: List<Task>,
    val projects: List<Project>,
    val tags: List<Tag>
)

data class BackupMetadata(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val appVersion: String,
    val createdAt: Instant = Instant.now(),
    val deviceName: String? = null
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 2
    }
}

data class BackupFile(
    val metadata: BackupMetadata,
    val data: BackupData
)
