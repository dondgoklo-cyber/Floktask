package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import com.taskmanager.domain.logger.Logger
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.project.GetAllProjectsFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getAllProjectsFlowUseCase: GetAllProjectsFlowUseCase,
    private val logger: Logger
) : ViewModel() {

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

    fun createTask(parsed: ParsedQuickTask, onCreated: (Long) -> Unit) {
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

                // Resolve project name to ID
                var projectId: Long? = null
                if (parsed.projectName != null) {
                    try {
                        val projects = getAllProjectsFlowUseCase().first()
                        projectId = projects.find {
                            it.title.equals(parsed.projectName, ignoreCase = true)
                        }?.id
                    } catch (e: Exception) {
                        logger.error("QuickAddViewModel", "Failed to resolve project", e)
                    }
                }

                val task = Task(
                    title = parsed.title,
                    deadline = deadline,
                    startTime = startTime,
                    durationMinutes = parsed.durationMinutes,
                    projectId = projectId,
                    tags = parsed.tags,
                    priority = parsed.priority ?: Priority.NONE
                )
                val id = createTaskUseCase(task)
                onCreated(id)
            } catch (e: Exception) {
                logger.error("QuickAddViewModel", "Error in launch block", e)
            }
        }
    }
}
