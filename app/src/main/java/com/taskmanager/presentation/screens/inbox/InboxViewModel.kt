package com.taskmanager.presentation.screens.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.usecase.task.SetTaskCompletedUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InboxUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase,
    val hapticManager: HapticManager
) : ViewModel() {

    val state: StateFlow<InboxUiState> = taskRepository.getInboxTasks()
        .map { tasks -> InboxUiState(tasks = tasks, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.Lazily, InboxUiState(isLoading = true))

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            // Через SetTaskCompletedUseCase — отмена напоминания при завершении.
            setTaskCompletedUseCase(taskId, true)
        }
    }

    /**
     * Handle FAB long-press for Inbox screen
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
    }
}
