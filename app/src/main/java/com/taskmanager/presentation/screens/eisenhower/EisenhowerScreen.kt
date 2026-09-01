package com.taskmanager.presentation.screens.eisenhower

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EisenhowerScreen(
    viewModel: EisenhowerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var draggedTaskId by remember { mutableStateOf<Long?>(null) }
    var hoveredQuadrant by remember { mutableStateOf<EisenhowerQuadrant?>(null) }
    val quadrantBounds = remember { mutableMapOf<EisenhowerQuadrant, Rect>() }

    var showQuickAdd by remember { mutableStateOf(false) }
    var selectedQuadrant by remember { mutableStateOf<com.taskmanager.domain.model.EisenhowerQuadrant?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.eisenhower_matrix)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            com.taskmanager.presentation.components.AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_task),
                onClick = { showQuickAdd = true }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Quadrant(
                        quadrant = EisenhowerQuadrant.DO_NOW,
                        tasks = state.quadrants[EisenhowerQuadrant.DO_NOW] ?: emptyList(),
                        color = AppTheme.colors.danger,
                        isDropTarget = hoveredQuadrant == EisenhowerQuadrant.DO_NOW,
                        draggedTaskId = draggedTaskId,
                        quadrantBounds = quadrantBounds,
                        onTaskDraggedTo = { quadrant ->
                            hoveredQuadrant = quadrant
                        },
                        onDrop = { taskId ->
                            viewModel.moveTask(taskId, EisenhowerQuadrant.DO_NOW)
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        onDragStart = { taskId -> draggedTaskId = taskId },
                        onDragEnd = {
                            hoveredQuadrant?.let { target ->
                                draggedTaskId?.let { id -> viewModel.moveTask(id, target) }
                            }
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        quadrant = EisenhowerQuadrant.SCHEDULE,
                        tasks = state.quadrants[EisenhowerQuadrant.SCHEDULE] ?: emptyList(),
                        color = AppTheme.colors.info,
                        isDropTarget = hoveredQuadrant == EisenhowerQuadrant.SCHEDULE,
                        draggedTaskId = draggedTaskId,
                        quadrantBounds = quadrantBounds,
                        onTaskDraggedTo = { quadrant ->
                            hoveredQuadrant = quadrant
                        },
                        onDrop = { taskId ->
                            viewModel.moveTask(taskId, EisenhowerQuadrant.SCHEDULE)
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        onDragStart = { taskId -> draggedTaskId = taskId },
                        onDragEnd = {
                            hoveredQuadrant?.let { target ->
                                draggedTaskId?.let { id -> viewModel.moveTask(id, target) }
                            }
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Quadrant(
                        quadrant = EisenhowerQuadrant.DELEGATE,
                        tasks = state.quadrants[EisenhowerQuadrant.DELEGATE] ?: emptyList(),
                        color = AppTheme.colors.warning,
                        isDropTarget = hoveredQuadrant == EisenhowerQuadrant.DELEGATE,
                        draggedTaskId = draggedTaskId,
                        quadrantBounds = quadrantBounds,
                        onTaskDraggedTo = { quadrant ->
                            hoveredQuadrant = quadrant
                        },
                        onDrop = { taskId ->
                            viewModel.moveTask(taskId, EisenhowerQuadrant.DELEGATE)
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        onDragStart = { taskId -> draggedTaskId = taskId },
                        onDragEnd = {
                            hoveredQuadrant?.let { target ->
                                draggedTaskId?.let { id -> viewModel.moveTask(id, target) }
                            }
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        quadrant = EisenhowerQuadrant.ELIMINATE,
                        tasks = state.quadrants[EisenhowerQuadrant.ELIMINATE] ?: emptyList(),
                        color = AppTheme.colors.outline,
                        isDropTarget = hoveredQuadrant == EisenhowerQuadrant.ELIMINATE,
                        draggedTaskId = draggedTaskId,
                        quadrantBounds = quadrantBounds,
                        onTaskDraggedTo = { quadrant ->
                            hoveredQuadrant = quadrant
                        },
                        onDrop = { taskId ->
                            viewModel.moveTask(taskId, EisenhowerQuadrant.ELIMINATE)
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        onDragStart = { taskId -> draggedTaskId = taskId },
                        onDragEnd = {
                            hoveredQuadrant?.let { target ->
                                draggedTaskId?.let { id -> viewModel.moveTask(id, target) }
                            }
                            draggedTaskId = null
                            hoveredQuadrant = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun Quadrant(
    quadrant: EisenhowerQuadrant,
    tasks: List<Task>,
    color: androidx.compose.ui.graphics.Color,
    isDropTarget: Boolean,
    draggedTaskId: Long?,
    quadrantBounds: MutableMap<EisenhowerQuadrant, Rect>,
    onTaskDraggedTo: (EisenhowerQuadrant) -> Unit,
    onDrop: (Long) -> Unit,
    onDragStart: (Long) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (titleRes, descRes) = when (quadrant) {
        EisenhowerQuadrant.DO_NOW -> R.string.do_now to R.string.important_urgent
        EisenhowerQuadrant.SCHEDULE -> R.string.schedule to R.string.important_not_urgent
        EisenhowerQuadrant.DELEGATE -> R.string.delegate to R.string.not_important_urgent
        EisenhowerQuadrant.ELIMINATE -> R.string.eliminate to R.string.not_important_not_urgent
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(color.copy(alpha = if (isDropTarget) 0.18f else 0.05f))
            .border(
                width = if (isDropTarget) 2.dp else 1.dp,
                color = color.copy(alpha = if (isDropTarget) 0.6f else 0.2f),
                shape = RoundedCornerShape(Radius.md)
            )
            .onGloballyPositioned { coords ->
                val pos = coords.positionInRoot()
                val size = coords.size
                quadrantBounds[quadrant] = Rect(
                    left = pos.x,
                    top = pos.y,
                    right = pos.x + size.width,
                    bottom = pos.y + size.height
                )
            }
            .pointerInput(quadrant) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        // Determine which task is under the pointer if any
                        draggedTaskId?.let { onTaskDraggedTo(quadrant) }
                    },
                    onDragEnd = {
                        draggedTaskId?.let { id ->
                            quadrantBounds[quadrant]?.let { /* quadrant is current target */ }
                            onDrop(id)
                        }
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Update hovered quadrant based on current pointer position
                        val current = change.position
                        val rootX = current.x
                        val rootY = current.y
                        quadrantBounds.entries.firstOrNull { (_, rect) ->
                            rect.contains(androidx.compose.ui.geometry.Offset(rootX, rootY))
                        }?.let { (q, _) -> onTaskDraggedTo(q) }
                    }
                )
            }
            .padding(Spacing.sm)
    ) {
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            stringResource(descRes),
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.onSurfaceVariant
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(tasks, key = { it.id ?: 0 }) { task ->
                TaskQuadrantItem(
                    task = task,
                    isBeingDragged = draggedTaskId == task.id,
                    onDragStart = { onDragStart(task.id ?: 0) },
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

@Composable
private fun TaskQuadrantItem(
    task: Task,
    isBeingDragged: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDrag = { change, dragAmount -> change.consume() }
                )
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isBeingDragged) 8.dp else 1.dp
        ),
        shape = RoundedCornerShape(Radius.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .padding(end = Spacing.xs)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(priorityColor(task.priority))
                    .padding(horizontal = 4.dp)
            ) {
                Text(" ", style = MaterialTheme.typography.labelSmall)
            }
            Text(
                task.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }

        
        if (showQuickAdd) {
            com.taskmanager.presentation.screens.tasks.QuickAddSheet(
                onDismiss = { showQuickAdd = false },
                onCreated = { showQuickAdd = false }
            )
        }
    }
    