package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.SubtaskProgress
import javax.inject.Inject

/**
 * Recomputes a parent task's progress from its subtasks (issue 22:
 * completing subtasks didn't update the parent's progress bar).
 */
class ComputeSubtaskProgressUseCase @Inject constructor() {

    /**
     * Pure progress computation from a list of subtasks.
     */
    operator fun invoke(subtasks: List<Subtask>): SubtaskProgress {
        val total = subtasks.size
        val completed = subtasks.count { it.isCompleted }
        val ratio = if (total == 0) 0f else completed.toFloat() / total.toFloat()
        val parentId = subtasks.firstOrNull()?.parentTaskId ?: -1L
        return SubtaskProgress(
            parentTaskId = parentId,
            total = total,
            completed = completed,
            ratio = ratio
        )
    }

    /**
     * Whether the parent task should be auto-completed (all subtasks done).
     */
    fun shouldCompleteParent(subtasks: List<Subtask>): Boolean =
        subtasks.isNotEmpty() && subtasks.all { it.isCompleted }
}
