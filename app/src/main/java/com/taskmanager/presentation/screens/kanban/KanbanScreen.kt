package com.taskmanager.presentation.screens.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.presentation.theme.AppIcons
import com.taskmanager.util.HapticManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    onTaskClick: (Long) -> Unit,
    viewModel: KanbanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val hapticManager = viewModel.hapticManager
    var draggedTaskId by remember { mutableStateOf<Long?>(null) }
    var hoveredColumn by remember { mutableStateOf<TaskStatus?>(null) }
    val columnBounds = remember { mutableMapOf<TaskStatus, Rect>() }

    var showQuickAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.kanban)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = AppIcons.KanbanFabIcon,
                contentDescription = stringResource(R.string.add_task),
                onClick = { showQuickAdd = true },
                onLongClick = viewModel::onFabLongClick,
                hapticManager = hapticManager
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(TaskStatus.entries.toList(), key = { it.name }) { status ->
                    val tasks = state.columns[status] ?: emptyList()
                    val isDropTarget = hoveredColumn == status
                    KanbanColumn(
                        status = status,
                        tasks = tasks,
                        isDropTarget = isDropTarget,
                        draggedTaskId = draggedTaskId,
                        columnBounds = columnBounds,
                        onDragStart = { taskId -> draggedTaskId = taskId },
                        onDraggedTo = { status -> hoveredColumn = status },
                        onDrop = { taskId ->
                            viewModel.moveTask(taskId, status)
                            draggedTaskId = null
                            hoveredColumn = null
                        },
                        onTaskClick = onTaskClick
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    status: TaskStatus,
    tasks: List<Task>,
    isDropTarget: Boolean,
    draggedTaskId: Long?,
    columnBounds: MutableMap<TaskStatus, Rect>,
    onDragStart: (Long) -> Unit,
    onDraggedTo: (TaskStatus) -> Unit,
    onDrop: (Long) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val accentColor = when (status) {
        TaskStatus.TODO -> AppTheme.colors.info
        TaskStatus.IN_PROGRESS -> AppTheme.colors.warning
        TaskStatus.DONE -> AppTheme.colors.success
    }
    val titleRes = when (status) {
        TaskStatus.TODO -> R.string.column_todo
        TaskStatus.IN_PROGRESS -> R.string.column_in_progress
        TaskStatus.DONE -> R.string.column_done
    }

    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(Radius.md))
            .background(AppTheme.colors.surfaceVariant.copy(alpha = if (isDropTarget) 0.5f else 0.3f))
            .border(
                width = if (isDropTarget) 2.dp else 0.dp,
                color = accentColor.copy(alpha = if (isDropTarget) 0.6f else 0f),
                shape = RoundedCornerShape(Radius.md)
            )
            .onGloballyPositioned { coords ->
                val pos = coords.positionInRoot()
                val size = coords.size
                columnBounds[status] = Rect(
                    left = pos.x,
                    top = pos.y,
                    right = pos.x + size.width,
                    bottom = pos.y + size.height
                )
            }
            .pointerInput(status) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {
                        draggedTaskId?.let { id -> onDrop(id) }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val p = change.position
                        columnBounds.entries.firstOrNull { (_, rect) ->
                            rect.contains(p)
                        }?.let { (s, _) -> onDraggedTo(s) }
                    }
                )
            }
            .padding(Spacing.sm)
    ) {
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            "${tasks.size}",
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(tasks, key = { it.id ?: 0 }) { task ->
                KanbanCard(
                    task = task,
                    isBeingDragged = draggedTaskId == task.id,
                    onClick = { task.id?.let(onTaskClick) },
                    onDragStart = { onDragStart(task.id ?: 0) }
                )
            }
        }
    }
}

@Composable
private fun KanbanCard(
    task: Task,
    isBeingDragged: Boolean,
    onClick: () -> Unit,
    onDragStart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { change, _ -> change.consume() }
                )
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isBeingDragged) 8.dp else 1.dp
        ),
        shape = RoundedCornerShape(Radius.sm)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.sm)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (task.isCompleted) AppTheme.colors.onSurfaceVariant
                else AppTheme.colors.onSurface
            )
            Box(
                modifier = Modifier
                    .padding(top = Spacing.xs)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(priorityColor(task.priority))
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )
        }
    }

        
        
        }
    