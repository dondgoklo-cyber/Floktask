package com.taskmanager.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class CalendarViewMode { DAY, THREE_DAYS, WEEK, MONTH, AGENDA }

data class CalendarUiState(
    val viewMode: CalendarViewMode = CalendarViewMode.DAY,
    val selectedDate: LocalDate = LocalDate.now(),
    val timedTasks: Map<LocalDate, List<Task>> = emptyMap(),
    val allTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val updateTaskUseCase: UpdateTaskUseCase,
    val hapticManager: HapticManager
) : ViewModel() {

    private val _viewMode = MutableStateFlow(CalendarViewMode.DAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val zone = ZoneId.systemDefault()

    val state: StateFlow<CalendarUiState> = combine(_viewMode, _selectedDate) { mode, date ->
        viewRange(mode, date)
    }.flatMapLatest { (rangeStart, rangeEnd) ->
        // Загружаем только задачи видимого диапазона, а не весь список.
        // PERFORMANCE: ранее getAllTasks() загружал все задачи и фильтровал в Kotlin.
        taskRepository.getTasksForRange(rangeStart, rangeEnd).map { tasks ->
            CalendarUiState(
                viewMode = _viewMode.value,
                selectedDate = _selectedDate.value,
                timedTasks = groupByDay(tasks),
                isLoading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, CalendarUiState(isLoading = true))

    /** Возвращает [start, end) эпох-миллис для текущего view mode. */
    private fun viewRange(mode: CalendarViewMode, date: LocalDate): Pair<Long, Long> {
        val (start, end) = when (mode) {
            CalendarViewMode.DAY -> date to date.plusDays(1)
            CalendarViewMode.THREE_DAYS -> date to date.plusDays(3)
            CalendarViewMode.WEEK -> date.with(DayOfWeek.MONDAY) to date.with(DayOfWeek.MONDAY).plusWeeks(1)
            CalendarViewMode.MONTH -> date.withDayOfMonth(1) to date.withDayOfMonth(1).plusMonths(1)
            CalendarViewMode.AGENDA -> date to date.plusMonths(1)
        }
        val s = start.atStartOfDay(zone).toInstant().toEpochMilli()
        val e = end.atStartOfDay(zone).toInstant().toEpochMilli()
        return s to e
    }

    private fun groupByDay(tasks: List<Task>): Map<LocalDate, List<Task>> =
        tasks
            .filter { it.deadline != null || it.startTime != null }
            .groupBy { task ->
                task.startTime?.atZone(zone)?.toLocalDate()
                    ?: task.deadline?.atZone(zone)?.toLocalDate()
                    ?: _selectedDate.value
            }
            .mapValues { (_, ts) -> ts.sortedWith(compareBy(nullsLast()) { it.startTime }) }

    fun setViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun goToNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun goToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun goToNextWeek() {
        _selectedDate.value = _selectedDate.value.plusWeeks(1)
    }

    fun goToPreviousWeek() {
        _selectedDate.value = _selectedDate.value.minusWeeks(1)
    }

    /**
     * Обновляет время и длительность задачи (используется при drag&drop в time blocking).
     * TASK → CALENDAR: обновление Task автоматически отражается в календаре через Flow.
     * Важно: drag меняет только startTime/duration и НЕ должен затирать deadline —
     * ранее deadline приравнивался к startTime, что теряло срок задачи (BUG).
     */
    fun updateTaskSchedule(task: Task, newStartTime: java.time.Instant, newDurationMinutes: Long?) {
        viewModelScope.launch {
            updateTaskUseCase(
                task.copy(
                    startTime = newStartTime,
                    deadline = task.deadline,
                    durationMinutes = newDurationMinutes ?: task.durationMinutes
                )
            )
        }
    }

    /**
     * Переносит задачу на другой день (без изменения времени).
     * Сохраняет deadline, если он задан отдельно от startTime.
     */
    fun moveTaskToDate(task: Task, newDate: LocalDate) {
        viewModelScope.launch {
            val currentTime = task.startTime?.atZone(zone)?.toLocalTime() ?: java.time.LocalTime.NOON
            val newInstant = newDate.atTime(currentTime).atZone(zone).toInstant()
            updateTaskUseCase(
                task.copy(
                    startTime = newInstant,
                    deadline = task.deadline
                )
            )
        }
    }

    /**
     * Handle FAB long-press for Calendar screen
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
    }
}

