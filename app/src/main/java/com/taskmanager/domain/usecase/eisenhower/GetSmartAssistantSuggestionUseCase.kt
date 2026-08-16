package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.nlp.EisenhowerClassifier
import java.time.Instant
import javax.inject.Inject

/**
 * "Smart Assistant" suggestion: picks the next task the user should focus on,
 * and returns a human-readable nudge ("Сделай Q1 задачи сейчас").
 *
 * Priority order: Q1 (do now) → Q3 (urgent, quick wins) → Q2 (schedule) → Q4.
 */
class GetSmartAssistantSuggestionUseCase @Inject constructor(
    private val classifier: EisenhowerClassifier
) {

    operator fun invoke(tasks: List<Task>): Suggestion {
        val incomplete = tasks.filterNot { it.isCompleted }
        val byQuadrant = EisenhowerQuadrant.entries.associateWith { q ->
            incomplete.filter { classifier.classify(it).quadrant == q }
        }

        val next = PRIORITY_ORDER
            .firstOrNull { q -> byQuadrant[q].orEmpty().isNotEmpty() }
            ?.let { q -> byQuadrant[q].orEmpty().minByOrNull { it.deadline ?: Instant.MAX } }

        val message = when (next?.let { classifier.classify(it).quadrant }) {
            EisenhowerQuadrant.Q1 -> "Сделай Q1 задачи сейчас — горит!"
            EisenhowerQuadrant.Q3 -> "Быстро разбери срочные мелочи (Q3), потом Q1/Q2."
            EisenhowerQuadrant.Q2 -> "Запланируй важные несрочные задачи (Q2) на ближайшие дни."
            EisenhowerQuadrant.Q4 -> "Q4 пуст или неважен — можно отложить или удалить."
            null -> "Все задачи выполнены. Отличная работа!"
        }

        return Suggestion(
            nextTask = next,
            message = message,
            counts = byQuadrant.mapValues { it.value.size }
        )
    }

    private companion object {
        val PRIORITY_ORDER = listOf(
            EisenhowerQuadrant.Q1,
            EisenhowerQuadrant.Q3,
            EisenhowerQuadrant.Q2,
            EisenhowerQuadrant.Q4
        )
    }
}

data class Suggestion(
    val nextTask: Task?,
    val message: String,
    val counts: Map<EisenhowerQuadrant, Int>
)
