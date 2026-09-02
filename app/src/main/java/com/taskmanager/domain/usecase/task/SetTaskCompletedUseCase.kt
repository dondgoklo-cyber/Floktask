package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.notification.ReminderScheduler
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for marking a task as completed or incomplete.
 * Coordinates DB update with reminder lifecycle: completing a task cancels its
 * scheduled alarm, so no stale reminders fire for done tasks.
 */
class SetTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) {

    /**
     * Set task completion status.
     * @param taskId The ID of the task to update
     * @param completed The new completion status
     */
    suspend operator fun invoke(taskId: Long, completed: Boolean) {
        taskRepository.setCompleted(taskId, completed)
        if (completed) {
            reminderScheduler.cancelReminder(taskId)
        }
    }

    /**
     * Toggle task completion status (convenience method).
     * @param taskId The ID of the task to toggle
     */
    suspend fun toggle(taskId: Long) {
        val task = taskRepository.getTaskById(taskId) ?: return
        invoke(taskId, !task.isCompleted)
    }
}
