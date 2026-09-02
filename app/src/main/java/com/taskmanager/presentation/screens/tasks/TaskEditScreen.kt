package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.domain.model.TaskTemplates
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShoppingCart
import com.taskmanager.presentation.components.parseTagColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val form by viewModel.formState.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val availableTags by viewModel.tags.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isEditing) R.string.edit_task else R.string.add_task))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Название
            AppTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text(stringResource(R.string.title)) },
                isError = form.titleError,
                supportingText = {
                    if (form.titleError) Text("Введите название задачи")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Шаблоны (только при создании новой задачи)
            if (!viewModel.isEditing) {
                Text("Шаблоны", style = MaterialTheme.typography.labelLarge, color = AppTheme.colors.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TaskTemplates.all.forEach { template ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                viewModel.onTitleChange(template.title)
                                viewModel.onPriorityChange(template.priority)
                                viewModel.onDurationChange(template.durationMinutes)
                                viewModel.onPomodoroEstimateChange(template.pomodoroEstimate)
                            },
                            label = { Text(template.title) }
                        )
                    }
                }
            }

            // Описание
            AppTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.description)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Проект — dropdown
            ProjectDropdown(
                selectedProjectId = form.projectId,
                projects = projects,
                onProjectChange = viewModel::onProjectChange
            )

            // Дата и время — кликабельные карточки
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.md)
            ) {
                Column(Modifier.padding(Spacing.md)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = AppTheme.colors.outline)
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(
                                form.deadlineDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                                    ?: stringResource(R.string.date),
                                color = if (form.deadlineDate != null) AppTheme.colors.onSurface
                                else AppTheme.colors.onSurfaceVariant
                            )
                        }
                        if (form.deadlineDate != null) {
                            IconButton(onClick = { viewModel.onDeadlineChange(null) }) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline)
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = AppTheme.colors.outline)
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(
                                form.startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                    ?: stringResource(R.string.time),
                                color = if (form.startTime != null) AppTheme.colors.onSurface
                                else AppTheme.colors.onSurfaceVariant
                            )
                        }
                        if (form.startTime != null) {
                            IconButton(onClick = { viewModel.onStartTimeChange(null) }) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline)
                            }
                        }
                    }
                }
            }

            // Длительность
            AppTextField(
                value = form.durationMinutes?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.onDurationChange(value.toLongOrNull())
                },
                label = { Text(stringResource(R.string.duration) + " (мин)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Напоминание
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = AppTheme.colors.outline)
                    TextButton(onClick = { showReminderPicker = true }) {
                        Text(
                            form.reminderDateTime?.format(
                                DateTimeFormatter.ofPattern("d MMM, HH:mm")
                            ) ?: stringResource(R.string.set_reminder),
                            color = if (form.reminderDateTime != null) AppTheme.colors.onSurface
                            else AppTheme.colors.onSurfaceVariant
                        )
                    }
                    if (form.reminderDateTime != null) {
                        IconButton(onClick = { viewModel.onReminderChange(null) }) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline)
                        }
                    }
                }
            }

            // Приоритет
            Text(stringResource(R.string.priority), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Priority.entries.forEach { priority ->
                    val label = when (priority) {
                        Priority.HIGH -> "Высокий"
                        Priority.MEDIUM -> "Средний"
                        Priority.LOW -> "Низкий"
                        Priority.NONE -> "Нет"
                    }
                    FilterChip(
                        selected = form.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) },
                        label = { Text(label) }
                    )
                }
            }

            // Статус
            Text(stringResource(R.string.status), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaskStatus.entries.forEach { status ->
                    val label = when (status) {
                        TaskStatus.TODO -> "К выполнению"
                        TaskStatus.IN_PROGRESS -> "В работе"
                        TaskStatus.DONE -> "Готово"
                    }
                    FilterChip(
                        selected = form.status == status,
                        onClick = { viewModel.onStatusChange(status) },
                        label = { Text(label) }
                    )
                }
            }

            // Pomodoro estimate
            AppTextField(
                value = form.pomodoroEstimate?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.onPomodoroEstimateChange(value.toIntOrNull())
                },
                label = { Text(stringResource(R.string.pomodoro_estimate) + " 🍅") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Теги
            Text(stringResource(R.string.tags), style = MaterialTheme.typography.titleSmall)
            if (form.tags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    form.tags.forEach { tag ->
                        val tagColor = parseTagColor(availableTags.find { it.name == tag }?.color)
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.removeTag(tag) },
                            label = { Text(tag) },
                            trailingIcon = { Icon(Icons.Filled.Close, contentDescription = null) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tagColor.copy(alpha = 0.18f),
                                selectedLabelColor = tagColor,
                                selectedTrailingIconColor = tagColor
                            )
                        )
                    }
                }
            }
            if (availableTags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    availableTags
                        .filter { it.name !in form.tags }
                        .forEach { tag ->
                            val tagColor = parseTagColor(tag.color)
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.addTag(tag.name) },
                                label = { Text(tag.name) },
                                leadingIcon = {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(tagColor, CircleShape)
                                    )
                                }
                            )
                        }
                }
            }
            AppTextField(
                value = "",
                onValueChange = { value ->
                    if (value.endsWith(",") || value.endsWith(" ")) {
                        viewModel.addTag(value.trimEnd(',', ' '))
                    }
                },
                label = { Text("Добавить тег...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Повтор
            Text(stringResource(R.string.recurrence), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                FilterChip(
                    selected = form.recurrenceRule == null,
                    onClick = { viewModel.onRecurrenceChange(null) },
                    label = { Text(stringResource(R.string.none)) }
                )
                RecurrenceRule.entries.forEach { rule ->
                    val label = when (rule) {
                        RecurrenceRule.DAILY -> "Ежедневно"
                        RecurrenceRule.WEEKLY -> "Еженедельно"
                        RecurrenceRule.MONTHLY -> "Ежемесячно"
                        RecurrenceRule.YEARLY -> "Ежегодно"
                        RecurrenceRule.CUSTOM -> "Другое"
                    }
                    FilterChip(
                        selected = form.recurrenceRule == rule,
                        onClick = { viewModel.onRecurrenceChange(rule) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            Button(
                onClick = { viewModel.save(onSaved = onBack) },
                enabled = !isSaving && form.title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (isSaving) R.string.saving else R.string.save))
            }
        }
    }

    if (showDatePicker) {
        val initialDate = form.deadlineDate ?: LocalDate.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.onDeadlineChange(date)
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showReminderPicker) {
        val initialDate = form.reminderDateTime?.toLocalDate() ?: form.deadlineDate ?: LocalDate.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showReminderPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val time = form.reminderDateTime?.toLocalTime() ?: LocalTime.of(9, 0)
                        viewModel.onReminderChange(date.atTime(time))
                    }
                    showReminderPicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showReminderPicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val initialTime = form.startTime ?: LocalTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onStartTimeChange(
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )
                    showTimePicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDropdown(
    selectedProjectId: Long?,
    projects: List<com.taskmanager.domain.model.Project>,
    onProjectChange: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProject = projects.find { it.id == selectedProjectId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        AppTextField(
            value = selectedProject?.title ?: stringResource(R.string.no_project),
            onValueChange = {},
            label = { Text(stringResource(R.string.project)) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text(stringResource(R.string.no_project)) },
                onClick = {
                    onProjectChange(null)
                    expanded = false
                }
            )
            projects.forEach { project ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(project.title) },
                    onClick = {
                        onProjectChange(project.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
