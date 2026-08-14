package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    fun createTask(parsed: ParsedQuickTask, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val zone = ZoneId.systemDefault()
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
                durationMinutes = parsed.durationMinutes
            )
            val id = createTaskUseCase(task)
            onCreated(id)
        }
    }
}
