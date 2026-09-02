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

    private val _isSaving = MutableStateFlow(false)
    /** Активное сохранение: UI должен блокировать кнопку Save, пока true. */
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

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
                    deadlineDate = task.deadline?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                    startTime = task.startTime?.atZone(ZoneId.systemDefault())?.toLocalTime(),
                    durationMinutes = task.durationMinutes,
                    pomodoroEstimate = task.pomodoroEstimate,
                    eisenhowerQuadrant = task.eisenhowerQuadrant,
                    tags = task.tags,
                    recurrenceRule = task.recurrenceRule,
                    reminderDateTime = task.reminderDate?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
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
        if (_isSaving.value) return // защита от повторного сохранения (double tap)
        _isSaving.value = true
        viewModelScope.launch {
            try {
                val (deadline, startTime) = combineDateTime(state)
                val reminderInstant = state.reminderDateTime
                    ?.atZone(ZoneId.systemDefault())?.toInstant()
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
                // Синхронизация будильника с состоянием БД: reminder есть и задача не завершена —
                // планируем, иначе отменяем. Ранее при удалении reminder не происходило cancelReminder.
                if (reminderInstant != null && !task.isCompleted) {
                    alarmScheduler.scheduleReminder(savedId, task.title, reminderInstant.toEpochMilli())
                } else {
                    alarmScheduler.cancelReminder(savedId)
                }
                onSaved()
            } finally {
                _isSaving.value = false
            }
        }
    }

    /**
     * Комбинирует дату дедлайна и время начала в пару (deadline, startTime) Instant.
     *
     * Разделяет deadline и startTime (BUGFIX: ранее при заданном времени они получали
     * один и тот же Instant, из-за чего startTime никогда не был раньше deadline).
     *
     * Правила:
     * - deadlineDate null → deadline=null, startTime=null.
     * - startTime null (только дата) → deadline = конец дня дедлайна (23:59:59.999),
     *   startTime=null. Это означает «срок — конец указанного дня».
     * - startTime задан → startTime = deadlineDate+startTime, deadline = конец дня
     *   дедлайна. startTime всегда <= deadline.
     */
    private fun combineDateTime(state: TaskFormState): Pair<Instant?, Instant?> =
        combineTaskDateTime(state, ZoneId.systemDefault())

        private fun combineTaskDateTime(state: TaskFormState, zone: ZoneId): Pair<Instant?, Instant?> {
        if (state.deadlineDate == null) return null to null

        val deadlineInstant = state.deadlineDate
            .atTime(LocalTime.of(23, 59, 59, 999_999_999))
            .atZone(zone)
            .toInstant()
        val startInstant = state.startTime?.let { time ->
            state.deadlineDate.atTime(time).atZone(zone).toInstant()
        }
        return deadlineInstant to startInstant
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

/**
 * Чистая (тестируемая) логика комбинирования даты дедлайна и времени начала.
 *
 * Правила (см. TaskEditViewModel.combineDateTime):
 * - deadlineDate null → (null, null).
 * - startTime null (только дата) → deadline = конец дня дедлайна (23:59:59.999), startTime=null.
 * - startTime задан → startTime = deadlineDate+startTime, deadline = конец дня дедлайна.
 *   startTime всегда <= deadline (в пределах одного дня).
 *
 * BUGFIX: ранее при заданном времени deadline и startTime получали один и тот же Instant,
 * из-за чего startTime никогда не был раньше deadline. Теперь они разделены.
 */

