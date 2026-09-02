package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) = runCatching {
        taskRepository.deleteTask(taskId)
    }.onFailure { e ->
        Log.e("DeleteTaskUseCase", "Error in invoke", e)
    }
}
