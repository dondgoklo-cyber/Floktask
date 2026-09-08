package com.taskmanager.presentation.screens.projectdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.EmptyState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.taskmanager.presentation.components.TaskCard
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private enum class ProjectViewMode { LIST, KANBAN, EISENHOWER, GANTT, NOTES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onNoteClick: (Long) -> Unit = {},
    onEditTask: (Long) -> Unit = {},
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
                contentDescription = if (viewMode == ProjectViewMode.NOTES) stringResource(R.string.add_note) else stringResource(R.string.add_task),
                onClick = {
                    if (viewMode == ProjectViewMode.NOTES) {
                        viewModel.createNoteForProject("") { id -> onNoteClick(id) }
                    } else {
                        onAddTask()
                    }
                }
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
                FilterChip(
                    selected = viewMode == ProjectViewMode.EISENHOWER,
                    onClick = { viewMode = ProjectViewMode.EISENHOWER },
                    label = { Text(stringResource(R.string.eisenhower_matrix)) }
                )
                FilterChip(
                    selected = viewMode == ProjectViewMode.GANTT,
                    onClick = { viewMode = ProjectViewMode.GANTT },
                    label = { Text(stringResource(R.string.view_gantt)) }
                )
                FilterChip(
                    selected = viewMode == ProjectViewMode.NOTES,
                    onClick = { viewMode = ProjectViewMode.NOTES },
                    label = { Text(stringResource(R.string.notes)) }
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
                        message = "Создайте первую задачу в проекте",
                        actionLabel = stringResource(R.string.add_task),
                        onAction = onAddTask
                    )
                }
                viewMode == ProjectViewMode.LIST -> {
                    ProjectTaskList(
                        tasks = state.tasks,
                        onTaskClick = onTaskClick,
                        onEditTask = onEditTask
                    )
                }
                viewMode == ProjectViewMode.KANBAN -> {
                    ProjectKanbanView(
                        tasks = state.tasks,
                        onMoveTask = { task, status -> viewModel.moveTask(task, status) },
                        onTaskClick = onTaskClick
                    )
                }
                viewMode == ProjectViewMode.EISENHOWER -> {
                    ProjectEisenhowerView(
                        tasks = state.tasks,
                        onTaskClick = onTaskClick
                    )
                }
                viewMode == ProjectViewMode.GANTT -> {
                    ProjectGanttView(
                        tasks = state.tasks,
                        onTaskClick = onTaskClick
                    )
                }
                viewMode == ProjectViewMode.NOTES -> {
                    ProjectNotesList(
                        notes = state.notes,
                        onNoteClick = onNoteClick,
                        onCreateNote = { viewModel.createNoteForProject("") { id -> onNoteClick(id) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectTaskList(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onEditTask: (Long) -> Unit
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
                onCheckedChange = { checked ->
                    if (checked) task.id?.let { /* viewModel.completeTask(it) */ }
                },
                onEditTask = onEditTask
            )
        }
    }
}

@Composable
private fun ProjectKanbanView(
    tasks: List<Task>,
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
                onCheckedChange = { }
            )
        }
    }
}

