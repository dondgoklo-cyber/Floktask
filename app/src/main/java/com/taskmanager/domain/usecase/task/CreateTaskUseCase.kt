package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long = runCatching {
        
    }.onFailure { e ->
        Log.e("CreateTaskUseCase", "Error in invoke", e)
    }
        taskRepository.createTask(task)
}
