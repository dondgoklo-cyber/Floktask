package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.note.GetNotesByProjectUseCase
import com.taskmanager.domain.usecase.project.GetProjectNameByIdUseCase
import com.taskmanager.domain.usecase.subtask.CreateSubtaskUseCase
import com.taskmanager.domain.usecase.subtask.GetSubtaskTreeUseCase
import com.taskmanager.domain.usecase.subtask.SetSubtaskCompletedUseCase
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailState(
    val task: Task? = null,
    val projectName: String? = null,
    val subtasks: List<Subtask> = emptyList(),
    val relatedNotes: List<Note> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getProjectNameByIdUseCase: GetProjectNameByIdUseCase,
    private val getSubtaskTreeUseCase: GetSubtaskTreeUseCase,
    private val getNotesByProjectUseCase: GetNotesByProjectUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val setSubtaskCompletedUseCase: SetSubtaskCompletedUseCase,
    private val createSubtaskUseCase: CreateSubtaskUseCase,
    private val deleteSubtaskUseCase: DeleteSubtaskUseCase,
    private val updateSubtaskUseCase: UpdateSubtaskUseCase,
    private val reorderSubtasksUseCase: ReorderSubtasksUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            try {
                val task = getTaskByIdUseCase(taskId)
                val projectName = task?.projectId?.let { getProjectNameByIdUseCase(it) }
                val subtasks = task?.let { getSubtaskTreeUseCase(it.id ?: 0) } ?: emptyList()
                val relatedNotes = task?.projectId?.let { pid ->
                    getNotesByProjectUseCase(pid).firstOrNull() ?: emptyList()
                } ?: emptyList()
                _state.value = TaskDetailState(
                    task = task,
                    projectName = projectName,
                    subtasks = subtasks,
                    relatedNotes = relatedNotes,
                    isLoading = false
                )
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error loading task", e)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            try {
                val updated = task.copy(isCompleted = !task.isCompleted)
                updateTaskUseCase(updated)
                _state.value = _state.value.copy(task = updated)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error toggling task complete", e)
            }
        }
    }

    fun toggleSubtask(subtask: Subtask) {
        viewModelScope.launch {
            try {
                setSubtaskCompletedUseCase(subtask.id ?: 0, !subtask.isCompleted)
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error toggling subtask", e)
            }
        }
    }

    fun addSubtask(taskId: Long, title: String, parentSubtaskId: Long? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val siblings = if (parentSubtaskId != null) {
                    findAllById(_state.value.subtasks, parentSubtaskId)?.children ?: emptyList()
                } else {
                    _state.value.subtasks
                }
                val orderIndex = (siblings.maxOfOrNull { it.orderIndex } ?: -1) + 1
                createSubtaskUseCase(
                    Subtask(taskId = taskId, title = title.trim(), orderIndex = orderIndex, parentSubtaskId = parentSubtaskId)
                )
                loadSubtasks(taskId)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error adding subtask", e)
            }
        }
    }

    private fun findAllById(tree: List<Subtask>, id: Long): Subtask? {
        for (s in tree) {
            if (s.id == id) return s
            val found = findAllById(s.children, id)
            if (found != null) return found
        }
        return null
    }

    fun deleteSubtask(subtask: Subtask) {
        viewModelScope.launch {
            try {
                deleteSubtaskUseCase(subtask.id ?: 0)
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error deleting subtask", e)
            }
        }
    }

    fun renameSubtask(subtask: Subtask, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                updateSubtaskUseCase(subtask.copy(title = newTitle.trim()))
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error renaming subtask", e)
            }
        }
    }

    fun reorderSubtask(taskId: Long, fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                reorderSubtasksUseCase(taskId, fromIndex, toIndex)
                loadSubtasks(taskId)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error reordering subtask", e)
            }
        }
    }

    private fun loadSubtasks(taskId: Long) {
        viewModelScope.launch {
            try {
                val subtasks = getSubtaskTreeUseCase(taskId)
                _state.value = _state.value.copy(subtasks = subtasks)
            } catch (e: Exception) {
                logger.error("TaskDetailViewModel", "Error loading subtasks", e)
            }
        }
    }
}
