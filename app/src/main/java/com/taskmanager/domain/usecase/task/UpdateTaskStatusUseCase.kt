package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for updating task status.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class UpdateTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(task: Task, newStatus: TaskStatus) {
        taskRepository.updateTask(task.copy(status = newStatus, isCompleted = newStatus == TaskStatus.DONE))
    }
}
