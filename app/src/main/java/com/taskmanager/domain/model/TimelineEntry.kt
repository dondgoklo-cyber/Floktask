package com.taskmanager.domain.model

import java.time.Instant

/**
 * A single entry on the unified timeline, derived from a [Task] (deadline)
 * or other sources. Keeps the timeline view decoupled from the source models.
 */
data class TimelineEntry(
    val id: String,
    val title: String,
    val start: Instant,
    val end: Instant?,
    val source: TimelineSource,
    val taskId: Long?,
    val color: String? = null,
    val isCompleted: Boolean = false
)

enum class TimelineSource { TASK_DEADLINE, TIME_BLOCK, CALENDAR_EVENT }
