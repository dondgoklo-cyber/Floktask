package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching tasks.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class SearchTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    operator fun invoke(query: String): Flow<List<Task>> = taskRepository.searchTasks(query)
}
