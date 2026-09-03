package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(task: Task): Long = runCatching {
        taskRepository.createTask(task)
    }.onFailure { e ->
        logger.error("CreateTaskUseCase", "Error in invoke", e)
    }.getOrThrow()
}
