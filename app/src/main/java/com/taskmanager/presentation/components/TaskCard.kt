package com.taskmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taskmanager.domain.model.Task
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { onCheckedChange(!task.isCompleted) }) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle
                        else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurface
                    )
                    task.description?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(task.priority)
                task.deadline?.let { deadline ->
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    val date = deadline.atZone(ZoneId.systemDefault()).toLocalDate()
                    val today = LocalDate.now()
                    val daysLeft = ChronoUnit.DAYS.between(today, date)
                    val deadlineText = when {
                        daysLeft < 0 -> "Просрочено"
                        daysLeft == 0L -> "Сегодня"
                        daysLeft == 1L -> "Завтра"
                        else -> date.format(formatter)
                    }
                    val deadlineColor = when {
                        daysLeft < 0 -> MaterialTheme.colorScheme.error
                        daysLeft <= 1 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = deadlineColor,
                            modifier = Modifier.height(16.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = deadlineText,
                            style = MaterialTheme.typography.labelSmall,
                            color = deadlineColor
                        )
                    }
                }
            }
        }
    }
}
