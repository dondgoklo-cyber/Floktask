package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(id: Long): Task? = runCatching {
        taskRepository.getTaskById(id)
    }.onFailure { e ->
        Log.e("GetTaskByIdUseCase", "Error in invoke", e)
    }
}
