package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.SubtaskRepository
import com.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailState(
    val task: Task? = null,
    val projectName: String? = null,
    val subtasks: List<Subtask> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val subtaskRepository: SubtaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            val projectName = task?.projectId?.let { projectRepository.getProjectById(it)?.title }
            val subtasks = task?.let { subtaskRepository.getSubtaskTree(it.id ?: 0) } ?: emptyList()
            _state.value = TaskDetailState(
                task = task,
                projectName = projectName,
                subtasks = subtasks,
                isLoading = false
            )
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            taskRepository.updateTask(updated)
            _state.value = _state.value.copy(task = updated)
        }
    }

    fun toggleSubtask(subtask: Subtask) {
        viewModelScope.launch {
            subtaskRepository.setCompleted(subtask.id ?: 0, !subtask.isCompleted)
            loadSubtasks(subtask.taskId)
        }
    }

    fun addSubtask(taskId: Long, title: String, parentSubtaskId: Long? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
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
            subtaskRepository.deleteSubtask(subtask.id ?: 0)
            loadSubtasks(subtask.taskId)
        }
    }

    fun renameSubtask(subtask: Subtask, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            subtaskRepository.updateSubtask(subtask.copy(title = newTitle.trim()))
            loadSubtasks(subtask.taskId)
        }
    }

    fun reorderSubtask(taskId: Long, fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            subtaskRepository.reorderSubtasks(taskId, fromIndex, toIndex)
            loadSubtasks(taskId)
        }
    }

    private fun loadSubtasks(taskId: Long) {
        viewModelScope.launch {
            val subtasks = subtaskRepository.getSubtaskTree(taskId)
            _state.value = _state.value.copy(subtasks = subtasks)
        }
    }
}
