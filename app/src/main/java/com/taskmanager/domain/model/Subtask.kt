package com.taskmanager.domain.model

/**
 * A subtask belonging to a parent [Task]. Completing subtasks updates the
 * parent's progress (issue 22).
 */
data class Subtask(
    val id: Long? = null,
    val parentTaskId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val order: Int = 0
)

/**
 * Computed progress for a parent task based on its subtasks.
 */
data class SubtaskProgress(
    val parentTaskId: Long,
    val total: Int,
    val completed: Int,
    val ratio: Float
) {
    val isFullyCompleted: Boolean get() = total > 0 && completed == total
}
