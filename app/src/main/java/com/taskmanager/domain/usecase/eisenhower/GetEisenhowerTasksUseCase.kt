package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

/**
 * Авто-распределение задач по квадрантам Эйзенхауэра на основе:
 * - importance (priority): HIGH/IMPORTANT → важная
 * - urgency (deadline): просрочено или срок сегодня/завтра → срочная
 *
 * Квадранты:
 * 1. DO_NOW       — важно + срочно
 * 2. SCHEDULE     — важно, не срочно
 * 3. DELEGATE     — не важно, срочно
 * 4. ELIMINATE    — не важно, не срочно
 *
 * Если у задачи явно указан eisenhowerQuadrant — используется он (ручное переопределение).
 */
class GetEisenhowerTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<Map<EisenhowerQuadrant, List<Task>>> =
        taskRepository.getAllTasks().map { tasks ->
            val now = Instant.now()
            val dayMillis = 24 * 60 * 60 * 1000L
            tasks.filter { !it.isCompleted }.groupBy { task ->
                task.eisenhowerQuadrant ?: autoQuadrant(task, now, dayMillis)
            }
        }

    private fun autoQuadrant(task: Task, now: Instant, dayMillis: Long): EisenhowerQuadrant {
        val isImportant = task.priority == Priority.HIGH || task.priority == Priority.MEDIUM
        val deadline = task.deadline
        val isUrgent = deadline != null && deadline.toEpochMilli() - now.toEpochMilli() < dayMillis
        return when {
            isImportant && isUrgent -> EisenhowerQuadrant.DO_NOW
            isImportant && !isUrgent -> EisenhowerQuadrant.SCHEDULE
            !isImportant && isUrgent -> EisenhowerQuadrant.DELEGATE
            else -> EisenhowerQuadrant.ELIMINATE
        }
    }
}
