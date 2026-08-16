package com.taskmanager.domain.model

import java.time.LocalDate

/**
 * Productivity analytics snapshot.
 *
 * @param dailyCompletion map of date -> count of completed tasks (for the heatmap)
 * @param currentStreak consecutive days (ending today or yesterday) with >= 1 completion
 * @param longestStreak longest consecutive-day run ever
 * @param totalCompleted total completed tasks in the observed window
 * @param deepWorkHours total estimated deep-work hours (sum of task durations, when available)
 * @param weeklyReport per-weekday completion counts for the last 7 days (Mon..Sun order)
 */
data class ProductivityStats(
    val dailyCompletion: Map<LocalDate, Int> = emptyMap(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompleted: Int = 0,
    val deepWorkHours: Double = 0.0,
    val weeklyReport: List<DayCount> = emptyList()
)

data class DayCount(
    val date: LocalDate,
    val completed: Int
)
