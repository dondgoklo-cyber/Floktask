package com.taskmanager.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getAllTasksUseCase: GetAllTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CalendarState>(CalendarState.Loading)
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        getAllTasksUseCase()
            .map { tasks -> tasks.filter { it.deadline != null || it.startTime != null } }
            .map { tasks -> groupByDay(tasks) }
            .map { days -> CalendarState.Success(days) as CalendarState }
            .onEach { _state.value = it }
            .catch { cause -> _state.value = CalendarState.Error(cause.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    private fun groupByDay(tasks: List<Task>): List<TaskDay> {
        val zone = ZoneId.systemDefault()
        return tasks
            .flatMap { task ->
                val days = mutableSetOf<LocalDate>()
                task.deadline?.atZone(zone)?.toLocalDate()?.let { days.add(it) }
                task.startTime?.atZone(zone)?.toLocalDate()?.let { days.add(it) }
                days.map { it to task }
            }
            .groupBy({ it.first }, { it.second })
            .toSortedMap(compareBy { it })
            .map { (day, dayTasks) ->
                TaskDay(day, dayTasks.sortedWith(compareBy(nullsLast()) { it.startTime }))
            }
    }
}

sealed class CalendarState {
    data object Loading : CalendarState()
    data class Success(val days: List<TaskDay>) : CalendarState()
    data class Error(val message: String) : CalendarState()
}

data class TaskDay(
    val date: LocalDate,
    val tasks: List<Task>
)
