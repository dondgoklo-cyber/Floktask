package com.taskmanager.domain.ai

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import java.time.Duration
import java.time.Instant

/**
 * Heuristic auto-prioritization engine.
 *
 * Scores tasks 0..100 based on deadline urgency, age, title urgency
 * keywords, and whether the task is already completed. The score maps
 * to a Priority bucket.
 */
object TaskPrioritizer {

    private val urgentKeywords = listOf(
        "urgent", "asap", "deadline", "today", "tomorrow", "now", "critical",
        "important", "must", "exam", "defense", "invoice", "payment"
    )

    private const val URGENT_KEYWORD_BONUS = 30
    private const val NEAR_DEADLINE_BONUS = 40
    private const val OLD_TASK_BONUS = 15

    fun score(task: Task, now: Instant = Instant.now()): Int {
        if (task.isCompleted) return 0
        var score = 50

        task.deadline?.let { deadline ->
            val hoursLeft = Duration.between(now, deadline).toHours()
            score += when {
                hoursLeft < 0 -> NEAR_DEADLINE_BONUS   // overdue
                hoursLeft <= 24 -> NEAR_DEADLINE_BONUS
                hoursLeft <= 72 -> NEAR_DEADLINE_BONUS / 2
                else -> 0
            }
        }

        val ageHours = Duration.between(task.createdAt, now).toHours()
        if (ageHours >= 72) score += OLD_TASK_BONUS

        val text = (task.title + " " + (task.description ?: "")).lowercase()
        if (urgentKeywords.any { it in text }) score += URGENT_KEYWORD_BONUS

        return score.coerceIn(0, 100)
    }

    fun prioritize(task: Task, now: Instant = Instant.now()): Priority {
        val s = score(task, now)
        return when {
            s >= 80 -> Priority.HIGH
            s >= 60 -> Priority.MEDIUM
            s >= 40 -> Priority.LOW
            else -> Priority.NONE
        }
    }

    /** Rank tasks by score descending (highest priority first). */
    fun rank(tasks: List<Task>, now: Instant = Instant.now()): List<Task> =
        tasks.sortedByDescending { score(it, now) }
}
