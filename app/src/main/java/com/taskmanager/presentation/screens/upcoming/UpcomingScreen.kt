package com.taskmanager.presentation.screens.upcoming

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TaskCard
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.screens.tasks.QuickAddSheet
import com.taskmanager.presentation.screens.tasks.TaskDetailSheet
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(
    onEditTask: (Long) -> Unit,
    viewModel: UpcomingViewModel = hiltViewModel()
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
                onEditTask(id)
            },
            onStartFocus = { id ->
                detailTaskId = null
                onEditTask(id)
            }
        )
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onCreated = { _ -> showQuickAdd = false }
        )
    }

    val grouped = remember(state.tasks) { groupByDay(state.tasks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.upcoming)) },
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
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    TaskListSkeleton()
                }
            }
            grouped.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.Event,
                    title = stringResource(R.string.upcoming_empty_title),
                    subtitle = stringResource(R.string.upcoming_empty_subtitle),
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    grouped.forEach { (day, tasks) ->
                        item(key = "header-$day") {
                            DayHeader(date = day)
                        }
                        items(tasks.size, key = { tasks[it].id ?: 0 }) { index ->
                            val task = tasks[index]
                            TaskCard(
                                task = task,
                                onClick = { detailTaskId = task.id ?: 0 },
                                onCheckedChange = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate) {
    val today = LocalDate.now()
    val label = when (ChronoUnit.DAYS.between(today, date)) {
        0L -> stringResource(R.string.today_label)
        1L -> stringResource(R.string.tomorrow_label)
        else -> date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale("ru"))
        )
    }
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colors.primary,
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.md, bottom = Spacing.xs)
    )
}

private fun groupByDay(tasks: List<Task>): List<Pair<LocalDate, List<Task>>> {
    val zone = ZoneId.systemDefault()
    return tasks
        .filter { it.startTime != null || it.deadline != null }
        .groupBy { task ->
            task.startTime?.atZone(zone)?.toLocalDate()
                ?: task.deadline?.atZone(zone)?.toLocalDate()
                ?: LocalDate.now()
        }
        .toSortedMap()
        .map { (day, list) ->
            day to list.sortedWith(
                compareBy(nullsLast()) { it.startTime }
            )
        }
}
