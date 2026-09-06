package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.HabitLogRepository
import java.time.LocalDate
import javax.inject.Inject

class LogHabitCompletionUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate = LocalDate.now(), count: Int = 1): Long =
        runCatching {
            habitLogRepository.logCompletion(habitId, date, count)
        }.onFailure { e ->
            logger.error("LogHabitCompletionUseCase", "Error in invoke", e)
        }.getOrThrow()

    suspend fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        val existing = habitLogRepository.getForDay(habitId, date)
        if (existing != null) {
            habitLogRepository.removeForDay(habitId, date)
        } else {
            habitLogRepository.logCompletion(habitId, date, 1)
        }
    }
}
