package com.taskmanager.domain.model

import java.time.Instant

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val projectId: Long? = null,
    val subprojectId: Long? = null,
    val priority: Priority = Priority.NONE,
    val status: TaskStatus = TaskStatus.TODO,
    val deadline: Instant? = null,
    val startTime: Instant? = null,
    val durationMinutes: Long? = null,
    val isCompleted: Boolean = false,
    val pomodoroEstimate: Int? = null,
    val timeEstimateMinutes: Long? = null,
    val eisenhowerQuadrant: EisenhowerQuadrant? = null,
    val tags: List<String> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val color: String? = null,
    val reminderDate: Instant? = null,
    val recurrenceRule: RecurrenceRule? = null
)

enum class Priority(val value: Int) {
    HIGH(1), MEDIUM(2), LOW(3), NONE(4)
}

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

/** Квадранты матрицы Эйзенхауэра. */
enum class EisenhowerQuadrant {
    DO_NOW,
    SCHEDULE,
    DELEGATE,
    ELIMINATE
}

enum class RecurrenceRule {
    DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}
