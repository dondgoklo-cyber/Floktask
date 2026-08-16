package com.taskmanager.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * A recurring habit. Completion is tracked via [HabitCompletion] entries.
 * When linked to a [taskId] (optional), completing the habit can complete a task.
 */
data class Habit(
    val id: Long? = null,
    val title: String,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetDaysOfWeek: Set<DayOfWeek> = emptySet(),
    val linkedTaskId: Long? = null,
    val color: String? = null,
    val createdAt: LocalDate = LocalDate.now()
)

enum class HabitFrequency { DAILY, WEEKLY, WEEKDAYS, CUSTOM }

data class HabitCompletion(
    val id: Long? = null,
    val habitId: Long,
    val date: LocalDate
)

data class HabitStreak(
    val habitId: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val completions: List<LocalDate>
)
