package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.gamification.RecordTaskCompletionUseCase
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.task.DeleteTaskUseCase
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, ACTIVE, COMPLETED }

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val recordTaskCompletionUseCase: RecordTaskCompletionUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    val priorityFilter: StateFlow<Priority?> = _priorityFilter.asStateFlow()

    val tasksState: StateFlow<TasksState> = combine(
        getAllTasksUseCase(),
        _searchQuery,
        _filter,
        _priorityFilter
    ) { tasks, query, filter, priority ->
        var filtered = tasks

        if (query.isNotBlank()) {
            val q = query.lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(q) ||
                    (it.description?.lowercase()?.contains(q) == true)
            }
        }

        filtered = when (filter) {
            TaskFilter.ALL -> filtered
            TaskFilter.ACTIVE -> filtered.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> filtered.filter { it.isCompleted }
        }

        if (priority != null) {
            filtered = filtered.filter { it.priority == priority }
        }

        TasksState.Success(filtered)
    }.catch { cause ->
        emit(TasksState.Error(cause.message ?: "Неизвестная ошибка"))
    }.stateIn(viewModelScope, SharingStarted.Lazily, TasksState.Loading)

    init {
        // Flow is collected via stateIn above; no manual launch needed.
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun setPriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
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
