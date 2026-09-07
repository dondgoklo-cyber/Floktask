package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import com.taskmanager.domain.logger.Logger
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.usecase.project.GetAllProjectsUseCase
import com.taskmanager.domain.usecase.tag.GetAllTagsUseCase
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    getAllProjectsUseCase: GetAllProjectsUseCase,
    getAllTagsUseCase: GetAllTagsUseCase,
    private val logger: Logger
) : ViewModel() {

    val projects: StateFlow<List<Project>> = getAllProjectsUseCase()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tags: StateFlow<List<Tag>> = getAllTagsUseCase()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createTaskFromVoice(
        title: String,
        date: LocalDate?,
        time: LocalTime?,
        priority: Priority,
        recurrence: RecurrenceRule?
    ) {
        viewModelScope.launch {
            try {
                val zone = ZoneId.of("UTC")
                val deadline = date?.atTime(time ?: LocalTime.MIDNIGHT)?.atZone(zone)?.toInstant()
                val task = Task(
                    title = title,
                    priority = priority,
                    deadline = deadline,
                    recurrenceRule = recurrence
                )
                createTaskUseCase(task)
            } catch (e: Exception) {
                logger.error("QuickAddViewModel", "Error in launch block", e)
            }
        }
    }

    fun createTask(
        parsed: ParsedQuickTask,
        projectId: Long? = null,
        priority: Priority = Priority.NONE,
        tags: List<String> = emptyList(),
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val zone = ZoneId.of("UTC")
                val (deadline, startTime) = if (parsed.deadlineDate != null && parsed.startTime != null) {
                    val instant = parsed.deadlineDate.atTime(parsed.startTime).atZone(zone).toInstant()
                    instant to instant
                } else if (parsed.deadlineDate != null) {
                    val instant = parsed.deadlineDate.atStartOfDay(zone).toInstant()
                    instant to null
                } else {
                    null to null
                }

                val task = Task(
                    title = parsed.title,
                    deadline = deadline,
                    startTime = startTime,
                    durationMinutes = parsed.durationMinutes,
                    projectId = projectId,
                    priority = priority,
                    tags = tags
                )
                val id = createTaskUseCase(task)
                onCreated(id)
            } catch (e: Exception) {
                logger.error("QuickAddViewModel", "Error in launch block", e)
            }
        }
    }
}
