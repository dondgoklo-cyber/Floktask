package com.taskmanager.presentation.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.usecase.project.CreateProjectUseCase
import com.taskmanager.domain.usecase.project.GetAllProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    getAllProjectsUseCase: GetAllProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _projectsState = MutableStateFlow<ProjectsState>(ProjectsState.Loading)
    val projectsState: StateFlow<ProjectsState> = _projectsState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        getAllProjectsUseCase()
            .map { ProjectsState.Success(it) as ProjectsState }
            .onEach { _projectsState.value = it }
            .catch { cause -> _projectsState.value = ProjectsState.Error(cause.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun openCreateDialog() { _showCreateDialog.value = true }
    fun closeCreateDialog() { _showCreateDialog.value = false }

    fun createProject(title: String, description: String?) {
        viewModelScope.launch {
            createProjectUseCase(Project(title = title.trim(), description = description?.trim()))
            closeCreateDialog()
        }
    }
}

sealed class ProjectsState {
    data object Loading : ProjectsState()
    data class Success(val projects: List<Project>) : ProjectsState()
    data class Error(val message: String) : ProjectsState()
}
