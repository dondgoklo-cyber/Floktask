package com.taskmanager.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    onCheckedChange: (Boolean) -> Unit,
    tagColors: Map<String, String> = emptyMap()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Checkbox: явный completed state
                IconButton(
                    onClick = { onCheckedChange(!task.isCompleted) },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (task.isCompleted) AppTheme.colors.success
                        else AppTheme.colors.outline
                    )
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle
                        else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (task.isCompleted) AppTheme.colors.success
                        else AppTheme.colors.outline
                    )
                }

                // Title + description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) AppTheme.colors.onSurfaceVariant
                        else AppTheme.colors.onSurface
                    )
                    task.description?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isCompleted) AppTheme.colors.outlineVariant
                            else AppTheme.colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Priority accent: маленькая вертикальная полоска слева от контента
                if (task.priority != com.taskmanager.domain.model.Priority.NONE) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(Radius.full))
                            .background(priorityColor(task.priority))
                    )
                }
            }

            // Meta row: deadline, time, tags — только если есть что показать
            val hasMeta = task.deadline != null || task.startTime != null || task.tags.isNotEmpty()
            if (hasMeta) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Deadline
                    task.deadline?.let { deadline ->
                        val date = deadline.atZone(ZoneId.of("UTC")).toLocalDate()
                        val today = LocalDate.now()
                        val daysLeft = ChronoUnit.DAYS.between(today, date)
                        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        val deadlineText = when {
                            daysLeft < 0 -> "Просрочено"
                            daysLeft == 0L -> "Сегодня"
                            daysLeft == 1L -> "Завтра"
                            else -> date.format(formatter)
                        }
                        val deadlineColor = when {
                            daysLeft < 0 -> AppTheme.colors.danger
                            daysLeft <= 1 -> AppTheme.colors.warning
                            task.isCompleted -> AppTheme.colors.outlineVariant
                            else -> AppTheme.colors.onSurfaceVariant
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = deadlineColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(Spacing.xs))
                            Text(
                                text = deadlineText,
                                style = MaterialTheme.typography.labelSmall,
                                color = deadlineColor
                            )
                        }
                    }

                    // Start time
                    task.startTime?.let { start ->
                        val time = start.atZone(ZoneId.of("UTC")).toLocalTime()
                        Text(
                            time.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (task.isCompleted) AppTheme.colors.outlineVariant
                            else AppTheme.colors.primary
                        )
                    }

                    // Tags
                    if (task.tags.isNotEmpty()) {
                        task.tags.take(3).forEach { tag ->
                            TagMiniChip(
                                text = tag,
                                isCompleted = task.isCompleted,
                                color = parseTagColor(tagColors[tag])
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagMiniChip(
    text: String,
    isCompleted: Boolean,
    color: Color = DEFAULT_TAG_COLOR
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(
                if (isCompleted) AppTheme.colors.surfaceVariant
                else color.copy(alpha = 0.18f)
            )
            .padding(horizontal = Spacing.sm, vertical = 2.dp)
    ) {
        Text(
            text = "#$text",
            style = MaterialTheme.typography.labelSmall,
            color = if (isCompleted) AppTheme.colors.outlineVariant
            else color
        )
    }
}
