package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.model.HabitLog
import com.taskmanager.domain.repository.HabitLogRepository
import java.time.LocalDate
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

data class HabitStats(
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate: Float,
    val totalCompletions: Int,
    val last30Days: Map<LocalDate, Int>
)

class GetHabitStatsUseCase @Inject constructor( 
    private val logger: Logger,
    private val habitLogRepository: HabitLogRepository
) {
    suspend operator fun invoke(habitId: Long): HabitStats {
        val logs = runCatching {
            habitLogRepository.getByHabit(habitId)
        }.onFailure { e ->
            logger.error("GetHabitStatsUseCase", "Error in invoke", e)
        }.getOrThrow()
        
        val logsByDate: Map<LocalDate, Int> = logs.groupBy { it.date }.mapValues { (_, values) ->
            values.sumOf { it.count }
        }
        val today = LocalDate.now()

        // Текущая серия (идём назад от сегодня)
        var currentStreak = 0
        var date = today
        while (logsByDate.containsKey(date)) {
            currentStreak++
            date = date.minusDays(1)
        }

        // Лучшая серия
        val sortedDates = logsByDate.keys.sorted()
        var bestStreak = 0
        var tempStreak = 0
        var prevDate: LocalDate? = null
        for (d in sortedDates) {
            if (prevDate != null && d == prevDate.plusDays(1)) {
                tempStreak++
            } else {
                tempStreak = 1
            }
            if (tempStreak > bestStreak) bestStreak = tempStreak
            prevDate = d
        }

        // Процент выполнения за последние 30 дней
        val last30 = (0 until 30).map { today.minusDays(it.toLong()) }
        val completedIn30 = last30.count { logsByDate.containsKey(it) }
        val completionRate = completedIn30.toFloat() / 30f

        // Последние 30 дней с количеством
        val last30Map = last30.associateWith { logsByDate[it] ?: 0 }

        return HabitStats(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            completionRate = completionRate,
            totalCompletions = logs.size,
            last30Days = last30Map
        )
    }
}
