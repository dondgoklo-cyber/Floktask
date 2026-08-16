package com.taskmanager.presentation.screens.focusmode

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val dndHelper: DndHelper
) : ViewModel() {

    private val _state = MutableStateFlow(FocusModeUiState())
    val state: StateFlow<FocusModeUiState> = _state.asStateFlow()

    fun startFocus(taskId: Long) {
        viewModelScope.launch {
            val task = getTaskByIdUseCase(taskId)
            _state.value = _state.value.copy(task = task, isActive = true)
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent = android.content.Intent(
                android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
