package com.taskmanager.presentation.screens.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.geofence.GeofenceManager
import com.taskmanager.domain.model.LocationReminder
import com.taskmanager.domain.usecase.location.CreateLocationReminderUseCase
import com.taskmanager.domain.usecase.location.DeleteLocationReminderUseCase
import com.taskmanager.domain.usecase.location.ObserveLocationRemindersUseCase
import com.taskmanager.domain.usecase.location.ToggleLocationReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationRemindersViewModel @Inject constructor(
    observeUseCase: ObserveLocationRemindersUseCase,
    private val createUseCase: CreateLocationReminderUseCase,
    private val deleteUseCase: DeleteLocationReminderUseCase,
    private val toggleUseCase: ToggleLocationReminderUseCase,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    private val _state = MutableStateFlow<LocationRemindersState>(LocationRemindersState.Loading)
    val state: StateFlow<LocationRemindersState> = _state.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        observeUseCase()
            .onEach { _state.value = LocationRemindersState.Success(it) }
            .catch { cause -> _state.value = LocationRemindersState.Error(cause.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun openCreateDialog() { _showCreateDialog.value = true }
    fun closeCreateDialog() { _showCreateDialog.value = false }

    fun createReminder(taskId: Long, label: String, latitude: Double, longitude: Double, radius: Float) {
        viewModelScope.launch {
            val reminder = LocationReminder(
                taskId = taskId,
                label = label,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radius
            )
            val id = createUseCase(reminder)
            geofenceManager.register(reminder.copy(id = id))
            closeCreateDialog()
        }
    }

    fun toggle(id: Long, active: Boolean) {
        viewModelScope.launch {
            toggleUseCase(id, active)
            if (!active) geofenceManager.unregister(id)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            geofenceManager.unregister(id)
            deleteUseCase(id)
        }
    }
}

sealed class LocationRemindersState {
    data object Loading : LocationRemindersState()
    data class Success(val reminders: List<LocationReminder>) : LocationRemindersState()
    data class Error(val message: String) : LocationRemindersState()
}
