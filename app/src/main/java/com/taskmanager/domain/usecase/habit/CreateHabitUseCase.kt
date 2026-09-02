package com.taskmanager.domain.usecase.habit

import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import javax.inject.Inject
import android.util.Log

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Long = runCatching {
        
    }.onFailure { e ->
        Log.e("CreateHabitUseCase", "Error in invoke", e)
    }
        habitRepository.createHabit(habit)
}
