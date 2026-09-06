package com.taskmanager.presentation.screens.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.GetInboxTasksUseCase
import com.taskmanager.domain.usecase.task.SetTaskCompletedUseCase
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
    private val getInboxTasksUseCase: GetInboxTasksUseCase,
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase,
    private val logger: Logger
) : ViewModel() {

    val state: StateFlow<InboxUiState> = getInboxTasksUseCase()
        .map { tasks -> InboxUiState(tasks = tasks, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.Lazily, InboxUiState(isLoading = true))

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            try {
                setTaskCompletedUseCase(taskId, true)
            } catch (e: Exception) {
                logger.error("InboxViewModel", "Error completing task", e)
            }
        }
    }
}
