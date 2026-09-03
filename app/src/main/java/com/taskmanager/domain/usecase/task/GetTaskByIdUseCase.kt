package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long): Task? = runCatching {
        taskRepository.getTaskById(id)
    }.onFailure { e ->
        logger.error("GetTaskByIdUseCase", "Error in invoke", e)
    }.getOrNull()
}
