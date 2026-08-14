package com.taskmanager.domain.model

import java.time.Instant

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val projectId: Long? = null,
    val priority: Priority = Priority.NONE,
    val deadline: Instant? = null,
    val isCompleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val color: String? = null,
    val reminderDate: Instant? = null,
    val recurrenceRule: RecurrenceRule? = null
)

enum class Priority(val value: Int) {
    HIGH(1), MEDIUM(2), LOW(3), NONE(4)
}

enum class RecurrenceRule {
    DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}
