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

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val color = when (priority) {
        Priority.HIGH -> Color(0xFFEF5350)
        Priority.MEDIUM -> Color(0xFFFFA726)
        Priority.LOW -> Color(0xFF66BB6A)
        Priority.NONE -> Color(0xFFBDBDBD)
    }
    val label = when (priority) {
        Priority.HIGH -> "Высокий"
        Priority.MEDIUM -> "Средний"
        Priority.LOW -> "Низкий"
        Priority.NONE -> "Без приоритета"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
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
