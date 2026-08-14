package com.taskmanager.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

private val HOUR_HEIGHT = 64.dp
private const val HOURS_START = 6
private const val HOURS_END = 24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.selectedDate.format(
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale("ru"))
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goToPreviousDay() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToNextDay() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Переключение режимов
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                CalendarViewMode.entries.forEach { mode ->
                    val label = when (mode) {
                        CalendarViewMode.DAY -> "День"
                        CalendarViewMode.THREE_DAYS -> "3 дня"
                        CalendarViewMode.WEEK -> "Неделя"
                        CalendarViewMode.MONTH -> "Месяц"
                        CalendarViewMode.AGENDA -> "Список"
                    }
                    FilterChip(
                        selected = state.viewMode == mode,
                        onClick = { viewModel.setViewMode(mode) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                when (state.viewMode) {
                    CalendarViewMode.DAY -> DayView(state, viewModel)
                    CalendarViewMode.WEEK -> WeekView(state, viewModel)
                    CalendarViewMode.THREE_DAYS -> WeekView(state, viewModel, days = 3)
                    CalendarViewMode.MONTH -> MonthView(state, viewModel)
                    CalendarViewMode.AGENDA -> AgendaView(state, viewModel)
                }
            }
        }
    }
}

@Composable
private fun DayView(state: CalendarUiState, viewModel: CalendarViewModel) {
    val dayTasks = state.timedTasks[state.selectedDate] ?: emptyList()
    val timedTasks = dayTasks.filter { it.startTime != null }
    val untimedTasks = dayTasks.filter { it.startTime == null }

    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Задачи без времени
        if (untimedTasks.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.height((untimedTasks.size * 56).coerceAtMost(200).dp)
            ) {
                items(untimedTasks, key = { it.id ?: 0 }) { task ->
                    UntimedTaskChip(task)
                }
            }
        }

        // Временная шкала с time blocks
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = scrollState) {
                items((HOURS_START until HOURS_END).toList()) { hour ->
                    HourRow(hour = hour, tasks = timedTasks, state = state, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun HourRow(
    hour: Int,
    tasks: List<Task>,
    state: CalendarUiState,
    viewModel: CalendarViewModel
) {
    val density = LocalDensity.current
    val zone = ZoneId.systemDefault()
    val hourTasks = tasks.filter { task ->
        task.startTime?.atZone(zone)?.hour == hour
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HOUR_HEIGHT),
        verticalAlignment = Alignment.Top
    ) {
        // Метка часа
        Text(
            text = String.format("%02d:00", hour),
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.width(48.dp).padding(top = Spacing.xs)
        )
        // Область задач
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            // Линия
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(end = Spacing.md)
                    .background(AppTheme.colors.divider)
            )
            hourTasks.forEach { task ->
                DraggableTimeBlock(
                    task = task,
                    hour = hour,
                    onMove = { deltaMinutes ->
                        val newStart = task.startTime!!.plusSeconds(deltaMinutes * 60L)
                        viewModel.updateTaskSchedule(task, newStart, task.durationMinutes)
                    },
                    onResize = { newDuration ->
                        viewModel.updateTaskSchedule(task, task.startTime!!, newDuration)
                    }
                )
            }
        }
    }
}

