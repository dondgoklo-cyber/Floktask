package com.taskmanager.domain.model

import java.time.Instant

/**
 * A logged time entry on a task (issue 36: tasks had estimate but no
 * actual time). Either start/stop-timer driven or manual entry.
 */
data class TimeEntry(
    val id: Long? = null,
    val taskId: Long,
    val start: Instant,
    val end: Instant?,
    val durationMinutes: Int,
    val note: String? = null,
    val isManual: Boolean = false
)

/**
 * Aggregated actual time vs estimate for a task.
 */
data class TimeSpentSummary(
    val taskId: Long,
    val totalMinutes: Int,
    val entryCount: Int,
    val estimateMinutes: Int?
) {
    val varianceMinutes: Int? get() = estimateMinutes?.let { totalMinutes - it }
}
