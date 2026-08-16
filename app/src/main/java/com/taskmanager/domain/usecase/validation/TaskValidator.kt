package com.taskmanager.domain.usecase.validation

import com.taskmanager.domain.model.Task
import java.time.Instant
import javax.inject.Inject

/**
 * Outcome of validating a task.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

/**
 * Validates a [Task] before it is persisted (issue 23: use cases accepted
 * any data without checks). Returns the first failure, or [ValidationResult.Valid].
 */
class TaskValidator @Inject constructor() {

    fun validate(task: Task, now: Instant = Instant.now()): ValidationResult {
        val title = task.title.trim()
        if (title.isEmpty()) return ValidationResult.Invalid("Title must not be empty")
        if (title.length > MAX_TITLE_LENGTH) {
            return ValidationResult.Invalid("Title must be at most $MAX_TITLE_LENGTH characters")
        }
        task.description?.let { d ->
            if (d.length > MAX_DESCRIPTION_LENGTH) {
                return ValidationResult.Invalid("Description must be at most $MAX_DESCRIPTION_LENGTH characters")
            }
        }
        // Deadline must not be in the past (allow a small grace window of 1 minute).
        task.deadline?.let { deadline ->
            if (deadline.isBefore(now.minusSeconds(60))) {
                return ValidationResult.Invalid("Deadline cannot be in the past")
            }
        }
        return ValidationResult.Valid
    }

    /**
     * Detects a circular dependency in a directed graph defined by [edges]
     * (from -> to). Returns true if a cycle exists.
     */
    fun hasCircularDependency(edges: Map<Long, List<Long>>): Boolean {
        val visited = mutableMapOf<Long, VisitState>()
        for (node in edges.keys) {
            if (detectCycle(node, edges, visited)) return true
        }
        return false
    }

    private enum class VisitState { VISITING, DONE }

    private fun detectCycle(
        node: Long,
        edges: Map<Long, List<Long>>,
        visited: MutableMap<Long, VisitState>
    ): Boolean {
        when (visited[node]) {
            VisitState.VISITING -> return true
            VisitState.DONE -> return false
            else -> {}
        }
        visited[node] = VisitState.VISITING
        edges[node]?.forEach { neighbor ->
            if (detectCycle(neighbor, edges, visited)) return true
        }
        visited[node] = VisitState.DONE
        return false
    }

    companion object {
        const val MAX_TITLE_LENGTH = 200
        const val MAX_DESCRIPTION_LENGTH = 4000
    }
}
