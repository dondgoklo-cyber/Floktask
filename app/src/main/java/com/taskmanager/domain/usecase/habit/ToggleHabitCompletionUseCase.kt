package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.repository.HabitLogRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for toggling habit completion for today.
 * Creates or deletes habit log entry for the current day.
 */
class ToggleHabitCompletionUseCase @Inject constructor(
    private val habitLogRepository: HabitLogRepository
) {
    
    /**
     * Toggle habit completion for today
     * @param habitId The ID of the habit
     * @return The new completion status
     */
    suspend operator fun invoke(habitId: Long): Boolean {
        val today = LocalDate.now()
        val existingLog = habitLogRepository.getForDay(habitId, today)
        
        if (existingLog != null) {
            // Habit is already completed today - mark as incomplete
            habitLogRepository.delete(existingLog.id ?: 0)
            return false
        } else {
            // Habit is not completed today - mark as complete
            habitLogRepository.create(habitId, today)
            return true
        }
    }
}
