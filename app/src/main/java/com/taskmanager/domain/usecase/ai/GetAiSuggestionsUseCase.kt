package com.taskmanager.domain.usecase.ai

import com.taskmanager.domain.ai.TaskPrioritizer
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

data class AiSuggestion(
    val task: Task,
    val score: Int,
    val suggestedPriority: com.taskmanager.domain.model.Priority
)

class GetAiSuggestionsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<AiSuggestion>> {
        val now = Instant.now()
        return taskRepository.getIncompleteTasks().map { tasks ->
            TaskPrioritizer.rank(tasks, now).map { task ->
                AiSuggestion(
                    task = task,
                    score = TaskPrioritizer.score(task, now),
                    suggestedPriority = TaskPrioritizer.prioritize(task, now)
                )
            }
        }
    }
}
