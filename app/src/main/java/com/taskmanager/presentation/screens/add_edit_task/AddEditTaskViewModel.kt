package com.taskmanager.presentation.screens.add_edit_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.GetTaskByIdUseCase
import com.taskmanager.domain.usecase.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddEditTaskUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Date? = null,
    val priority: Priority = Priority.NONE,
    val projectId: Long? = null,
    val tagIds: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        val taskId = savedStateHandle.get<Long>("taskId")
        if (taskId != null && taskId > 0) {
            loadTask(taskId)
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val task = getTaskByIdUseCase(taskId)
                if (task != null) {
                    _uiState.value = _uiState.value.copy(
                        taskId = task.id,
                        title = task.title,
                        description = task.description,
                        dueDate = task.dueDate,
                        priority = task.priority,
                        projectId = task.projectId,
                        tagIds = task.tagIds,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isError = true,
                        errorMessage = "Задача не найдена",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Ошибка загрузки задачи",
                    isLoading = false
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateDueDate(date: Date?) {
        _uiState.value = _uiState.value.copy(dueDate = date)
    }

    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun updateProjectId(projectId: Long?) {
        _uiState.value = _uiState.value.copy(projectId = projectId)
    }

    fun updateTagIds(tagIds: List<Long>) {
        _uiState.value = _uiState.value.copy(tagIds = tagIds)
    }

    fun saveTask() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            // Валидация
            if (currentState.title.isBlank()) {
                _uiState.value = currentState.copy(
                    isError = true,
                    errorMessage = "Название задачи обязательно"
                )
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true)
            
            try {
                val task = Task(
                    id = currentState.taskId,
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    dueDate = currentState.dueDate,
                    priority = currentState.priority,
                    projectId = currentState.projectId,
                    tagIds = currentState.tagIds,
                    isCompleted = currentState.taskId != null && currentState.taskId > 0 
                        && getTaskByIdUseCase(currentState.taskId!!)?.isCompleted == true,
                    createdAt = currentState.taskId != null && currentState.taskId > 0 
                        ? getTaskByIdUseCase(currentState.taskId!!)?.createdAt ?: Date() 
                        : Date(),
                    updatedAt = Date()
                )

                saveTaskUseCase(task)
                
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Ошибка сохранения задачи",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(isError = false, errorMessage = "")
    }

    fun clearSaved() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
