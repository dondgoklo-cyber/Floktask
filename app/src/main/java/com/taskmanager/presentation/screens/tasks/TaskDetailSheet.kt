package com.taskmanager.presentation.screens.tasks

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.model.Task
import com.taskmanager.haptic.HapticType
import com.taskmanager.haptic.rememberHaptic
import com.taskmanager.presentation.R
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.AppIcon
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.PriorityIndicator
import com.taskmanager.presentation.components.StatusBadge
import com.taskmanager.presentation.components.TaskCard
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.utils.toLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: Task,
    viewModel: TaskDetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onEdit: (Long) -> Unit,
    onStartFocus: (Long) -> Unit,
    onNoteClick: (Long) -> Unit = {},
    state: TaskDetailViewModel.TaskDetailState = viewModel.state.value
) {
    val haptic = rememberHaptic()
    val context = LocalContext.current

    LaunchedEffect(task.id) {
        viewModel.loadTask(task.id ?: 0)
        viewModel.loadSubtasks(task.id ?: 0)
        viewModel.loadRelatedNotes(task.id ?: 0)
    }

    if (state.task != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = AppTheme.colors.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = Spacing.m)
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppTheme.colors.outline)
                )
            }
        ) {
            TaskDetailContent(
                task = state.task!!,
                subtasks = state.subtasks,
                state = state,
                onToggleComplete = { viewModel.toggleComplete(state.task!!) },
                onEdit = { onEdit(state.task!!.id ?: 0) },
                onStartFocus = { onStartFocus(state.task!!.id ?: 0) },
                onAddSubtask = { title, parentId -> viewModel.addSubtask(state.task!!.id ?: 0, title, parentId) },
                onToggleSubtask = { viewModel.toggleSubtask(it) },
                onDeleteSubtask = { viewModel.deleteSubtask(it) },
                onReorderSubtask = { from, to ->
                    viewModel.reorderSubtasks(state.task!!.id ?: 0, listOf(from.toLong(), to.toLong()))
                },
                onNoteClick = onNoteClick
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    subtasks: List<Subtask>,
    state: TaskDetailViewModel.TaskDetailState,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onStartFocus: () -> Unit,
    onAddSubtask: (String, Long?) -> Unit,
    onToggleSubtask: (Subtask) -> Unit,
    onDeleteSubtask: (Subtask) -> Unit,
    onReorderSubtask: (Int, Int) -> Unit,
    relatedNotes: List<Note> = emptyList(),
    onNoteClick: (Long) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = Spacing.xl,
            end = Spacing.xl,
            bottom = Spacing.xxxl
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.l),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityIndicator(
                        priority = task.priority,
                        modifier = Modifier.padding(end = Spacing.s)
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        task.deadline?.toLocalDate()?.let { date ->
                            Text(
                                text = date.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                    StatusBadge(
                        status = task.status,
                        modifier = Modifier.padding(start = Spacing.s)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.l))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onToggleComplete) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Filled.Refresh else Icons.Filled.Check,
                                contentDescription = null,
                                tint = if (task.isCompleted) AppTheme.colors.outline else AppTheme.colors.primary
                            )
                        }
                        IconButton(onClick = onStartFocus) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                tint = AppTheme.colors.primary
                            )
                        }
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = AppTheme.colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                if (task.description?.isNotBlank() == true) {
                    Text(
                        text = task.description!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.l))
                }

                if (task.tags?.isNotBlank() == true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        task.tags!!.split(",").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Radius.m))
                                    .background(AppTheme.colors.surfaceVariant)
                                    .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                            ) {
                                Text(
                                    text = tag.trim(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.l))
                }
            }
        }

        // Subtasks
        if (subtasks.isNotEmpty()) {
            item {
                SubtaskSection(
                    subtasks = subtasks.filter { it.parentSubtaskId == null },
                    depth = 0,
                    onAdd = onAddSubtask,
                    onToggle = onToggleSubtask,
                    onDelete = onDeleteSubtask
                )
            }
        }

        // Related notes
        if (relatedNotes.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.related_notes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            relatedNotes.take(5).forEach { note ->
                item {
                    Card(
                        onClick = { onNoteClick(note.id ?: 0) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs)
                            .clip(RoundedCornerShape(Radius.m)),
                        colors = CardDefaults.cardColors(
                            containerColor = AppTheme.colors.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.m)
                        ) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            note.content?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtaskSection(
    subtasks: List<Subtask>,
    depth: Int,
    onAdd: (String, Long?) -> Unit,
    onToggle: (Subtask) -> Unit,
    onDelete: (Subtask) -> Unit
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
            SubtaskRow(
                subtask = subtask,
                depth = depth,
                onToggle = onToggle,
                onDelete = onDelete,
                onEdit = { s, title ->
                    editingSubtask = s
                    editingTitle = title
                }
            )

            if (editingSubtask == subtask) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = editingTitle,
                        onValueChange = { editingTitle = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.enter_subtask_title)) }
                    )
                    IconButton(
                        onClick = { editingSubtask = null },
                        enabled = editingTitle.isNotBlank()
                    ) { Icon(Icons.Filled.Check, contentDescription = null) }
                    IconButton(onClick = { editingSubtask = null }) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = AppTheme.colors.outline)
                    }
                }
            }

            if (subtask.children.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = ((depth + 1) * 16).dp)) {
                    SubtaskSection(
                        subtasks = subtask.children,
                        depth = depth + 1,
                        onAdd = onAdd,
                        onToggle = onToggle,
                        onDelete = onDelete
                    )
                }
            }
        }
        // Add new subtask field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSubtaskTitle,
                onValueChange = { newSubtaskTitle = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.add_subtask)) }
            )
            IconButton(
                onClick = {
                    if (newSubtaskTitle.isNotBlank()) {
                        onAdd(newSubtaskTitle, null)
                        newSubtaskTitle = ""
                    }
                },
                enabled = newSubtaskTitle.isNotBlank()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun SubtaskRow(
    subtask: Subtask,
    depth: Int,
    onToggle: (Subtask) -> Unit,
    onDelete: (Subtask) -> Unit,
    onEdit: (Subtask, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.s))
            .clickable { onToggle(subtask) }
            .background(
                if (subtask.isCompleted) AppTheme.colors.surfaceVariant
                else AppTheme.colors.surface
            )
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (subtask.isCompleted) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (subtask.isCompleted) AppTheme.colors.primary else AppTheme.colors.outline
        )
        Text(
            text = subtask.title,
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.s),
            style = MaterialTheme.typography.bodyMedium,
            color = if (subtask.isCompleted) AppTheme.colors.onSurfaceVariant else AppTheme.colors.onSurface
        )
        IconButton(onClick = { onEdit(subtask, subtask.title) }) {
            Icon(Icons.Filled.Edit, contentDescription = null)
        }
        IconButton(onClick = { onDelete(subtask) }) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = AppTheme.colors.error)
        }
    }
}
