package com.taskmanager.domain.usecase.batch

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Result of a batch operation: how many tasks were actually affected.
 */
data class BatchResult(val affected: Int)

/**
 * Multi-select bulk operations on tasks (issue 19): mark complete,
 * delete, and move to a project — applied to a set of task ids.
 */
class BatchTaskOperationsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    suspend fun complete(taskIds: List<Long>, completed: Boolean): BatchResult {
        var affected = 0
        taskIds.forEach { id ->
            val task = taskRepository.getTaskById(id) ?: return@forEach
            taskRepository.updateTask(
                task.copy(
                    isCompleted = completed,
                    updatedAt = Instant.now()
                )
            )
            affected++
        }
        return BatchResult(affected)
    }

    suspend fun delete(taskIds: List<Long>): BatchResult {
        var affected = 0
        taskIds.forEach { id ->
            taskRepository.deleteTask(id)
            affected++
        }
        return BatchResult(affected)
    }

    suspend fun moveToProject(taskIds: List<Long>, projectId: Long?): BatchResult {
        var affected = 0
        taskIds.forEach { id ->
            val task = taskRepository.getTaskById(id) ?: return@forEach
            taskRepository.updateTask(
                task.copy(
                    projectId = projectId,
                    updatedAt = Instant.now()
                )
            )
            affected++
        }
        return BatchResult(affected)
    }

    /**
     * Set priority for multiple tasks.
     */
    suspend fun setPriority(taskIds: List<Long>, priority: Priority): BatchResult {
        var affected = 0
        taskIds.forEach { id ->
            val task = taskRepository.getTaskById(id) ?: return@forEach
            taskRepository.updateTask(
                task.copy(
                    priority = priority,
                    updatedAt = Instant.now()
                )
            )
            affected++
        }
        return BatchResult(affected)
    }

    /**
     * Move multiple tasks to a specific date (deadline).
     * If date is null, clears the deadline.
     */
    suspend fun moveToDate(taskIds: List<Long>, date: LocalDate?): BatchResult {
        var affected = 0
        taskIds.forEach { id ->
            val task = taskRepository.getTaskById(id) ?: return@forEach
            val newDeadline = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
            taskRepository.updateTask(
                task.copy(
                    deadline = newDeadline,
                    updatedAt = Instant.now()
                )
            )
            affected++
        }
        return BatchResult(affected)
    }

}
