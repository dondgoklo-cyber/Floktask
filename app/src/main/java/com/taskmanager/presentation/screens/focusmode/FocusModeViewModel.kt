package com.taskmanager.presentation.screens.focusmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import com.taskmanager.presentation.screens.focusmode.DndHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusModeUiState(
    val task: Task? = null,
    val isActive: Boolean = false,
    val dndEnabled: Boolean = false
)

@HiltViewModel
class FocusModeViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val dndHelper: DndHelper,
    private val logger: Logger
) : ViewModel() {

    private val _state = MutableStateFlow(FocusModeUiState())
    val state: StateFlow<FocusModeUiState> = _state.asStateFlow()

    // Event for UI to handle DND settings navigation
    sealed interface FocusModeEvent {
        data object OpenDndSettings : FocusModeEvent
    }

    private val _events = MutableStateFlow<FocusModeEvent?>(null)
    val events: StateFlow<FocusModeEvent?> = _events.asStateFlow()

    fun startFocus(taskId: Long) {
        viewModelScope.launch {
            try {
                val task = getTaskByIdUseCase(taskId)
                _state.value = _state.value.copy(task = task, isActive = true)
            } catch (e: Exception) {
                logger.error("FocusModeViewModel", "Error in launch block", e)
            }
        }
        // Enable DND if permission granted; UI surfaces the status.
        val applied = dndHelper.enableDnd()
        _state.value = _state.value.copy(dndEnabled = applied)
    }

    fun stopFocus() {
        dndHelper.disableDnd()
        _state.value = _state.value.copy(isActive = false, dndEnabled = false)
    }

    val isDndAccessGranted: Boolean
        get() = dndHelper.isPolicyAccessGranted

    fun openDndSettings() {
        viewModelScope.launch {
            _events.emit(FocusModeEvent.OpenDndSettings)
        }
    }
}
