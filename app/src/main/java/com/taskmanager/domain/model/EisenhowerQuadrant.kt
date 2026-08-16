package com.taskmanager.domain.model

/**
 * Eisenhower matrix quadrants.
 *
 * - [Q1]: urgent + important  → "Do now"
 * - [Q2]: not urgent + important → "Schedule"
 * - [Q3]: urgent + not important → "Delegate / batch"
 * - [Q4]: not urgent + not important → "Eliminate / later"
 */
enum class EisenhowerQuadrant(val code: String) {
    Q1("Q1"),
    Q2("Q2"),
    Q3("Q3"),
    Q4("Q4")
}

data class EisenhowerClassification(
    val quadrant: EisenhowerQuadrant,
    val isUrgent: Boolean,
    val isImportant: Boolean
)
