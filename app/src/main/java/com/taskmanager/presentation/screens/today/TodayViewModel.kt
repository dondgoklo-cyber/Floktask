package com.taskmanager.presentation.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class TodayUiState(
    val overdue: List<Task> = emptyList(),
    val dueToday: List<Task> = emptyList(),
    val noDeadline: List<Task> = emptyList()
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    getAllTasksUseCase: GetAllTasksUseCase
) : ViewModel() {

    private val zone = ZoneId.systemDefault()

    val state: StateFlow<TodayUiState> = getAllTasksUseCase()
        .map { tasks ->
            val incomplete = tasks.filterNot { it.isCompleted }
            val today = LocalDate.now(zone)
            val (overdue, rest) = incomplete.partition { task ->
                task.deadline?.isBefore(today.atStartOfDay(zone).toInstant()) == true
            }
            val (dueToday, noDeadline) = rest.partition { task ->
                task.deadline?.atZone(zone)?.toLocalDate() == today
            }
            TodayUiState(overdue = overdue, dueToday = dueToday, noDeadline = noDeadline.take(5))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState())

}
