package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.PriorityBadge
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    taskId: Long,
    onDismiss: () -> Unit,
    onEdit: (Long) -> Unit,
    onStartFocus: (Long) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Загрузка задачи при первом показе
    androidx.compose.runtime.LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = Radius.xl, topEnd = Radius.xl)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            val task = state.task
            if (task != null) {
                TaskDetailContent(
                    task = task,
                    projectName = state.projectName,
                    subtasks = state.subtasks,
                    onToggleComplete = { viewModel.toggleComplete(task) },
                    onEdit = { onEdit(task.id ?: 0) },
                    onStartFocus = { onStartFocus(task.id ?: 0) },
                    onAddSubtask = { title -> viewModel.addSubtask(task.id ?: 0, title) },
                    onToggleSubtask = { viewModel.toggleSubtask(it) },
                    onDeleteSubtask = { viewModel.deleteSubtask(it) }
                )
            }
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    projectName: String?,
    subtasks: List<Subtask>,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onStartFocus: () -> Unit,
    onAddSubtask: (String) -> Unit,
    onToggleSubtask: (Subtask) -> Unit,
    onDeleteSubtask: (Subtask) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = Spacing.xl,
            end = Spacing.xl,
            bottom = Spacing.xxxl
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Заголовок + действия
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                IconButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle
                        else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = stringResource(R.string.complete),
                        tint = if (task.isCompleted) AppTheme.colors.success else AppTheme.colors.outline
                    )
                }
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Описание
        task.description?.takeIf { it.isNotBlank() }?.let {
            item {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }

        // Метаданные: проект, дата, время, приоритет
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(Radius.lg)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    DetailRow(
                        icon = Icons.Filled.Folder,
                        label = stringResource(R.string.project),
                        value = projectName ?: stringResource(R.string.no_project)
                    )
                    task.deadline?.let { deadline ->
                        DetailRow(
                            icon = Icons.Filled.CalendarMonth,
                            label = stringResource(R.string.deadline),
                            value = deadline
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                        )
                    }
                    task.startTime?.let { start ->
                        DetailRow(
                            icon = Icons.Filled.Schedule,
                            label = stringResource(R.string.time),
                            value = start
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                    task.durationMinutes?.let { duration ->
                        DetailRow(
                            icon = Icons.Filled.Schedule,
                            label = stringResource(R.string.duration),
                            value = formatDuration(duration)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, tint = AppTheme.colors.outline)
                        Text(
                            stringResource(R.string.priority),
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                        PriorityBadge(task.priority)
                    }
                    task.pomodoroEstimate?.let { estimate ->
                        DetailRow(
                            icon = Icons.Filled.Bolt,
                            label = stringResource(R.string.pomodoro_estimate),
                            value = "$estimate 🍅"
                        )
                    }
                }
            }
        }

        // Теги
        if (task.tags.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        stringResource(R.string.tags),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        task.tags.forEach { tag ->
                            TagChip(tag)
                        }
                    }
                }
            }
        }

        // Подзадачи
        item {
            SubtaskSection(
                subtasks = subtasks,
                onAdd = onAddSubtask,
                onToggle = onToggleSubtask,
                onDelete = onDeleteSubtask
            )
        }

        // Кнопки действий
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Button(
                    onClick = onStartFocus,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null)
                    Text(stringResource(R.string.start_pomodoro))
                }
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.edit_task))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Icon(icon, contentDescription = null, tint = AppTheme.colors.outline)
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun TagChip(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(AppTheme.colors.surfaceVariant)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Icon(Icons.Filled.Tag, contentDescription = null, tint = AppTheme.colors.onSurfaceVariant)
        Text(name, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SubtaskSection(
    subtasks: List<Subtask>,
    onAdd: (String) -> Unit,
    onToggle: (Subtask) -> Unit,
    onDelete: (Subtask) -> Unit
) {
    var newSubtaskTitle by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            stringResource(R.string.subtasks),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        subtasks.forEach { subtask ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                IconButton(onClick = { onToggle(subtask) }) {
                    Icon(
                        imageVector = if (subtask.isCompleted) Icons.Filled.CheckCircle
                        else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (subtask.isCompleted) AppTheme.colors.success else AppTheme.colors.outline
                    )
                }
                Text(
                    subtask.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    color = if (subtask.isCompleted) AppTheme.colors.onSurfaceVariant
                    else AppTheme.colors.onSurface
                )
                IconButton(onClick = { onDelete(subtask) }) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = AppTheme.colors.outline)
                }
            }
        }
        OutlinedTextField(
            value = newSubtaskTitle,
            onValueChange = { newSubtaskTitle = it },
            placeholder = { Text("Добавить подзадачу...") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAdd(newSubtaskTitle)
                        newSubtaskTitle = ""
                    },
                    enabled = newSubtaskTitle.isNotBlank()
                ) { Icon(Icons.Filled.Add, contentDescription = null) }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatDuration(minutes: Long): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h > 0 && m > 0 -> "$h ч $m мин"
        h > 0 -> "$h ч"
        else -> "$m мин"
    }
}
