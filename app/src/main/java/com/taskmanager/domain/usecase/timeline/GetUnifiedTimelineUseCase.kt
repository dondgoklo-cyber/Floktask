package com.taskmanager.domain.usecase.timeline

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TimelineEntry
import com.taskmanager.domain.model.TimelineSource
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Builds a unified timeline by merging tasks (by deadline) into a single
 * ascending-by-start stream. Calendar events and time blocks can be merged in
 * later — this implementation currently maps [Task] deadlines to entries.
 */
class GetUnifiedTimelineUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(startEpochMillis: Long, endEpochMillis: Long): Flow<List<TimelineEntry>> =
        taskRepository.getTasksByDeadlineBetween(startEpochMillis, endEpochMillis)
            .map { tasks -> tasks.map { it.toTimelineEntry() } }

    private fun Task.toTimelineEntry(): TimelineEntry = TimelineEntry(
        id = "task-${id ?: 0}",
        title = title,
        start = deadline ?: updatedAt,
        end = null,
        source = TimelineSource.TASK_DEADLINE,
        taskId = id,
        color = color,
        isCompleted = isCompleted
    )

}
