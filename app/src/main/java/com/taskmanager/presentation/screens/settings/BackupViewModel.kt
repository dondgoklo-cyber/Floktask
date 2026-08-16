package com.taskmanager.presentation.screens.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.repository.LocalBackup
import com.taskmanager.domain.repository.RestoreResult
import com.taskmanager.domain.usecase.backup.ExportBackupUseCase
import com.taskmanager.domain.usecase.backup.ImportBackupUseCase
import com.taskmanager.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BackupState {
    data object Idle : BackupState()
    data object Exporting : BackupState()
    data object Importing : BackupState()
    data object SavingLocal : BackupState()
    data class ExportSuccess(val taskCount: Int) : BackupState()
    data class ImportSuccess(val result: RestoreResult) : BackupState()
    data class LocalBackupSaved(val fileName: String) : BackupState()
    data class Restored(val result: RestoreResult) : BackupState()
    data class Error(val message: String) : BackupState()
}

data class BackupUiState(
    val backupState: BackupState = BackupState.Idle,
    val localBackups: List<LocalBackup> = emptyList()
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val backupRepository: BackupRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()

    init {
        loadLocalBackups()
    }

    private fun loadLocalBackups() {
        viewModelScope.launch {
            runCatching { backupRepository.listLocalBackups() }
                .onSuccess { list -> _state.value = _state.value.copy(localBackups = list) }
        }
    }

    fun exportTo(uri: Uri) {
        _state.value = _state.value.copy(backupState = BackupState.Exporting)
        viewModelScope.launch {
            runCatching {
                val app = getApplication<Application>()
                app.contentResolver.openOutputStream(uri)?.use { output ->
                    exportBackupUseCase(output)
                } ?: error("Could not open file for writing")
            }.onSuccess { file ->
                _state.value = _state.value.copy(
                    backupState = BackupState.ExportSuccess(file.data.tasks.size)
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    backupState = BackupState.Error(e.message ?: "Export failed")
                )
            }
        }
    }

    fun importFrom(uri: Uri) {
        _state.value = _state.value.copy(backupState = BackupState.Importing)
        viewModelScope.launch {
            runCatching {
                val app = getApplication<Application>()
                app.contentResolver.openInputStream(uri)?.use { input ->
                    importBackupUseCase(input)
                } ?: error("Could not open file for reading")
            }.onSuccess { result ->
                loadLocalBackups()
                _state.value = _state.value.copy(
                    backupState = BackupState.ImportSuccess(result)
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    backupState = BackupState.Error(e.message ?: "Import failed")
                )
            }
        }
    }

    fun backupNow() {
        _state.value = _state.value.copy(backupState = BackupState.SavingLocal)
        viewModelScope.launch {
            runCatching { backupRepository.saveToLocal() }
                .onSuccess { name ->
                    loadLocalBackups()
                    _state.value = _state.value.copy(
                        backupState = BackupState.LocalBackupSaved(name)
                    )
                }.onFailure { e ->
                    _state.value = _state.value.copy(
                        backupState = BackupState.Error(e.message ?: "Backup failed")
                    )
                }
        }
    }

    fun restoreLocal(fileName: String) {
        _state.value = _state.value.copy(backupState = BackupState.Importing)
        viewModelScope.launch {
            runCatching {
                val file = backupRepository.loadFromLocal(fileName)
                backupRepository.restore(file)
            }.onSuccess { result ->
                _state.value = _state.value.copy(
                    backupState = BackupState.Restored(result)
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    backupState = BackupState.Error(e.message ?: "Restore failed")
                )
            }
        }
    }

    fun deleteLocal(fileName: String) {
        viewModelScope.launch {
            runCatching { backupRepository.deleteLocalBackup(fileName) }
                .also { loadLocalBackups() }
        }
    }

    fun reset() {
        _state.value = _state.value.copy(backupState = BackupState.Idle)
    }
}
