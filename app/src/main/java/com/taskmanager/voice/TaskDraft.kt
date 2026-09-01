package com.taskmanager.voice

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import java.time.LocalDate
import java.time.LocalTime

/**
 * 1f403e3c353643423e373d304f 3c3e34353b4c 3f3e413b35 3f304041383d3330 333e3b3e413e323e39 32323e3430.
 * 3f3e3a30374b32303542414f 3f3e3b4c373e323042353b4c 343b4f 3f3e34423235403634353d384f 413e3734303d38353c 3730343047303c.
 */
data class TaskDraft(
    val title: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val projectName: String? = null,
    val tags: List<String> = emptyList(),
    val priority: Priority = Priority.NONE,
    val durationMinutes: Long? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val rawText: String = "",
    val isAmbiguousTime: Boolean = false
)
