package com.taskmanager.domain.model

import java.time.Instant

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val projectId: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val deadline: Long? = null, // Timestamp in milliseconds
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val color: String? = null,
    val reminderDate: Long? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val tagIds: List<Long> = emptyList()
)

enum class Priority(val value: Int) {
    HIGH(1), MEDIUM(2), LOW(3), NONE(4)
}

enum class RecurrenceRule {
    DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}
