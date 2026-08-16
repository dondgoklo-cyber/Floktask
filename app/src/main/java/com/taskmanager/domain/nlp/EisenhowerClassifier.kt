package com.taskmanager.domain.nlp

import com.taskmanager.domain.model.EisenhowerClassification
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/**
 * Classifies a [Task] into an Eisenhower matrix quadrant.
 *
 * Urgency: a task is urgent if its deadline is today, overdue, or within
 * the next [URGENT_WINDOW_HOURS] hours.
 * Importance: HIGH priority is important; everything else is not.
 */
class EisenhowerClassifier(
    private val zone: ZoneId = ZoneId.systemDefault(),
    private val nowProvider: () -> Instant = { Instant.now() }
) {

    fun classify(task: Task): EisenhowerClassification {
        val important = isImportant(task)
        val urgent = isUrgent(task)
        val quadrant = when {
            urgent && important -> EisenhowerQuadrant.Q1
            !urgent && important -> EisenhowerQuadrant.Q2
            urgent && !important -> EisenhowerQuadrant.Q3
            else -> EisenhowerQuadrant.Q4
        }
        return EisenhowerClassification(quadrant, urgent, important)
    }

    private fun isImportant(task: Task): Boolean = task.priority == Priority.HIGH

    private fun isUrgent(task: Task): Boolean {
        val deadline = task.deadline ?: return false
        if (task.isCompleted) return false
        val now = nowProvider()
        // Overdue or due within the urgent window.
        val horizon = now.plus(Duration.ofHours(URGENT_WINDOW_HOURS.toLong()))
        return !deadline.isAfter(horizon)
    }

    companion object {
        const val URGENT_WINDOW_HOURS = 24
    }
}
