package com.taskmanager.presentation.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.repository.TaskRepository
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
    private val taskRepository: TaskRepository
) : ViewModel() {

    val state: StateFlow<KanbanUiState> = taskRepository.getAllTasks()
        .map { tasks ->
            val columns = TaskStatus.entries.associateWith { status ->
                tasks.filter { it.status == status }
            }
            KanbanUiState(columns = columns, isLoading = false)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, KanbanUiState(isLoading = true))

    fun moveTask(task: Task, newStatus: TaskStatus) {
        if (task.status == newStatus) return
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(status = newStatus, isCompleted = newStatus == TaskStatus.DONE))
        }
    }
}
