package com.taskmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
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
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        shape = RoundedCornerShape(Radius.md)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                IconButton(onClick = { onCheckedChange(!task.isCompleted) }) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle
                        else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) AppTheme.colors.success
                        else AppTheme.colors.outline
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (task.isCompleted) AppTheme.colors.outline
                        else AppTheme.colors.onSurface
                    )
                    task.description?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
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
                        daysLeft < 0 -> AppTheme.colors.danger
                        daysLeft <= 1 -> AppTheme.colors.warning
                        else -> AppTheme.colors.onSurfaceVariant
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = deadlineColor,
                            modifier = Modifier.height(16.dp)
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = deadlineText,
                            style = MaterialTheme.typography.labelSmall,
                            color = deadlineColor
                        )
                    }
                }
                task.startTime?.let { start ->
                    val time = start.atZone(ZoneId.systemDefault()).toLocalTime()
                    Text(
                        time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.primary
                    )
                }
            }
        }
    }
}
