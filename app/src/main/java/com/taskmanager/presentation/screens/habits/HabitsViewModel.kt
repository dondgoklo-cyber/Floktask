package com.taskmanager.presentation.screens.habits

import androidx.lifecycle.ViewModel
import com.taskmanager.domain.logger.Logger
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.usecase.habit.CreateHabitUseCase
import com.taskmanager.domain.usecase.habit.GetActiveHabitsUseCase
import com.taskmanager.domain.usecase.habit.GetHabitStatsUseCase
import com.taskmanager.domain.usecase.habit.LogHabitCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HabitWithCompletion(
    val habit: Habit,
    val completedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int
)

data class HabitsUiState(
    val habits: List<HabitWithCompletion> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false
)

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val getActiveHabitsUseCase: GetActiveHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val logHabitCompletionUseCase: LogHabitCompletionUseCase,
    private val getHabitStatsUseCase: GetHabitStatsUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    // Trigger to force state recomputation when habit logs change
    // (the habits table doesn't change on toggle, only habit_logs does)
    private val _refreshTrigger = MutableStateFlow(0)

    val state: StateFlow<HabitsUiState> = combine(
        getActiveHabitsUseCase(),
        _refreshTrigger
    ) { habits, _ ->
        val today = LocalDate.now()
        val withCompletion = habits.map { habit ->
            val stats = getHabitStatsUseCase(habit.id ?: 0)
            HabitWithCompletion(
                habit = habit,
                completedToday = stats.last30Days.containsKey(today),
                currentStreak = stats.currentStreak,
                bestStreak = stats.bestStreak
            )
        }
        HabitsUiState(habits = withCompletion, isLoading = false)
    }
    .stateIn(viewModelScope, SharingStarted.Lazily, HabitsUiState(isLoading = true))

    fun openCreateDialog() { _showCreateDialog.value = true }
    fun closeCreateDialog() { _showCreateDialog.value = false }

    fun createHabit(name: String, color: String?, frequency: String) {
        viewModelScope.launch {
            try {
                val habit = Habit(
                    name = name.trim(),
                    color = color,
                    frequency = com.taskmanager.domain.model.HabitFrequency.valueOf(frequency)
                )
                createHabitUseCase(habit)
                closeCreateDialog()
            } catch (e: Exception) {
                logger.error("HabitsViewModel", "Error creating habit", e)
            }
        }
    }

    fun toggleCompletion(habitId: Long) {
        viewModelScope.launch {
            try {
                logHabitCompletionUseCase.toggleCompletion(habitId)
                // Force state recomputation so the UI updates immediately
                _refreshTrigger.value++
            } catch (e: Exception) {
                logger.error("HabitsViewModel", "Error toggling completion", e)
            }
        }
    }
}