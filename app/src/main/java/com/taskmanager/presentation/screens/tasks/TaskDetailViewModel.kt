package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.NoteRepository
import com.taskmanager.domain.repository.SubtaskRepository
import com.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

data class TaskDetailState(
    val task: Task? = null,
    val projectName: String? = null,
    val subtasks: List<Subtask> = emptyList(),
    val relatedNotes: List<Note> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val subtaskRepository: SubtaskRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                val projectName = task?.projectId?.let { projectRepository.getProjectById(it)?.title }
                val subtasks = task?.let { subtaskRepository.getSubtaskTree(it.id ?: 0) } ?: emptyList()
                val relatedNotes = task?.projectId?.let { pid ->
                    noteRepository.getNotesByProject(pid).firstOrNull() ?: emptyList()
                } ?: emptyList()
                _state.value = TaskDetailState(
                    task = task,
                    projectName = projectName,
                    subtasks = subtasks,
                    relatedNotes = relatedNotes,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            try {
                val updated = task.copy(isCompleted = !task.isCompleted)
                taskRepository.updateTask(updated)
                _state.value = _state.value.copy(task = updated)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
            }
        }
    }

    fun toggleSubtask(subtask: Subtask) {
        viewModelScope.launch {
            try {
                subtaskRepository.setCompleted(subtask.id ?: 0, !subtask.isCompleted)
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
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
                subtaskRepository.createSubtask(
                    Subtask(taskId = taskId, title = title.trim(), orderIndex = orderIndex, parentSubtaskId = parentSubtaskId)
                )
                loadSubtasks(taskId)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
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
                subtaskRepository.deleteSubtask(subtask.id ?: 0)
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
            }
        }
    }

    fun renameSubtask(subtask: Subtask, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                subtaskRepository.updateSubtask(subtask.copy(title = newTitle.trim()))
                loadSubtasks(subtask.taskId)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
            }
        }
    }

    fun reorderSubtask(taskId: Long, fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                subtaskRepository.reorderSubtasks(taskId, fromIndex, toIndex)
                loadSubtasks(taskId)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
            }
        }
    }

    private fun loadSubtasks(taskId: Long) {
        viewModelScope.launch {
            try {
                val subtasks = subtaskRepository.getSubtaskTree(taskId)
                _state.value = _state.value.copy(subtasks = subtasks)
            } catch (e: Exception) {
                Log.e("TaskDetailViewModel", "Error in launch block", e)
            }
        }
    }
}
