package com.taskmanager.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class InsightsUiState(
    val tasksCompletedToday: Int = 0,
    val tasksCompletedWeek: Int = 0,
    val tasksTotal: Int = 0,
    val pomodoroStats: PomodoroStats = PomodoroStats(0, 0, 0, 0, 0, 0),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getPomodoroStatsUseCase: GetPomodoroStatsUseCase,
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val logger: Logger
) : ViewModel() {

    private val zone = ZoneId.of("UTC")

    val state: StateFlow<InsightsUiState> = combine(
        getAllTasksUseCase(),
        getPomodoroStatsUseCase(),
        getFinanceSummaryUseCase().totalIncome(),
        getFinanceSummaryUseCase().totalExpense()
    ) { tasks, pomoStats, income, expense ->
        val today = LocalDate.now()
        val todayStart = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val weekStart = today.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()

        val completed = tasks.filter { it.isCompleted }
        val completedToday = completed.count {
            it.updatedAt.toEpochMilli() >= todayStart
        }
        val completedWeek = completed.count {
            it.updatedAt.toEpochMilli() >= weekStart
        }

        InsightsUiState(
            tasksCompletedToday = completedToday,
            tasksCompletedWeek = completedWeek,
            tasksTotal = completed.size,
            pomodoroStats = pomoStats,
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, InsightsUiState(isLoading = true))
}
