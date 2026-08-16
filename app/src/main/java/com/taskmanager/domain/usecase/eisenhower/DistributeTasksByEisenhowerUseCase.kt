package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.nlp.EisenhowerClassifier
import javax.inject.Inject

/**
 * Buckets a list of incomplete tasks into the four Eisenhower quadrants,
 * ordered so the most actionable tasks come first within each quadrant
 * (Q1 by deadline asc, Q2 by deadline asc, etc.).
 */
class DistributeTasksByEisenhowerUseCase @Inject constructor(
    private val classifier: EisenhowerClassifier
) {
    operator fun invoke(tasks: List<Task>): Map<EisenhowerQuadrant, List<Task>> {
        val incomplete = tasks.filterNot { it.isCompleted }
        return EisenhowerQuadrant.entries.associateWith { q ->
            incomplete
                .filter { classifier.classify(it).quadrant == q }
                .sortedWith(compareBy(nullsLast()) { it.deadline })
        }
    }
}
