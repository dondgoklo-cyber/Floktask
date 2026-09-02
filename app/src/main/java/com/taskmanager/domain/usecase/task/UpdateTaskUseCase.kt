package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.notification.ReminderScheduler
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Updates a task and keeps its reminder in sync with the stored reminderDate:
 * a future reminderDate reschedules the alarm; a null/past reminderDate cancels it.
 * This centralises reminder lifecycle for all update paths (drag in calendar, detail, edit).
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task)
        syncReminder(task)
    }

    /**
     * Reschedule or cancel the reminder so that AlarmManager always matches the DB.
     * Safe for tasks without an id (new tasks): no-op.
     */
    private fun syncReminder(task: Task) {
        val id = task.id ?: return
        // Завершённая задача не должна получать напоминания, даже если reminderDate задан.
        val reminder = task.reminderDate
        if (!task.isCompleted && reminder != null && reminder.toEpochMilli() > System.currentTimeMillis()) {
            reminderScheduler.scheduleReminder(id, task.title, reminder.toEpochMilli())
        } else {
            reminderScheduler.cancelReminder(id)
        }
    }
}
