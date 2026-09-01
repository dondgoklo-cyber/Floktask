package com.taskmanager.domain.usecase.kanban

import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for moving a task to a different status in Kanban board.
 * Updates the task status and triggers any related notifications.
 */
class MoveTaskToStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    
    /**
     * Move task to a new status
     * @param taskId The ID of the task to move
     * @param newStatus The new status to set
     */
    suspend operator fun invoke(taskId: Long, newStatus: TaskStatus) {
        val task = taskRepository.getTaskById(taskId) ?: return
        taskRepository.updateTask(task.copy(status = newStatus))
    }
    
    /**
     * Move task to TODO status
     */
    suspend fun moveToTodo(taskId: Long) = invoke(taskId, TaskStatus.TODO)
    
    /**
     * Move task to IN_PROGRESS status
     */
    suspend fun moveToInProgress(taskId: Long) = invoke(taskId, TaskStatus.IN_PROGRESS)
    
    /**
     * Move task to DONE status
     */
    suspend fun moveToDone(taskId: Long) = invoke(taskId, TaskStatus.DONE)
}
