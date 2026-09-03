package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class UpdateEisenhowerQuadrantUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, quadrant: EisenhowerQuadrant?): Result<Unit> {
        return try {
            val task = taskRepository.getTaskById(taskId) ?: return@invoke Result.failure(Exception("Task not found"))
            taskRepository.updateTask(task.copy(eisenhowerQuadrant = quadrant))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UpdateEisenhowerQuadrantUseCase", "Error in invoke", e)
            Result.failure(e)
        }
    }
}
