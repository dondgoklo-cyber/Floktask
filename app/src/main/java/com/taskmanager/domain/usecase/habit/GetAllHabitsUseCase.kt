package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all habits.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<Habit>> = habitRepository.getAllHabits()
}
