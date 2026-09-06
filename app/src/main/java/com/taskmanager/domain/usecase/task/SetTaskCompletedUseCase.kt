package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for setting task completion status.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class SetTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(taskId: Long, completed: Boolean) {
        taskRepository.setCompleted(taskId, completed)
    }
}
