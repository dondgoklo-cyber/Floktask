package com.taskmanager.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.backup.BackupManager
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.usecase.settings.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val hasPin: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val userPreferences: UserPreferences,
    private val logger: Logger
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun exportToUri(uri: Uri, onSuccess: () -> Unit, onError: () -> Unit) {
        _state.value = _state.value.copy(isExporting = true)
        viewModelScope.launch {
            try {
                val ok = backupManager.exportToUri(uri)
                _state.value = _state.value.copy(isExporting = false)
                if (ok) onSuccess() else onError()
            } catch (e: Exception) {
                logger.error("SettingsViewModel", "Error in launch block", e)
            }
        }
    }

    fun importFromUri(uri: Uri, onSuccess: () -> Unit, onError: () -> Unit) {
        _state.value = _state.value.copy(isImporting = true)
        viewModelScope.launch {
            try {
                val ok = backupManager.importFromUri(uri)
                _state.value = _state.value.copy(isImporting = false)
                if (ok) onSuccess() else onError()
            } catch (e: Exception) {
                logger.error("SettingsViewModel", "Error in launch block", e)
            }
        }
    }
}
