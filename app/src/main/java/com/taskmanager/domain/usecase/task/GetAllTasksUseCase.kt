package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = taskRepository.getAllTasks()
}
