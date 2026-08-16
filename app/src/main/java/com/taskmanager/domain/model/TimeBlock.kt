package com.taskmanager.domain.model

import java.time.Instant
import java.time.LocalDate

data class TimeBlock(
    val id: Long? = null,
    val title: String,
    val taskId: Long? = null,
    val projectId: Long? = null,
    val startTime: Instant,
    val endTime: Instant,
    val color: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    init {
        require(!endTime.isBefore(startTime)) {
            "endTime must not be before startTime"
        }
    }

    val durationMinutes: Long
        get() = (endTime.toEpochMilli() - startTime.toEpochMilli()) / 60_000

    fun date(zone: java.time.ZoneId = java.time.ZoneId.systemDefault()): LocalDate =
        startTime.atZone(zone).toLocalDate()
}
