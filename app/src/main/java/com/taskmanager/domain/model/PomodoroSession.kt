package com.taskmanager.domain.model

import java.time.Instant

data class PomodoroSession(
    val id: Long? = null,
    val taskId: Long?,
    val startTime: Instant = Instant.now(),
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val type: PomodoroType = PomodoroType.WORK
)
