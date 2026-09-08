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

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val color = priorityColor(priority)
    val label = when (priority) {
        Priority.HIGH -> "P1"
        Priority.MEDIUM -> "P2"
        Priority.LOW -> "P3"
        Priority.NONE -> ""
    }
    
    if (label.isEmpty()) return
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
