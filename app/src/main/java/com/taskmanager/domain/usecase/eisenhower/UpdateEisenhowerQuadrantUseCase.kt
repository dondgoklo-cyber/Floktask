package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class UpdateEisenhowerQuadrantUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, quadrant: EisenhowerQuadrant?) = runCatching {
        val task = taskRepository.getTaskById(taskId) ?: return
        taskRepository.updateTask(task.copy(eisenhowerQuadrant = quadrant))
    }.onFailure { e ->
        Log.e("UpdateEisenhowerQuadrantUseCase", "Error in invoke", e)
    }
}
