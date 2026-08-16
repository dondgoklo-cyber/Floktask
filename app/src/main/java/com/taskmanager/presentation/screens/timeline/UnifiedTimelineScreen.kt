package com.taskmanager.presentation.screens.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.TimelineEntry
import com.taskmanager.domain.model.TimelineSource
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DAY_FORMATTER = DateTimeFormatter.ofPattern("d MMMM")
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun UnifiedTimelineScreen(
    viewModel: UnifiedTimelineViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val zone = ZoneId.systemDefault()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::previousDay) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day")
            }
            Text(
                text = state.selectedDate.format(DAY_FORMATTER)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::goToToday) {
                Icon(Icons.Filled.Today, contentDescription = "Today")
            }
            IconButton(onClick = viewModel::nextDay) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next day")
            }
        }

        if (state.entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No events on this day",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.entries, key = { it.id }) { entry ->
                    TimelineEntryRow(entry = entry, zone = zone)
                }
            }
        }
    }
}

@Composable
private fun TimelineEntryRow(entry: TimelineEntry, zone: ZoneId) {
    val sourceLabel = when (entry.source) {
        TimelineSource.TASK_DEADLINE -> "Task"
        TimelineSource.TIME_BLOCK -> "Block"
        TimelineSource.CALENDAR_EVENT -> "Calendar"
    }
    val accent = entry.color?.let {
        runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull()
    } ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .padding(end = 12.dp)
                    .background(accent)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (entry.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (entry.isCompleted) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurfaceVariant
                )
                val time = entry.start.atZone(zone).toLocalTime().format(TIME_FORMATTER) +
                    (entry.end?.let { " – " + it.atZone(zone).toLocalTime().format(TIME_FORMATTER) } ?: "")
                Text(
                    "$sourceLabel · $time",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
