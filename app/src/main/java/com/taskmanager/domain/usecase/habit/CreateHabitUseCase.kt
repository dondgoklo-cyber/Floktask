package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(habit: Habit): Long = runCatching {
        habitRepository.createHabit(habit)
    }.onFailure { e ->
        logger.error("CreateHabitUseCase", "Error in invoke", e)
    }.getOrThrow()
}
