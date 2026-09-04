package com.taskmanager.presentation
import com.taskmanager.domain.logger.Logger.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
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

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _viewMode = MutableStateFlow(CalendarViewMode.DAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val zone = ZoneId.of("UTC")

    val state: StateFlow<CalendarUiState> = combine(
        _viewMode,
        _selectedDate,
        taskRepository.getAllTasks()
    ) { viewMode, selectedDate, allTasks ->
        // Группируем задачи по дате (deadline или startTime)
        val byDay = allTasks
            .filter { it.deadline != null || it.startTime != null }
            .groupBy { task ->
                task.startTime?.atZone(zone)?.toLocalDate()
                    ?: task.deadline?.atZone(zone)?.toLocalDate()
                    ?: selectedDate
            }
            .mapValues { (_, tasks) ->
                tasks.sortedWith(compareBy(nullsLast()) { it.startTime })
            }
        CalendarUiState(
            viewMode = viewMode,
            selectedDate = selectedDate,
            timedTasks = byDay,
            allTasks = allTasks,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, CalendarUiState(isLoading = true))

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
     */
    fun updateTaskSchedule(task: Task, newStartTime: java.time.Instant, newDurationMinutes: Long?) {
        viewModelScope.launch {
        try {
            updateTaskUseCase(
                task.copy(
                    startTime = newStartTime,
                    deadline = newStartTime,
                    durationMinutes = newDurationMinutes ?: task.durationMinutes
                )
            )
        } catch (e: Exception) {
            logger.error("CalendarViewModel", "Error in launch block", e)
            // Optionally update state to show error
        }
    }
    }

    /**
     * Переносит задачу на другой день (без изменения времени).
     */
    fun moveTaskToDate(task: Task, newDate: LocalDate) {
        viewModelScope.launch {
        try {
            val currentTime = task.startTime?.atZone(zone)?.toLocalTime() ?: java.time.LocalTime.NOON
            val newInstant = newDate.atTime(currentTime).atZone(zone).toInstant()
            updateTaskUseCase(
                task.copy(
                    startTime = newInstant,
                    deadline = newInstant
                )
            )
        } catch (e: Exception) {
            logger.error("CalendarViewModel", "Error in launch block", e)
            // Optionally update state to show error
        }
    }
    }
}
