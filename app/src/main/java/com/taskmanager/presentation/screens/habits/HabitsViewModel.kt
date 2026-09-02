package com.taskmanager.presentation.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.usecase.habit.CreateHabitUseCase
import com.taskmanager.domain.usecase.habit.GetActiveHabitsUseCase
import com.taskmanager.domain.usecase.habit.GetHabitStatsUseCase
import com.taskmanager.domain.usecase.habit.ToggleHabitCompletionUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val getHabitStatsUseCase: GetHabitStatsUseCase,
    val hapticManager: HapticManager
) : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    /**
     * Handle FAB long-press for Habits screen
     * Quick add new habit
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
        openCreateDialog()
    }

    val state: StateFlow<HabitsUiState> = getActiveHabitsUseCase()
        .map { habits ->
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
            val habit = Habit(
                name = name.trim(),
                color = color,
                frequency = com.taskmanager.domain.model.HabitFrequency.valueOf(frequency)
            )
            createHabitUseCase(habit)
            closeCreateDialog()
        }
    }

    fun toggleCompletion(habitId: Long) {
        viewModelScope.launch {
            toggleHabitCompletionUseCase(habitId)
        }
    }
}
