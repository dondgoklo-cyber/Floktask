package com.taskmanager.presentation.screens.projectdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TaskCard
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

private enum class ProjectViewMode { LIST, KANBAN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onTaskClick: (Long) -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var viewMode by rememberSaveable { mutableStateOf(ProjectViewMode.LIST) }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.project?.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_task),
                onClick = onAddTask
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // View mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                FilterChip(
                    selected = viewMode == ProjectViewMode.LIST,
                    onClick = { viewMode = ProjectViewMode.LIST },
                    label = { Text(stringResource(R.string.view_list)) }
                )
                FilterChip(
                    selected = viewMode == ProjectViewMode.KANBAN,
                    onClick = { viewMode = ProjectViewMode.KANBAN },
                    label = { Text(stringResource(R.string.view_kanban)) }
                )
            }

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) { TaskListSkeleton() }
                }
                state.tasks.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Filled.Add,
                        title = "В этом проекте пока нет задач",
                        subtitle = "Создайте первую задачу в проекте",
                        actionText = stringResource(R.string.add_task),
                        onAction = onAddTask
                    )
                }
                viewMode == ProjectViewMode.LIST -> {
                    ProjectTaskList(
                        tasks = state.tasks,
                        tagColors = state.tagColors,
                        onTaskClick = onTaskClick
                    )
                }
                viewMode == ProjectViewMode.KANBAN -> {
                    ProjectKanbanView(
                        tasks = state.tasks,
                        tagColors = state.tagColors,
                        onMoveTask = { task, status -> viewModel.moveTask(task, status) },
                        onTaskClick = onTaskClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectTaskList(
    tasks: List<Task>,
    tagColors: Map<String, String>,
    onTaskClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items(tasks, key = { it.id ?: 0 }) { task ->
            TaskCard(
                task = task,
                onClick = { task.id?.let(onTaskClick) },
                onCheckedChange = { },
                tagColors = tagColors
            )
        }
    }
}

@Composable
private fun ProjectKanbanView(
    tasks: List<Task>,
    tagColors: Map<String, String>,
    onMoveTask: (Task, TaskStatus) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val columns = TaskStatus.entries.associateWith { status ->
        tasks.filter { it.status == status }
    }

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        items(TaskStatus.entries.toList(), key = { it.name }) { status ->
            val columnTasks = columns[status] ?: emptyList()
            KanbanColumn(
                status = status,
                tasks = columnTasks,
                tagColors = tagColors,
                onMoveTask = { onMoveTask(it, status) },
                onTaskClick = onTaskClick
            )
        }
    }
}

@Composable
private fun KanbanColumn(
    status: TaskStatus,
    tasks: List<Task>,
    tagColors: Map<String, String>,
    onMoveTask: (Task) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val titleRes = when (status) {
        TaskStatus.TODO -> R.string.column_todo
        TaskStatus.IN_PROGRESS -> R.string.column_in_progress
        TaskStatus.DONE -> R.string.column_done
    }
    val accentColor = when (status) {
        TaskStatus.TODO -> AppTheme.colors.info
        TaskStatus.IN_PROGRESS -> AppTheme.colors.warning
        TaskStatus.DONE -> AppTheme.colors.success
    }

    Column(
        modifier = Modifier
            .padding(Spacing.sm)
            .clip(RoundedCornerShape(Radius.md))
            .background(AppTheme.colors.surfaceVariant.copy(alpha = 0.3f))
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
            color = AppTheme.colors.onSurfaceVariant
        )
        tasks.forEach { task ->
            TaskCard(
                task = task,
                onClick = { task.id?.let(onTaskClick) },
                onCheckedChange = { },
                tagColors = tagColors
            )
        }
    }
}
