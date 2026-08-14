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
import androidx.compose.ui.input.pointer.pointerInput
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.eisenhower_matrix)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
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
                        onDrop = { taskId -> viewModel.moveTask(taskId, EisenhowerQuadrant.DO_NOW) },
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        quadrant = EisenhowerQuadrant.SCHEDULE,
                        tasks = state.quadrants[EisenhowerQuadrant.SCHEDULE] ?: emptyList(),
                        color = AppTheme.colors.info,
                        onDrop = { taskId -> viewModel.moveTask(taskId, EisenhowerQuadrant.SCHEDULE) },
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
                        onDrop = { taskId -> viewModel.moveTask(taskId, EisenhowerQuadrant.DELEGATE) },
                        modifier = Modifier.weight(1f)
                    )
                    Quadrant(
                        quadrant = EisenhowerQuadrant.ELIMINATE,
                        tasks = state.quadrants[EisenhowerQuadrant.ELIMINATE] ?: emptyList(),
                        color = AppTheme.colors.outline,
                        onDrop = { taskId -> viewModel.moveTask(taskId, EisenhowerQuadrant.ELIMINATE) },
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
    onDrop: (Long) -> Unit,
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
            .background(color.copy(alpha = 0.05f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(Radius.md))
            .padding(Spacing.sm)
            .pointerInput(quadrant) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {},
                    onDrag = { _, _ -> }
                )
            }
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
                    accentColor = color,
                    onDragStart = { /* drag tracking в полной реализации */ }
                )
            }
        }
    }
}

@Composable
private fun TaskQuadrantItem(
    task: Task,
    accentColor: androidx.compose.ui.graphics.Color,
    onDragStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { _, _ -> }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
}
