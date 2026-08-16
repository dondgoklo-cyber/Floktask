package com.taskmanager.domain.usecase.analytics

import com.taskmanager.domain.model.DayCount
import com.taskmanager.domain.model.ProductivityStats
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetProductivityStatsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    private val zone: ZoneId = ZoneId.systemDefault()

    operator fun invoke(): Flow<ProductivityStats> =
        taskRepository.getCompletedTasks()
            .map { tasks -> compute(tasks) }

    fun compute(tasks: List<Task>): ProductivityStats {
        val completed = tasks.filter { it.isCompleted }
        val byDay = completed
            .groupBy { it.updatedAt.atZone(zone).toLocalDate() }
            .mapValues { it.value.size }

        val today = LocalDate.now(zone)
        val windowStart = today.minusDays(HEATMAP_WINDOW_DAYS - 1L)
        val heatmap = (0 until HEATMAP_WINDOW_DAYS)
            .map { windowStart.plusDays(it.toLong()) }
            .associateWith { byDay[it] ?: 0 }

        val weekly = (0 until 7)
            .map { today.minusDays((6 - it).toLong()) }
            .map { date -> DayCount(date, byDay[date] ?: 0) }

        val currentStreak = computeCurrentStreak(byDay, today)
        val longestStreak = computeLongestStreak(byDay)

        return ProductivityStats(
            dailyCompletion = heatmap,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalCompleted = completed.size,
            deepWorkHours = 0.0, // populated when Pomodoro sessions are available (issue 11)
            weeklyReport = weekly
        )
    }

    /**
     * Consecutive days ending today (or yesterday if today has none) with >= 1 completion.
     */
    private fun computeCurrentStreak(byDay: Map<LocalDate, Int>, today: LocalDate): Int {
        var cursor = today
        if ((byDay[cursor] ?: 0) == 0) {
            cursor = today.minusDays(1)
            if ((byDay[cursor] ?: 0) == 0) return 0
        }
        var streak = 0
        while ((byDay[cursor] ?: 0) > 0) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    /**
     * Longest run of consecutive days with >= 1 completion across all history.
     */
    private fun computeLongestStreak(byDay: Map<LocalDate, Int>): Int {
        if (byDay.isEmpty()) return 0
        val days = byDay.keys.sorted()
        var best = 1
        var current = 1
        for (i in 1 until days.size) {
            if (days[i] == days[i - 1].plusDays(1)) {
                current++
                if (current > best) best = current
            } else {
                current = 1
            }
        }
        return best
    }

    companion object {
        const val HEATMAP_WINDOW_DAYS = 90
    }
}
