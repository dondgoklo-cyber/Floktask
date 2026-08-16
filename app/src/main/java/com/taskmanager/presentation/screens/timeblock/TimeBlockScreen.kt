package com.taskmanager.presentation.screens.timeblock

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.TimeBlock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val HOUR_HEIGHT = 64.dp
private val DAY_FORMATTER = DateTimeFormatter.ofPattern("d MMMM")
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TimeBlockScreen(
    viewModel: TimeBlockViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val blocks by viewModel.blocks.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Quick-create a 1h block at the next full hour of the selected day.
                val zone = ZoneId.systemDefault()
                val now = java.time.LocalDateTime.now()
                val startHour = (now.hour + 1).coerceAtMost(22)
                val start = selectedDate.atTime(startHour, 0).atZone(zone).toInstant()
                val end = start.plusSeconds(3600)
                viewModel.createBlock("New block", start, end)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add block")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DateHeader(
                selectedDate = selectedDate,
                onPrevious = viewModel::previousDay,
                onNext = viewModel::nextDay,
                onToday = viewModel::goToToday
            )

            ViewModeToggle(
                selected = viewMode,
                onSelect = viewModel::setViewMode
            )

            when (viewMode) {
                TimeBlockViewMode.DAY -> DayTimeline(
                    date = selectedDate,
                    blocks = blocks,
                    onToggleCompleted = viewModel::toggleCompleted,
                    onDelete = viewModel::deleteBlock
                )
                TimeBlockViewMode.WEEK -> WeekGrid(
                    weekStart = selectedDate.with(DayOfWeek.MONDAY),
                    blocks = blocks,
                    onToggleCompleted = viewModel::toggleCompleted,
                    onDelete = viewModel::deleteBlock
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    selectedDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day")
        }
        Text(
            text = selectedDate.format(DAY_FORMATTER)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToday) {
            Icon(Icons.Filled.Today, contentDescription = "Today")
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next day")
        }
    }
}

@Composable
private fun ViewModeToggle(
    selected: TimeBlockViewMode,
    onSelect: (TimeBlockViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == TimeBlockViewMode.DAY,
            onClick = { onSelect(TimeBlockViewMode.DAY) },
            label = { Text("Day") }
        )
        FilterChip(
            selected = selected == TimeBlockViewMode.WEEK,
            onClick = { onSelect(TimeBlockViewMode.WEEK) },
            label = { Text("Week") }
        )
    }
}

@Composable
private fun DayTimeline(
    date: LocalDate,
    blocks: List<TimeBlock>,
    onToggleCompleted: (TimeBlock) -> Unit,
    onDelete: (Long) -> Unit
) {
    val zone = ZoneId.systemDefault()
    val dayBlocks = blocks.filter { it.date(zone) == date }
        .sortedBy { it.startTime }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items((0..23).toList(), key = { it }) { hour ->
            HourRow(
                hour = hour,
                block = dayBlocks.firstOrNull {
                    it.startTime.atZone(zone).hour == hour
                },
                onToggleCompleted = onToggleCompleted,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun HourRow(
    hour: Int,
    block: TimeBlock?,
    onToggleCompleted: (TimeBlock) -> Unit,
    onDelete: (Long) -> Unit
) {
    val zone = ZoneId.systemDefault()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HOUR_HEIGHT)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = String.format("%02d:00", hour),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .width(48.dp)
                .padding(top = 2.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            if (block != null) {
                BlockCard(
                    block = block,
                    onClick = { onToggleCompleted(block) },
                    onLongClick = { onDelete(block.id ?: -1L) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .height(1.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BlockCard(
    block: TimeBlock,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zone = ZoneId.systemDefault()
    val blockColor = block.color?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
        ?: MaterialTheme.colorScheme.primaryContainer

    Card(
        modifier = modifier
            .padding(vertical = 2.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = blockColor)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = block.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (block.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                color = if (block.isCompleted) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${block.startTime.atZone(zone).toLocalTime().format(TIME_FORMATTER)}" +
                    " – ${block.endTime.atZone(zone).toLocalTime().format(TIME_FORMATTER)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WeekGrid(
    weekStart: LocalDate,
    blocks: List<TimeBlock>,
    onToggleCompleted: (TimeBlock) -> Unit,
    onDelete: (Long) -> Unit
) {
    val zone = ZoneId.systemDefault()
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(days, key = { it.toEpochDay() }) { day ->
            val dayBlocks = blocks.filter { it.date(zone) == day }
                .sortedBy { it.startTime }
            WeekDayHeader(day)
            dayBlocks.forEach { block ->
                BlockCard(
                    block = block,
                    onClick = { onToggleCompleted(block) },
                    onLongClick = { onDelete(block.id ?: -1L) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun WeekDayHeader(day: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = day.format(DAY_FORMATTER),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
