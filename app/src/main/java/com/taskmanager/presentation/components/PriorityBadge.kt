package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val color = priorityColor(priority)
    val label = when (priority) {
        Priority.HIGH -> "Высокий"
        Priority.MEDIUM -> "Средний"
        Priority.LOW -> "Низкий"
        Priority.NONE -> "Без приоритета"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
