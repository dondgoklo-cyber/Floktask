package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving tasks by project.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetTasksByProjectUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    operator fun invoke(projectId: Long): Flow<List<Task>> = taskRepository.getTasksByProject(projectId)
}
