package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class UpdateEisenhowerQuadrantUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, quadrant: EisenhowerQuadrant?): Result<Unit> = runCatching {
        val task = taskRepository.getTaskById(taskId) ?: return@runCatching
        taskRepository.updateTask(task.copy(eisenhowerQuadrant = quadrant))
        Result.success(Unit)
    }.onFailure { e ->
        Log.e("UpdateEisenhowerQuadrantUseCase", "Error in invoke", e)
    }.getOrElse { Result.failure(it) }
}
