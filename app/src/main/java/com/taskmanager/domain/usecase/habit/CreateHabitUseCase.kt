package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class CreateHabitUseCase @Inject constructor( 
    private val logger: Logger,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Long = runCatching {
        habitRepository.createHabit(habit)
    }.onFailure { e ->
        logger.error("CreateHabitUseCase", "Error in invoke", e)
    }.getOrThrow()
}
