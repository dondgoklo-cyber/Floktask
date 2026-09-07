package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.R
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onCreated: (Long) -> Unit,
    viewModel: QuickAddViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val availableTags by viewModel.tags.collectAsStateWithLifecycle()

    var selectedPriority by remember { mutableStateOf(Priority.NONE) }
    var selectedProjectId by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showProjectMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = Radius.xl,
            topEnd = Radius.xl
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                stringResource(R.string.add_task),
                style = MaterialTheme.typography.titleMedium
            )
            AppTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = {
                    Text("Позвонить клиенту завтра в 15:00 на час...")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val parsed = QuickAddParser.parse(input)
                            val finalDate = selectedDate ?: parsed.deadlineDate
                            val finalTime = selectedTime ?: parsed.startTime
                            val finalParsed = parsed.copy(
                                deadlineDate = finalDate,
                                startTime = finalTime
                            )
                            viewModel.createTask(
                                parsed = finalParsed,
                                projectId = selectedProjectId,
                                priority = selectedPriority,
                                tags = selectedTags.toList(),
                                onCreated = { id -> onCreated(id) }
                            )
                        },
                        enabled = input.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    }
                }
            )
            Text(
                "Подсказки: завтра, сегодня, 15:00, на час, на 30 мин",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Flag, contentDescription = null, tint = AppTheme.colors.outline, modifier = Modifier.size(20.dp))
                Priority.entries.forEach { priority ->
                    val label = when (priority) {
                        Priority.HIGH -> "P1"
                        Priority.MEDIUM -> "P2"
                        Priority.LOW -> "P3"
                        Priority.NONE -> "Нет"
                    }
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = if (selectedPriority == priority) Priority.NONE else priority },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = AppTheme.colors.outline, modifier = Modifier.size(20.dp))
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        selectedDate?.format(DateTimeFormatter.ofPattern("d MMM")) ?: "Дата",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedDate != null) AppTheme.colors.onSurface else AppTheme.colors.onSurfaceVariant
                    )
                }
                if (selectedDate != null) {
                    IconButton(onClick = { selectedDate = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline, modifier = Modifier.size(16.dp))
                    }
                }
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = AppTheme.colors.outline, modifier = Modifier.size(20.dp))
                TextButton(onClick = { showTimePicker = true }) {
                    Text(
                        selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Время",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedTime != null) AppTheme.colors.onSurface else AppTheme.colors.onSurfaceVariant
                    )
                }
                if (selectedTime != null) {
                    IconButton(onClick = { selectedTime = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline, modifier = Modifier.size(16.dp))
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = showProjectMenu,
                onExpandedChange = { showProjectMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTextField(
                    value = projects.find { it.id == selectedProjectId }?.title ?: "Без проекта",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.Folder, contentDescription = null, tint = AppTheme.colors.outline) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProjectMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    singleLine = true
                )
                androidx.compose.material3.DropdownMenu(
                    expanded = showProjectMenu,
                    onDismissRequest = { showProjectMenu = false }
                ) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Без проекта") },
                        onClick = { selectedProjectId = null; showProjectMenu = false }
                    )
                    projects.forEach { project ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(project.title) },
                            onClick = { selectedProjectId = project.id; showProjectMenu = false }
                        )
                    }
                }
            }

            if (availableTags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    availableTags.forEach { tag ->
                        FilterChip(
                            selected = tag.name in selectedTags,
                            onClick = {
                                selectedTags = if (tag.name in selectedTags) {
                                    selectedTags - tag.name
                                } else {
                                    selectedTags + tag.name
                                }
                            },
                            label = { Text(tag.name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialDate = selectedDate ?: LocalDate.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val initialTime = selectedTime ?: LocalTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) { TimePicker(state = timePickerState) }
    }
}
