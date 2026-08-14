package com.taskmanager.presentation.screens.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InboxUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class InboxViewModel @Inject constructor(
    taskRepository: TaskRepository
) : ViewModel() {

    val state: StateFlow<InboxUiState> = taskRepository.getInboxTasks()
        .map { tasks -> InboxUiState(tasks = tasks, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.Lazily, InboxUiState(isLoading = true))
}
