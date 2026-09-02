package com.taskmanager.presentation.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.usecase.kanban.MoveTaskToStatusUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KanbanUiState(
    val columns: Map<TaskStatus, List<Task>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val moveTaskToStatusUseCase: MoveTaskToStatusUseCase,
    private val taskRepository: com.taskmanager.domain.repository.TaskRepository,
    val hapticManager: HapticManager
) : ViewModel() {

    val state: StateFlow<KanbanUiState> = taskRepository.getAllTasks()
        .map { tasks ->
            val columns = TaskStatus.entries.associateWith { status ->
                tasks.filter { it.status == status }
            }
            KanbanUiState(columns = columns, isLoading = false)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, KanbanUiState(isLoading = true))

    fun moveTask(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            moveTaskToStatusUseCase(taskId, newStatus)
        }
    }

    /**
     * Handle FAB long-press for Kanban screen
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
    }
}
