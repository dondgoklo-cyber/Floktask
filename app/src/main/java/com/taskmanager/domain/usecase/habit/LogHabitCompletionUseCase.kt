package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.repository.HabitLogRepository
import java.time.LocalDate
import javax.inject.Inject
import android.util.Log

class LogHabitCompletionUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate = LocalDate.now(), count: Int = runCatching {
        1): Long =
    }.onFailure { e ->
        Log.e("LogHabitCompletionUseCase", "Error in invoke", e)
    }
        habitLogRepository.logCompletion(habitId, date, count)

    suspend fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        val existing = habitLogRepository.getForDay(habitId, date)
        if (existing != null) {
            habitLogRepository.removeForDay(habitId, date)
        } else {
            habitLogRepository.logCompletion(habitId, date)
        }
    }
}
