package com.taskmanager.domain.model

import java.time.Instant

/**
 * A completed Pomodoro session, optionally linked to a [taskId] so that
 * focused time can be attributed to a specific task (issue 11).
 */
data class PomodoroSession(
    val id: Long? = null,
    val taskId: Long?,
    val startTime: Instant = Instant.now(),
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val type: PomodoroType = PomodoroType.WORK
)
