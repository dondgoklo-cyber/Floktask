package com.taskmanager.presentation.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.HabitLogRepository
import com.taskmanager.domain.repository.HabitRepository
import com.taskmanager.domain.repository.PomodoroSessionRepository
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.usecase.note.GetAllNotesUseCase
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.domain.usecase.finance.GetRecentTransactionsUseCase
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.TaskRepository
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
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class TodayUiState(
    val tasksForToday: List<Task> = emptyList(),
    val nextTasks: List<Task> = emptyList(),
    val completedToday: Int = 0,
    val totalToday: Int = 0,
    val overdueCount: Int = 0,
    val progress: Float = 0f,
    val focusMinutesToday: Int = 0,
    val habitsToday: List<Habit> = emptyList(),
    val habitsCompleted: Int = 0,
    val habitsTotal: Int = 0,
    val projects: List<Project> = emptyList(),
    val financeBalance: Double = 0.0,
    val financeIncome: Double = 0.0,
    val financeExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val recentNotes: List<Note> = emptyList(),
    val inboxTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val habitRepository: HabitRepository,
    private val habitLogRepository: HabitLogRepository,
    private val pomodoroSessionRepository: PomodoroSessionRepository,
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase
) : ViewModel() {

    private val zone = ZoneId.of("UTC")

    private val _state = MutableStateFlow(TodayUiState(isLoading = true))
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    private val _inboxTasks = MutableStateFlow<List<Task>>(emptyList())

    init {
        observeTodayData()
        viewModelScope.launch {
            taskRepository.getInboxTasks().collect { _inboxTasks.value = it.take(3) }
        }
    }

    private fun observeTodayData() {
        val today = LocalDate.now()
        val dayStart = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val dashboardDataFlow = combine(
            getFinanceSummaryUseCase.totalIncome(),
            getFinanceSummaryUseCase.totalExpense(),
            getRecentTransactionsUseCase(3),
            getAllNotesUseCase()
        ) { income, expense, recentTx, notes ->
            DashboardFinanceData(income, expense, recentTx, notes.take(3))
        }
        combine(
            taskRepository.getTasksForDay(dayStart, dayEnd),
            pomodoroSessionRepository.getSessionsForDay(dayStart, dayEnd),
            habitRepository.getActiveHabits(),
            projectRepository.getActiveProjects(),
            dashboardDataFlow
        ) { tasks, pomodoros, habits, projects, data ->
            val now = System.currentTimeMillis()
            val completedToday = tasks.count { it.isCompleted }
            val totalToday = tasks.size
            val overdue = tasks.count { !it.isCompleted && it.deadline != null && it.deadline.toEpochMilli() < now }

            // Ближайшие невыполненные задачи со временем
            val nextTasks = tasks
                .filter { !it.isCompleted && it.startTime != null && it.startTime.toEpochMilli() >= now }
                .sortedBy { it.startTime }
                .take(4)

            val focusMinutes = pomodoros
                .filter { it.isCompleted }
                .sumOf { it.durationMinutes }

            // Привычки: проверяем выполнение за сегодня
            val habitStates = habits.map { habit ->
                val log = habitLogRepository.getForDay(habit.id ?: 0, today)
                habit to (log != null)
            }
            val habitsCompleted = habitStates.count { it.second }
            val habitsTotal = habitStates.size

            TodayUiState(
                tasksForToday = tasks,
                nextTasks = nextTasks,
                completedToday = completedToday,
                totalToday = totalToday,
                overdueCount = overdue,
                progress = if (totalToday == 0) 0f else completedToday.toFloat() / totalToday,
                focusMinutesToday = focusMinutes,
                habitsToday = habits,
                habitsCompleted = habitsCompleted,
                habitsTotal = habitsTotal,
                projects = projects,
                financeBalance = data.income - data.expense,
                financeIncome = data.income,
                financeExpense = data.expense,
                recentTransactions = data.recentTx,
                recentNotes = data.recentNotes,
                inboxTasks = _inboxTasks.value,
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, TodayUiState(isLoading = true))
            .let { flow ->
                viewModelScope.launch {
                    flow.collect { _state.value = it }
                }
            }
    }
}

private data class DashboardFinanceData(
    val income: Double,
    val expense: Double,
    val recentTx: List<Transaction>,
    val recentNotes: List<Note>
)
