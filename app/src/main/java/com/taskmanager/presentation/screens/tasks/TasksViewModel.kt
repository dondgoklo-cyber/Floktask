package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.task.DeleteTaskUseCase
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import com.taskmanager.domain.usecase.gamification.RecordTaskCompletionUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
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
class TasksViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val recordTaskCompletionUseCase: RecordTaskCompletionUseCase
) : ViewModel() {

    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Loading)
    val tasksState: StateFlow<TasksState> = _tasksState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        getAllTasksUseCase()
            .onEach { _tasksState.value = TasksState.Success(it) }
            .catch { cause -> _tasksState.value = TasksState.Error(cause.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun createTask(title: String) {
        viewModelScope.launch { createTaskUseCase(Task(title = title)) }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            val willComplete = !task.isCompleted
            updateTaskUseCase(task.copy(isCompleted = willComplete))
            if (willComplete) recordTaskCompletionUseCase(task.priority)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch { deleteTaskUseCase(taskId) }
    }
}

sealed class TasksState {
    data object Loading : TasksState()
    data class Success(val tasks: List<Task>) : TasksState()
    data class Error(val message: String) : TasksState()
}
