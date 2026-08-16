package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.model.HabitCompletion
import com.taskmanager.domain.model.HabitFrequency
import com.taskmanager.domain.model.HabitStreak
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Computes current/longest streaks for a habit from its completion dates,
 * and whether the habit is due today (issue 13: habit completion influences
 * streaks and can generate tasks).
 */
class GetHabitStreakUseCase @Inject constructor() {

    operator fun invoke(habit: Habit, completions: List<HabitCompletion>): HabitStreak {
        val dates = completions.map { it.date }.sorted().distinct()
        val current = computeCurrentStreak(dates)
        val longest = computeLongestStreak(dates)
        return HabitStreak(
            habitId = habit.id ?: 0,
            currentStreak = current,
            longestStreak = longest,
            completions = dates
        )
    }

    /**
     * Whether the habit should be completed on [date] based on its frequency.
     */
    fun isDueToday(habit: Habit, date: LocalDate = LocalDate.now()): Boolean = when (habit.frequency) {
        HabitFrequency.DAILY -> true
        HabitFrequency.WEEKDAYS -> date.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        HabitFrequency.WEEKLY -> true
        HabitFrequency.CUSTOM -> habit.targetDaysOfWeek.isEmpty() || date.dayOfWeek in habit.targetDaysOfWeek
    }

    private fun computeCurrentStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val today = LocalDate.now()
        val set = dates.toSet()
        // Allow today or yesterday as the streak start (grace for "not done yet today").
        var cursor = today
        if (today !in set) {
            cursor = today.minusDays(1)
            if (cursor !in set) return 0
        }
        var streak = 0
        while (cursor in set) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun computeLongestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        var best = 1
        var current = 1
        for (i in 1 until dates.size) {
            if (dates[i] == dates[i - 1].plusDays(1)) {
                current++
                if (current > best) best = current
            } else {
                current = 1
            }
        }
        return best
    }
}
