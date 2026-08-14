package com.taskmanager.presentation.screens.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _projectId = MutableStateFlow(0L)
    val projectId: StateFlow<Long> = _projectId.asStateFlow()

    val state: StateFlow<ProjectDetailUiState> = _projectId
        .filter { it > 0 }
        .flatMapLatest { id ->
            combine(
                flowOf(id),
                taskRepository.getTasksByProject(id)
            ) { projectId, tasks ->
                val project = projectRepository.getProjectById(projectId)
                ProjectDetailUiState(
                    project = project,
                    tasks = tasks,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, ProjectDetailUiState(isLoading = true))

    fun loadProject(id: Long) {
        _projectId.value = id
    }

    fun moveTask(task: Task, newStatus: TaskStatus) {
        if (task.status == newStatus) return
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(status = newStatus, isCompleted = newStatus == TaskStatus.DONE))
        }
    }
}
