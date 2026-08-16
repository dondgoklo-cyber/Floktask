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
    val style = PriorityStyles.forPriority(priority)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(style.accentColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = PriorityStyles.priorityLabel(priority),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
