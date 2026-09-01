package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for toggling task completion status.
 * Simple one-click operation to mark task as done/undone.
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    
    /**
     * Toggle task completion status
     * @param taskId The ID of the task to toggle
     * @return The new completion status
     */
    suspend operator fun invoke(taskId: Long): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false
        val newStatus = !task.isCompleted
        taskRepository.setCompleted(taskId, newStatus)
        return newStatus
    }
}
