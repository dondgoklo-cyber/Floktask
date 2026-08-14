package com.taskmanager.domain.model

import java.time.Instant

data class PomodoroSession(
    val id: Long? = null,
    val taskId: Long? = null,
    val startTime: Instant,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val type: PomodoroType = PomodoroType.WORK,
    val createdAt: Instant = Instant.now()
)

enum class PomodoroType {
    WORK, SHORT_BREAK, LONG_BREAK
}
