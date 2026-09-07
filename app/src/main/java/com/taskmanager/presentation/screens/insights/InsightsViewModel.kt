package com.taskmanager.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.usecase.gamification.ObserveUserStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InsightsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Float = 0f,
    val highPriorityTasks: Int = 0,
    val mediumPriorityTasks: Int = 0,
    val lowPriorityTasks: Int = 0,
    val pomodoroStats: PomodoroStats? = null,
    val userStats: UserStats? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    getAllTasksUseCase: GetAllTasksUseCase,
    getPomodoroStatsUseCase: GetPomodoroStatsUseCase,
    observeUserStatsUseCase: ObserveUserStatsUseCase,
    private val logger: Logger
) : ViewModel() {

    val state: StateFlow<InsightsUiState> = combine(
        getAllTasksUseCase(),
        getPomodoroStatsUseCase(),
        observeUserStatsUseCase()
    ) { tasks, pomodoroStats, userStats ->
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val now = java.time.Instant.now()
        val overdue = tasks.count { !it.isCompleted && it.deadline != null && it.deadline!!.isBefore(now) }

        InsightsUiState(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = total - completed,
            overdueTasks = overdue,
            completionRate = if (total > 0) completed.toFloat() / total else 0f,
            highPriorityTasks = tasks.count { it.priority == Priority.HIGH && !it.isCompleted },
            mediumPriorityTasks = tasks.count { it.priority == Priority.MEDIUM && !it.isCompleted },
            lowPriorityTasks = tasks.count { it.priority == Priority.LOW && !it.isCompleted },
            pomodoroStats = pomodoroStats,
            userStats = userStats,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, InsightsUiState())
}