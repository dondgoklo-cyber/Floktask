package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for marking a task as completed or incomplete.
 * This is a core business operation that updates both the task status
 * and triggers any related notifications or analytics.
 */
class SetTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    
    /**
     * Toggle task completion status
     * @param taskId The ID of the task to update
     * @param completed The new completion status
     */
    suspend operator fun invoke(taskId: Long, completed: Boolean) {
        taskRepository.setCompleted(taskId, completed)
    }
    
    /**
     * Toggle task completion status (convenience method)
     * @param taskId The ID of the task to toggle
     */
    suspend fun toggle(taskId: Long) {
        val task = taskRepository.getTaskById(taskId) ?: return
        taskRepository.setCompleted(taskId, !task.isCompleted)
    }
}
