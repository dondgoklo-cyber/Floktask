package com.taskmanager.presentation.screens.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.usecase.note.CreateNoteUseCase
import com.taskmanager.domain.usecase.note.GetNotesByProjectUseCase
import com.taskmanager.domain.usecase.project.GetProjectByIdUseCase
import com.taskmanager.domain.usecase.tag.GetAllTagsUseCase
import com.taskmanager.domain.usecase.task.GetTasksByProjectUseCase
import com.taskmanager.domain.usecase.task.SetTaskCompletedUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val tagColors: Map<String, String> = emptyMap(),
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getNotesByProjectUseCase: GetNotesByProjectUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _projectId = MutableStateFlow(0L)
    val projectId: StateFlow<Long> = _projectId.asStateFlow()

    val state: StateFlow<ProjectDetailUiState> = _projectId
        .filter { it > 0 }
        .flatMapLatest { id ->
            combine(
                flowOf(id),
                getTasksByProjectUseCase(id),
                getAllTagsUseCase(),
                getNotesByProjectUseCase(id)
            ) { projectId, tasks, allTags, notes ->
                val project = getProjectByIdUseCase(projectId)
                val colors = allTags.associate { it.name to (it.color ?: "") }
                ProjectDetailUiState(
                    project = project,
                    tasks = tasks,
                    tagColors = colors,
                    notes = notes,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, ProjectDetailUiState(isLoading = true))

    fun loadProject(id: Long) {
        _projectId.value = id
    }

    fun createNoteForProject(title: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val id = createNoteUseCase(Note(
                    title = title,
                    contentMarkdown = "",
                    projectId = _projectId.value
                ))
                onCreated(id)
            } catch (e: Exception) {
                logger.error("ProjectDetailViewModel", "Error creating note for project", e)
            }
        }
    }

    fun moveTask(task: Task, newStatus: TaskStatus) {
        if (task.status == newStatus) return
        viewModelScope.launch {
            try {
                updateTaskUseCase(task.copy(status = newStatus, isCompleted = newStatus == TaskStatus.DONE))
            } catch (e: Exception) {
                logger.error("ProjectDetailViewModel", "Error moving task", e)
            }
    }
    }

    fun moveTaskToQuadrant(task: Task, quadrant: EisenhowerQuadrant) {
        viewModelScope.launch {
            try {
                updateTaskUseCase(task.copy(eisenhowerQuadrant = quadrant))
            } catch (e: Exception) {
                logger.error("ProjectDetailViewModel", "Error moving task to quadrant", e)
            }
        }
    }

    fun toggleTaskCompleted(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                setTaskCompletedUseCase(taskId, completed)
            } catch (e: Exception) {
                logger.error("ProjectDetailViewModel", "Error toggling task completion", e)
            }
        }
    }
}
