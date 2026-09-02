package com.taskmanager.presentation.screens.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class UpcomingUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    taskRepository: TaskRepository,
    val hapticManager: HapticManager
) : ViewModel() {

    private val zone = ZoneId.systemDefault()

    val state: StateFlow<UpcomingUiState> = taskRepository
        .getUpcomingTasks(LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli())
        .map { tasks -> UpcomingUiState(tasks = tasks, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UpcomingUiState(isLoading = true))
}


    /**
     * Handle FAB long-press for Upcoming screen
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
    }

