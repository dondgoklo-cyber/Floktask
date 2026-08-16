package com.taskmanager.presentation.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.Task

@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Today", style = MaterialTheme.typography.headlineSmall)
        }

        if (state.overdue.isNotEmpty()) {
            item { SectionHeader("Overdue (${state.overdue.size})") }
            items(state.overdue, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = true)
            }
        }

        if (state.dueToday.isNotEmpty()) {
            item { SectionHeader("Due today (${state.dueToday.size})") }
            items(state.dueToday, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = false)
            }
        }

        if (state.noDeadline.isNotEmpty()) {
            item { SectionHeader("Backlog") }
            items(state.noDeadline, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = false)
            }
        }

        if (state.overdue.isEmpty() && state.dueToday.isEmpty() && state.noDeadline.isEmpty()) {
            item {
                Text(
                    "Nothing for today. Plan ahead or take a break.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun TaskRow(task: Task, accent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (accent) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            task.deadline?.let {
                Text(
                    java.time.Instant.ofEpochMilli(it.toEpochMilli())
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