@Composable
private fun DraggableTimeBlock(
    task: Task,
    hour: Int,
    onMove: (deltaMinutes: Int) -> Unit,
    onResize: (Long) -> Unit
) {
    val density = LocalDensity.current
    val zone = ZoneId.systemDefault()
    var dragY by remember { mutableFloatStateOf(0f) }

    val startMinute = task.startTime?.atZone(zone)?.minute ?: 0
    val duration = task.durationMinutes ?: 30
    val heightDp = (duration.toFloat() / 60f) * HOUR_HEIGHT.value
    val offsetDp = (startMinute.toFloat() / 60f) * HOUR_HEIGHT.value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = Spacing.md, top = offsetDp.dp)
            .height(heightDp.dp)
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {
                        val deltaMinutes = with(density) { (dragY / HOUR_HEIGHT.toPx() * 60).roundToInt() }
                        if (deltaMinutes != 0) {
                            onMove(deltaMinutes)
                        }
                        dragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragY += dragAmount.y
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(Radius.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.primaryContainer)
                .padding(horizontal = Spacing.sm, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = heightDp.dp)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(priorityColor(task.priority))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                val endTime = task.startTime!!.plusSeconds(duration * 60)
                val fmt = DateTimeFormatter.ofPattern("HH:mm")
                Text(
                    "${task.startTime.atZone(zone).toLocalTime().format(fmt)} - ${endTime.atZone(zone).toLocalTime().format(fmt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UntimedTaskChip(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(Radius.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(priorityColor(task.priority))
            )
            Text(task.title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun WeekView(state: CalendarUiState, viewModel: CalendarViewModel, days: Int = 7) {
    val startOfWeek = state.selectedDate.with(DayOfWeek.MONDAY)
    val weekDays = (0 until days).map { startOfWeek.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        items(weekDays) { date ->
            val dayTasks = state.timedTasks[date] ?: emptyList()
            DaySection(
                date = date,
                tasks = dayTasks,
                isSelected = date == state.selectedDate,
                onSelectDate = { viewModel.selectDate(date) }
            )
        }
    }
}

@Composable
private fun DaySection(
    date: LocalDate,
    tasks: List<Task>,
    isSelected: Boolean,
    onSelectDate: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, d MMM", Locale("ru")) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onSelectDate,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        ),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) AppTheme.colors.primaryContainer else AppTheme.colors.surface
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                date.format(dateFormatter),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) AppTheme.colors.primary else AppTheme.colors.onSurface
            )
            if (tasks.isEmpty()) {
                Text(
                    "Нет задач",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            } else {
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        task.startTime?.let { start ->
                            Text(
                                start.atZone(ZoneId.systemDefault())
                                    .toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.primary
                            )
                        } ?: Box(
                            modifier = Modifier.size(6.dp).clip(CircleShape)
                                .background(priorityColor(task.priority))
                        )
                        Text(
                            task.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (task.isCompleted) AppTheme.colors.onSurfaceVariant
                            else AppTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthView(state: CalendarUiState, viewModel: CalendarViewModel) {
    val startOfMonth = state.selectedDate.withDayOfMonth(1)
    val startOfWeek = startOfMonth.with(DayOfWeek.MONDAY)
    val daysInMonth = startOfMonth.lengthOfMonth()
    val days = (0 until 42).map { startOfWeek.plusDays(it.toLong()) }

    val cols = 7
    val rows = days.chunked(cols)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..7).forEach { dayNum ->
                    Text(
                        DayOfWeek.of(dayNum).getDisplayName(TextStyle.NARROW, Locale("ru")),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        items(rows) { week ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                week.forEach { date ->
                    val inMonth = date.month == startOfMonth.month
                    val dayTasks = state.timedTasks[date] ?: emptyList()
                    val isToday = date == LocalDate.now()
                    val isSelected = date == state.selectedDate

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(
                                when {
                                    isSelected -> AppTheme.colors.primary
                                    isToday -> AppTheme.colors.primaryContainer
                                    else -> AppTheme.colors.surface
                                }
                            )
                            .pointerInput(date) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { viewModel.selectDate(date) }
                                )
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                isSelected -> AppTheme.colors.onPrimary
                                !inMonth -> AppTheme.colors.outline
                                else -> AppTheme.colors.onSurface
                            },
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (dayTasks.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.primary
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaView(state: CalendarUiState, viewModel: CalendarViewModel) {
    val sortedDays = state.timedTasks.keys.sorted()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        if (sortedDays.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.xxxl),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_scheduled_tasks),
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }
        } else {
            items(sortedDays) { date ->
                val tasks = state.timedTasks[date] ?: emptyList()
                DaySection(
                    date = date,
                    tasks = tasks,
                    isSelected = date == state.selectedDate,
                    onSelectDate = { viewModel.selectDate(date) }
                )
            }
        }
    }
}
