package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase
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
            Log.e("QuickAddViewModel", "Error in launch block", e)
            // Optionally update state to show error
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
        } catch (e: Exception) {
            Log.e("QuickAddViewModel", "Error in launch block", e)
            // Optionally update state to show error
        }
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
                durationMinutes = parsed.durationMinutes
            )
            val id = createTaskUseCase(task)
            onCreated(id)
        }
    }
}
