package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(task: Task) = runCatching {
        taskRepository.updateTask(task)
    }.onFailure { e ->
        logger.error("UpdateTaskUseCase", "Error in invoke", e)
    }
}
