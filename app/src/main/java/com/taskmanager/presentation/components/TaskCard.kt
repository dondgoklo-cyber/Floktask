package com.taskmanager.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskmanager.domain.model.Task

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                Spacer(Modifier.width(8.dp))
                Text(task.title, style = MaterialTheme.typography.titleMedium)
            }
            task.description?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            PriorityBadge(task.priority)
        }
    }
}
