package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.project.GetAllProjectsUseCase
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    getAllProjectsUseCase: GetAllProjectsUseCase
) : ViewModel() {

    private val taskId: Long? = savedStateHandle
        .get<String>("taskId")
        ?.toLongOrNull()
        ?.takeIf { it > 0 }

    val isEditing: Boolean get() = taskId != null

    val projects: StateFlow<List<Project>> = getAllProjectsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            getTaskByIdUseCase(id)?.let { task ->
                _formState.value = TaskFormState(
                    title = task.title,
                    description = task.description.orEmpty(),
                    priority = task.priority,
                    projectId = task.projectId,
                    recurrenceRule = task.recurrenceRule
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _formState.value = _formState.value.copy(title = value, titleError = false)
    }

    fun onDescriptionChange(value: String) {
        _formState.value = _formState.value.copy(description = value)
    }

    fun onPriorityChange(value: Priority) {
        _formState.value = _formState.value.copy(priority = value)
    }

    fun onProjectChange(value: Long?) {
        _formState.value = _formState.value.copy(projectId = value)
    }

    fun onRecurrenceChange(value: RecurrenceRule?) {
        _formState.value = _formState.value.copy(recurrenceRule = value)
    }

    fun save(onSaved: () -> Unit) {
        val state = _formState.value
        if (state.title.isBlank()) {
            _formState.value = state.copy(titleError = true)
            return
        }
        viewModelScope.launch {
            val existing = taskId?.let { getTaskByIdUseCase(it) }
            val task = (existing?.copy(
                title = state.title.trim(),
                description = state.description.trim().ifBlank { null },
                priority = state.priority,
                projectId = state.projectId,
                recurrenceRule = state.recurrenceRule
            ) ?: Task(
                title = state.title.trim(),
                description = state.description.trim().ifBlank { null },
                priority = state.priority,
                projectId = state.projectId,
                recurrenceRule = state.recurrenceRule
            ))
            if (existing != null) {
                updateTaskUseCase(task)
            } else {
                createTaskUseCase(task)
            }
            onSaved()
        }
    }
}

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.NONE,
    val projectId: Long? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val titleError: Boolean = false
)
