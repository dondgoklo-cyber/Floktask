package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        if (task.id != null && task.id > 0) {
            taskRepository.updateTask(task)
        } else {
            taskRepository.createTask(task)
        }
    }
}
