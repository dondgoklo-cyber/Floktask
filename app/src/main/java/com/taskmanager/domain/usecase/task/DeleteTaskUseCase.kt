package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.notification.AlarmScheduler
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val logger: Logger
) {
    suspend operator fun invoke(taskId: Long) = runCatching {
        // Cancel any pending reminder before deleting
        alarmScheduler.cancelReminder(taskId)
        taskRepository.deleteTask(taskId)
    }.onFailure { e ->
        logger.error("DeleteTaskUseCase", "Error in invoke", e)
    }
}
