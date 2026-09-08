package com.taskmanager.presentation.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.screens.tasks.TaskDetailSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel(),
    onEditTask: (Long) -> Unit = {},
    onStartFocus: (Long) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var detailTaskId by remember { mutableStateOf<Long?>(null) }

    if (detailTaskId != null) {
        TaskDetailSheet(
            taskId = detailTaskId!!,
            onDismiss = { detailTaskId = null },
            onEdit = { 
                detailTaskId = null
                onEditTask(it)
            },
            onStartFocus = { 
                detailTaskId = null
                onStartFocus(it)
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Сегодня", style = MaterialTheme.typography.headlineSmall)
        }

        if (state.overdue.isNotEmpty()) {
            item { SectionHeader("Просрочено (" + state.overdue.size + ")") }
            items(state.overdue, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = true, onClick = { task.id?.let { detailTaskId = it } })
            }
        }

        if (state.dueToday.isNotEmpty()) {
            item { SectionHeader("На сегодня (" + state.dueToday.size + ")") }
            items(state.dueToday, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = false, onClick = { task.id?.let { detailTaskId = it } })
            }
        }

        if (state.noDeadline.isNotEmpty()) {
            item { SectionHeader("Бэклог") }
            items(state.noDeadline, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                TaskRow(task, accent = false, onClick = { task.id?.let { detailTaskId = it } })
            }
        }

        if (state.overdue.isEmpty() && state.dueToday.isEmpty() && state.noDeadline.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Filled.CheckCircle,
                    title = "На сегодня задач нет",
                    message = "Запланируйте задачи или отдохните",
                    modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskRow(task: Task, accent: Boolean, onClick: () -> Unit) {
    val deadlineText = task.deadline?.let {
        val localDate = java.time.Instant.ofEpochMilli(it.toEpochMilli())
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        com.taskmanager.presentation.util.RelativeDateFormatter.formatDate(localDate)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
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
            deadlineText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
