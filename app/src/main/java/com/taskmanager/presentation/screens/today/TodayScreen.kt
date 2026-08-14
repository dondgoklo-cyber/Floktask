package com.taskmanager.presentation.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.screens.tasks.TaskDetailSheet
import com.taskmanager.presentation.screens.tasks.QuickAddSheet
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel(),
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    onStartFocus: (Long) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var detailTaskId by remember { mutableStateOf<Long?>(null) }
    var showQuickAdd by remember { mutableStateOf(false) }

    if (detailTaskId != null) {
        TaskDetailSheet(
            taskId = detailTaskId!!,
            onDismiss = { detailTaskId = null },
            onEdit = { id ->
                detailTaskId = null
                onTaskClick(id)
            },
            onStartFocus = onStartFocus
        )
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onCreated = { _ ->
                showQuickAdd = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showQuickAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item { GreetingHeader() }
                item { ProgressCard(state) }
                item { SummaryRow(state) }
                if (state.nextTasks.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.next_tasks)) }
                    items(state.nextTasks, key = { it.id ?: 0 }) { task ->
                        NextTaskRow(task, onClick = { detailTaskId = task.id ?: 0 })
                    }
                }
                if (state.focusMinutesToday > 0) {
                    item { FocusCard(state) }
                }
                if (state.habitsTotal > 0) {
                    item { HabitsCard(state) }
                }
            }
        }
    }
}

@Composable
private fun GreetingHeader() {
    val hour = LocalTime.now().hour
    val greeting = when (hour) {
        in 5..11 -> stringResource(R.string.good_morning)
        in 12..17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_evening)
    }
    Text(
        text = greeting,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colors.onBackground
    )
}

@Composable
private fun ProgressCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Column(Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.todays_progress),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.primary
                )
            }
            Spacer(Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = state.progress,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(Radius.full)),
                color = AppTheme.colors.primary,
                trackColor = AppTheme.colors.surfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryRow(state: TodayUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.totalToday.toString(),
            label = stringResource(R.string.tasks_total),
            color = AppTheme.colors.info
        )
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.completedToday.toString(),
            label = stringResource(R.string.tasks_completed),
            color = AppTheme.colors.success
        )
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.overdueCount.toString(),
            label = stringResource(R.string.tasks_overdue),
            color = AppTheme.colors.danger
        )
    }
}

@Composable
private fun SummaryStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
        shape = RoundedCornerShape(Radius.md)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(color)
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurface
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xs)
    )
}

@Composable
private fun NextTaskRow(task: Task, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
        shape = RoundedCornerShape(Radius.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            task.startTime?.let { start ->
                val time = start.atZone(ZoneId.systemDefault()).toLocalTime()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.primary
                    )
                    task.durationMinutes?.let { mins ->
                        Text(
                            "${mins}м",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            } ?: Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = AppTheme.colors.outline
            )
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape).background(priorityColor(task.priority))
            )
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = AppTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun FocusCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = AppTheme.colors.primary)
            }
            Column {
                Text(
                    stringResource(R.string.focus_time_today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
                Text(
                    "${state.focusMinutesToday / 60}ч ${state.focusMinutesToday % 60}м",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun HabitsCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Column(Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.habits_today),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${state.habitsCompleted}/${state.habitsTotal}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.primary
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                repeat(state.habitsTotal) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < state.habitsCompleted) AppTheme.colors.success
                                else AppTheme.colors.surfaceVariant
                            )
                    )
                }
            }
        }
    }
}