@Composable
private fun ProjectEisenhowerView(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit
) {
    val quadrants = EisenhowerQuadrant.entries.associateWith { q ->
        tasks.filter { it.eisenhowerQuadrant == q }
    }
    val unassigned = tasks.filter { it.eisenhowerQuadrant == null }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        if (unassigned.isNotEmpty()) {
            item {
                Text(
                    "Без квадранта",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
            items(unassigned, key = { it.id ?: 0 }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
        val doNow = quadrants[EisenhowerQuadrant.DO_NOW] ?: emptyList()
        if (doNow.isNotEmpty()) {
            item { QuadrantHeader(R.string.do_now, AppTheme.colors.danger, doNow.size) }
            items(doNow, key = { it.id ?: 0 }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
        val schedule = quadrants[EisenhowerQuadrant.SCHEDULE] ?: emptyList()
        if (schedule.isNotEmpty()) {
            item { QuadrantHeader(R.string.schedule, AppTheme.colors.info, schedule.size) }
            items(schedule, key = { it.id ?: 0 }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
        val delegate = quadrants[EisenhowerQuadrant.DELEGATE] ?: emptyList()
        if (delegate.isNotEmpty()) {
            item { QuadrantHeader(R.string.delegate, AppTheme.colors.warning, delegate.size) }
            items(delegate, key = { it.id ?: 0 }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
        val eliminate = quadrants[EisenhowerQuadrant.ELIMINATE] ?: emptyList()
        if (eliminate.isNotEmpty()) {
            item { QuadrantHeader(R.string.eliminate, AppTheme.colors.outline, eliminate.size) }
            items(eliminate, key = { it.id ?: 0 }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
    }
}

@Composable
private fun QuadrantHeader(
    titleRes: Int,
    color: androidx.compose.ui.graphics.Color,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.full))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = Spacing.sm, vertical = 2.dp)
        ) {
            Text(
                stringResource(titleRes),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            "$count",
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.onSurfaceVariant
        )
    }
}

@Composable
private fun ProjectGanttView(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit
) {
    val zoneId = ZoneId.systemDefault()
    val now = remember { Instant.now() }

    val datedTasks = tasks.filter { it.startTime != null || it.deadline != null }
    val undatedTasks = tasks.filter { it.startTime == null && it.deadline == null }

    if (datedTasks.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Add,
            title = "Нет задач с датами",
            message = "Добавьте дату начала или дедлайн к задачам для отображения на диаграмме Ганта",
            actionLabel = stringResource(R.string.add_task),
            onAction = { }
        )
        return
    }

    // Calculate date range from all tasks
    val allInstants = datedTasks.mapNotNull { it.startTime } + datedTasks.mapNotNull { it.deadline }
    val minInstant = allInstants.minByOrNull { it } ?: now
    val maxInstant = allInstants.maxByOrNull { it } ?: now
    val minDate = minInstant.atZone(zoneId).toLocalDate()
    val maxDate = maxInstant.atZone(zoneId).toLocalDate()
    val totalDays = maxOf(ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1, 7)
    val dayWidthDp = 80.dp
    val rowHeightDp = 52.dp
    val scrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        // Timeline header — horizontally scrollable in sync with task bars
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0 until totalDays) {
                    val date = minDate.plusDays(i.toLong())
                    val isToday = date == LocalDate.now(zoneId)
                    Column(
                        modifier = Modifier
                            .width(dayWidthDp)
                            .padding(Spacing.xs),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            date.dayOfWeek.name.take(3).lowercase()
                                .replaceFirstChar { c -> c.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant
                        )
                        Text(
                            date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) AppTheme.colors.primary else AppTheme.colors.onSurface
                        )
                    }
                }
            }
        }

        // Task bars
        items(datedTasks, key = { it.id ?: 0 }) { task ->
            GanttTaskBar(
                task = task,
                minDate = minDate,
                dayWidthDp = dayWidthDp,
                rowHeightDp = rowHeightDp,
                zoneId = zoneId,
                scrollState = scrollState,
                onTaskClick = onTaskClick
            )
        }

        // Undated tasks
        if (undatedTasks.isNotEmpty()) {
            item {
                Text(
                    "Без даты",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(top = Spacing.sm)
                )
            }
            items(undatedTasks, key = { "un_${it.id ?: 0}" }) { task ->
                TaskCard(task = task, onClick = { task.id?.let(onTaskClick) }, onCheckedChange = { })
            }
        }
    }
}

@Composable
private fun GanttTaskBar(
    task: Task,
    minDate: LocalDate,
    dayWidthDp: androidx.compose.ui.unit.Dp,
    rowHeightDp: androidx.compose.ui.unit.Dp,
    zoneId: ZoneId,
    scrollState: androidx.compose.foundation.ScrollState,
    onTaskClick: (Long) -> Unit
) {
    val taskStart = (task.startTime ?: task.deadline ?: Instant.now()).atZone(zoneId).toLocalDate()
    val taskEnd = (task.deadline ?: task.startTime ?: Instant.now()).atZone(zoneId).toLocalDate()

    val startOffsetDays = maxOf(ChronoUnit.DAYS.between(minDate, taskStart).toInt(), 0)
    val durationDays = maxOf(ChronoUnit.DAYS.between(taskStart, taskEnd).toInt() + 1, 1)
    val barWidthDp = (durationDays * dayWidthDp.value).dp
    val startOffsetDp = (startOffsetDays * dayWidthDp.value).dp

    val barColor = when (task.status) {
        TaskStatus.TODO -> AppTheme.colors.info
        TaskStatus.IN_PROGRESS -> AppTheme.colors.warning
        TaskStatus.DONE -> AppTheme.colors.success
    }
    val barAlpha = if (task.status == TaskStatus.DONE) 0.4f else 0.7f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeightDp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(startOffsetDp))
        Box(
            modifier = Modifier
                .width(barWidthDp)
                .height(rowHeightDp - Spacing.sm)
                .clip(RoundedCornerShape(Radius.sm))
                .background(barColor.copy(alpha = barAlpha))
                .padding(horizontal = Spacing.xs, vertical = 2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                task.title,
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProjectNotesList(
    notes: List<com.taskmanager.domain.model.Note>,
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit
) {
    if (notes.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Add,
            title = "В проекте пока нет заметок",
            message = "Заметки помогут хранить документацию и идеи по проекту",
            actionLabel = stringResource(R.string.add_note),
            onAction = onCreateNote
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items(notes, key = { it.id ?: 0 }) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { note.id?.let(onNoteClick) },
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
                    shape = RoundedCornerShape(Radius.lg),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
                        Text(
                            note.title.ifBlank { "Без названия" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        note.contentMarkdown.takeIf { it.isNotBlank() }?.let { content ->
                            val preview = content.lines().filter { it.isNotBlank() }.firstOrNull() ?: ""
                            if (preview.isNotBlank()) {
                                Text(
                                    preview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
