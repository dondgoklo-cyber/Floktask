package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(id: Long): Task? = taskRepository.getTaskById(id)
}
