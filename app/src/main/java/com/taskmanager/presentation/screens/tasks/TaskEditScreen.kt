package com.taskmanager.presentation.screens.tasks

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
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
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val form by viewModel.formState.collectAsState()
    val projects by viewModel.projects.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var newTagText by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isEditing) R.string.edit_task else R.string.add_task))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
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
            OutlinedTextField(
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

            // Описание
            OutlinedTextField(
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

            // Дата и время
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                OutlinedTextField(
                    value = form.deadlineDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.date)) },
                    readOnly = true,
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { source ->
                            androidx.compose.runtime.LaunchedEffect(source) { }
                        }
                )
                // TODO: кнопка для открытия DatePicker — см. ниже
                
                OutlinedTextField(
                    value = form.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.time)) },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(form.deadlineDate?.let { "📅 $it" } ?: stringResource(R.string.date))
                }
                TextButton(onClick = { showTimePicker = true }) {
                    Text(form.startTime?.let { "🕐 $it" } ?: stringResource(R.string.time))
                }
                if (form.deadlineDate != null || form.startTime != null) {
                    TextButton(onClick = {
                        viewModel.onDeadlineChange(null)
                        viewModel.onStartTimeChange(null)
                    }) {
                        Text(stringResource(R.string.none))
                    }
                }
            }

            // Длительность
            OutlinedTextField(
                value = form.durationMinutes?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.onDurationChange(value.toLongOrNull())
                },
                label = { Text(stringResource(R.string.duration) + " (мин)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Приоритет
            Text(stringResource(R.string.priority), style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
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
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
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
            OutlinedTextField(
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
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    form.tags.forEach { tag ->
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.removeTag(tag) },
                            label = { Text(tag) },
                            trailingIcon = { Icon(Icons.Filled.Close, contentDescription = null) }
                        )
                    }
                }
            }
            OutlinedTextField(
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
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }

    // DatePicker
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

    // TimePicker
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
        OutlinedTextField(
            value = selectedProject?.title ?: stringResource(R.string.no_project),
            onValueChange = {},
            label = { Text(stringResource(R.string.project)) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        androidx.compose.material3.ExposedDropdownMenu(
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
