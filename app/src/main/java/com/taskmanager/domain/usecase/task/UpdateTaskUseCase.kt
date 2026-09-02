package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = runCatching {
        taskRepository.updateTask(task)
    }.onFailure { e ->
        Log.e("UpdateTaskUseCase", "Error in invoke", e)
    }
}
