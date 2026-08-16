package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Long =
        habitRepository.createHabit(habit)
}
