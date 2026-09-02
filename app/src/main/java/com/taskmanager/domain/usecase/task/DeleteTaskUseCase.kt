package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.notification.ReminderScheduler
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(taskId: Long) {
        taskRepository.deleteTask(taskId)
        // Удаление задачи отменяет запланированное напоминание, иначе будильник
        // может сработать для несуществующей задачи.
        reminderScheduler.cancelReminder(taskId)
    }
}
