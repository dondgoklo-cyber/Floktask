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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
                    onAddSubtask = { title, parentId -> viewModel.addSubtask(task.id ?: 0, title, parentId) },
                    onToggleSubtask = { viewModel.toggleSubtask(it) },
                    onDeleteSubtask = { viewModel.deleteSubtask(it) },
                    onRenameSubtask = { subtask, title -> viewModel.renameSubtask(subtask, title) },
                    onReorderSubtask = { from, to ->
                        viewModel.reorderSubtask(task.id ?: 0, from, to)
                    }
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
    onAddSubtask: (String, Long?) -> Unit,
    onToggleSubtask: (Subtask) -> Unit,
    onDeleteSubtask: (Subtask) -> Unit,
    onRenameSubtask: (Subtask, String) -> Unit,
    onReorderSubtask: (Int, Int) -> Unit
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

        // Прогресс подзадач
        if (subtasks.isNotEmpty()) {
            item { SubtaskProgress(subtasks) }
        }

        // Подзадачи
        item {
            SubtaskSection(
                subtasks = subtasks,
                depth = 0,
                onAdd = onAddSubtask,
                onToggle = onToggleSubtask,
                onDelete = onDeleteSubtask,
                onRename = onRenameSubtask
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
private fun SubtaskProgress(subtasks: List<Subtask>) {
    val completed = subtasks.count { it.isCompleted }
    val total = subtasks.size
    val progress = if (total > 0) completed.toFloat() / total else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            stringResource(R.string.subtask_progress),
            style = MaterialTheme.typography.labelLarge,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            stringResource(R.string.completed_subtasks, completed, total),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.primary
        )
    }
    androidx.compose.material3.LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.xs)
            .height(6.dp)
            .clip(RoundedCornerShape(Radius.full)),
        color = AppTheme.colors.primary,
        trackColor = AppTheme.colors.surfaceVariant
    )
}

@Composable
private fun SubtaskSection(
    subtasks: List<Subtask>,
    depth: Int,
    onAdd: (String, Long?) -> Unit,
    onToggle: (Subtask) -> Unit,
    onDelete: (Subtask) -> Unit,
    onRename: (Subtask, String) -> Unit
) {
    var newSubtaskTitle by remember { mutableStateOf("") }
    var editingSubtask by remember { mutableStateOf<Subtask?>(null) }
    var editingTitle by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        if (depth == 0) {
            Text(
                stringResource(R.string.subtasks),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        subtasks.forEach { subtask ->
            if (editingSubtask?.id == subtask.id) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.padding(start = (depth * 16).dp)
                ) {
                    OutlinedTextField(
                        value = editingTitle,
                        onValueChange = { editingTitle = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    onRename(subtask, editingTitle)
                                    editingSubtask = null
                                },
                                enabled = editingTitle.isNotBlank()
                            ) { Icon(Icons.Filled.Check, contentDescription = null) }
                        }
                    )
                    IconButton(onClick = { editingSubtask = null }) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline)
                    }
                }
            } else {
                SubtaskRow(
                    subtask = subtask,
                    depth = depth,
                    onToggle = { onToggle(subtask) },
                    onEdit = {
                        editingSubtask = subtask
                        editingTitle = subtask.title
                    },
                    onDelete = { onDelete(subtask) },
                    onAddChild = { title -> onAdd(title, subtask.id) }
                )
            }
            // Рекурсивный рендеринг дочерних подзадач (до 5 уровней)
            if (subtask.children.isNotEmpty() && depth < 4) {
                Column(modifier = Modifier.padding(start = (depth + 1) * 16.dp)) {
                    SubtaskSection(
                        subtasks = subtask.children,
                        depth = depth + 1,
                        onAdd = onAdd,
                        onToggle = onToggle,
                        onDelete = onDelete,
                        onRename = onRename
                    )
                }
            }
        }
        // Поле добавления подзадачи на текущем уровне
        OutlinedTextField(
            value = newSubtaskTitle,
            onValueChange = { newSubtaskTitle = it },
            placeholder = {
                Text(
                    if (depth == 0) "Добавить подзадачу..." else "Добавить вложенную...",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (depth * 16).dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        val parentId = if (depth == 0) null else null
                        onAdd(newSubtaskTitle, parentId)
                        newSubtaskTitle = ""
                    },
                    enabled = newSubtaskTitle.isNotBlank()
                ) { Icon(Icons.Filled.Add, contentDescription = null) }
            }
        )
    }
}

@Composable
private fun SubtaskRow(
    subtask: Subtask,
    depth: Int,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddChild: (String) -> Unit
) {
    var showAddChild by remember { mutableStateOf(false) }
    var childTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(start = (depth * 16).dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (subtask.isCompleted) Icons.Filled.CheckCircle
                    else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (subtask.isCompleted) AppTheme.colors.success
                    else AppTheme.colors.outline
                )
            }
            Text(
                subtask.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = if (subtask.isCompleted) AppTheme.colors.onSurfaceVariant
                else AppTheme.colors.onSurface
            )
            // Кнопка добавления дочерней подзадачи (до 4 уровня вложенности)
            if (depth < 4) {
                IconButton(onClick = { showAddChild = !showAddChild }) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = AppTheme.colors.outline)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_subtask), tint = AppTheme.colors.outline)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = AppTheme.colors.outline)
            }
        }
        if (showAddChild && depth < 4) {
            OutlinedTextField(
                value = childTitle,
                onValueChange = { childTitle = it },
                placeholder = { Text("Вложенная подзадача...", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(start = Spacing.lg),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (childTitle.isNotBlank()) {
                                onAddChild(childTitle)
                                childTitle = ""
                                showAddChild = false
                            }
                        },
                        enabled = childTitle.isNotBlank()
                    ) { Icon(Icons.Filled.Check, contentDescription = null) }
                }
            )
        }
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
