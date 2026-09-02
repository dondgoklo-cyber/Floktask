package com.taskmanager.presentation.screens.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.usecase.project.GetAllProjectsUseCase
import com.taskmanager.domain.usecase.tag.CreateTagUseCase
import com.taskmanager.domain.usecase.tag.GetAllTagsUseCase
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import com.taskmanager.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val createTagUseCase: CreateTagUseCase,
    getAllProjectsUseCase: GetAllProjectsUseCase,
    getAllTagsUseCase: GetAllTagsUseCase
) : ViewModel() {

    private val taskId: Long? = savedStateHandle
        .get<Long>("taskId")
        ?.takeIf { it > 0 }

    private val projectIdArg: Long? = savedStateHandle
        .get<Long>("projectId")
        ?.takeIf { it > 0 }

    val isEditing: Boolean get() = taskId != null

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    val projects: StateFlow<List<Project>> = getAllProjectsUseCase()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tags: StateFlow<List<Tag>> = getAllTagsUseCase()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        taskId?.let { loadTask(it) }
        // При создании из Project — предзаполнить projectId
        if (taskId == null && projectIdArg != null) {
            _formState.value = _formState.value.copy(projectId = projectIdArg)
        }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            getTaskByIdUseCase(id)?.let { task ->
                _formState.value = TaskFormState(
                    title = task.title,
                    description = task.description.orEmpty(),
                    priority = task.priority,
                    projectId = task.projectId,
                    status = task.status,
                    deadlineDate = task.deadline?.atZone(ZoneId.of("UTC"))?.toLocalDate(),
                    startTime = task.startTime?.atZone(ZoneId.of("UTC"))?.toLocalTime(),
                    durationMinutes = task.durationMinutes,
                    pomodoroEstimate = task.pomodoroEstimate,
                    eisenhowerQuadrant = task.eisenhowerQuadrant,
                    tags = task.tags,
                    recurrenceRule = task.recurrenceRule,
                    reminderDateTime = task.reminderDate?.atZone(ZoneId.of("UTC"))?.toLocalDateTime()
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _formState.value = _formState.value.copy(title = value, titleError = false)
    }

    fun onDescriptionChange(value: String) {
        _formState.value = _formState.value.copy(description = value)
    }

    fun onPriorityChange(value: Priority) {
        _formState.value = _formState.value.copy(priority = value)
    }

    fun onProjectChange(value: Long?) {
        _formState.value = _formState.value.copy(projectId = value)
    }

    fun onStatusChange(value: TaskStatus) {
        _formState.value = _formState.value.copy(status = value)
    }

    fun onDeadlineChange(value: LocalDate?) {
        _formState.value = _formState.value.copy(deadlineDate = value)
    }

    fun onStartTimeChange(value: LocalTime?) {
        _formState.value = _formState.value.copy(startTime = value)
    }

    fun onDurationChange(value: Long?) {
        _formState.value = _formState.value.copy(durationMinutes = value)
    }

    fun onPomodoroEstimateChange(value: Int?) {
        _formState.value = _formState.value.copy(pomodoroEstimate = value)
    }

    fun onEisenhowerChange(value: EisenhowerQuadrant?) {
        _formState.value = _formState.value.copy(eisenhowerQuadrant = value)
    }

    fun onTagsChange(value: List<String>) {
        _formState.value = _formState.value.copy(tags = value)
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isNotBlank() && trimmed !in _formState.value.tags) {
            _formState.value = _formState.value.copy(tags = _formState.value.tags + trimmed)
            viewModelScope.launch {
                val exists = tags.value.any { it.name.equals(trimmed, ignoreCase = true) }
                if (!exists) {
                    createTagUseCase(com.taskmanager.domain.model.Tag(name = trimmed))
                }
            }
        }
    }

    fun removeTag(tag: String) {
        _formState.value = _formState.value.copy(tags = _formState.value.tags - tag)
    }

    fun onReminderChange(value: LocalDateTime?) {
        _formState.value = _formState.value.copy(reminderDateTime = value)
    }

    fun onRecurrenceChange(value: RecurrenceRule?) {
        _formState.value = _formState.value.copy(recurrenceRule = value)
    }

    fun save(onSaved: () -> Unit) {
        val state = _formState.value
        if (state.title.isBlank()) {
            _formState.value = state.copy(titleError = true)
            return
        }
        viewModelScope.launch {
            val (deadline, startTime) = combineDateTime(state)
            val reminderInstant = state.reminderDateTime
                ?.atZone(ZoneId.of("UTC"))?.toInstant()
            val existing = taskId?.let { getTaskByIdUseCase(it) }
            val task = (existing?.copy(
                title = state.title.trim(),
                description = state.description.trim().ifBlank { null },
                priority = state.priority,
                projectId = state.projectId,
                status = state.status,
                deadline = deadline,
                startTime = startTime,
                durationMinutes = state.durationMinutes,
                pomodoroEstimate = state.pomodoroEstimate,
                eisenhowerQuadrant = state.eisenhowerQuadrant,
                tags = state.tags,
                recurrenceRule = state.recurrenceRule,
                reminderDate = reminderInstant
            ) ?: Task(
                title = state.title.trim(),
                description = state.description.trim().ifBlank { null },
                priority = state.priority,
                projectId = state.projectId,
                status = state.status,
                deadline = deadline,
                startTime = startTime,
                durationMinutes = state.durationMinutes,
                pomodoroEstimate = state.pomodoroEstimate,
                eisenhowerQuadrant = state.eisenhowerQuadrant,
                tags = state.tags,
                recurrenceRule = state.recurrenceRule,
                reminderDate = reminderInstant
            ))
            val savedId = if (existing != null) {
                updateTaskUseCase(task)
                task.id ?: 0L
            } else {
                createTaskUseCase(task)
            }
            reminderInstant?.let { inst ->
                alarmScheduler.scheduleReminder(savedId, task.title, inst.toEpochMilli())
            } ?: alarmScheduler.cancelReminder(savedId)
            onSaved()
        }
    }

    /**
     * Комбинирует дату дедлайна и время начала в Instant.
     * Если есть время — deadline = date+time, startTime = date+time.
     * Если только дата — deadline = date в полночь.
     */
    private fun combineDateTime(state: TaskFormState): Pair<Instant?, Instant?> {
        val zone = ZoneId.of("UTC")
        if (state.deadlineDate == null) return null to null

        val time = state.startTime ?: LocalTime.MIDNIGHT
        val dateTime = state.deadlineDate.atTime(time)
        val instant = dateTime.atZone(zone).toInstant()

        val startInstant = if (state.startTime != null) instant else null
        return instant to startInstant
    }
}

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.NONE,
    val projectId: Long? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val deadlineDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val durationMinutes: Long? = null,
    val pomodoroEstimate: Int? = null,
    val eisenhowerQuadrant: EisenhowerQuadrant? = null,
    val tags: List<String> = emptyList(),
    val recurrenceRule: RecurrenceRule? = null,
    val reminderDateTime: LocalDateTime? = null,
    val titleError: Boolean = false
)
