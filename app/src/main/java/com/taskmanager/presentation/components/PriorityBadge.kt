package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.taskmanager.domain.model.Priority

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val style = PriorityStyles.forPriority(priority)
    val priorityIcon = PriorityStyles.priorityIcon(priority)
    val priorityLabel = PriorityStyles.priorityLabel(priority)
    val contentDesc = LocalContext.current.getString(
        when (priority) {
            Priority.HIGH -> com.taskmanager.app.R.string.priority_high
            Priority.MEDIUM -> com.taskmanager.app.R.string.priority_medium
            Priority.LOW -> com.taskmanager.app.R.string.priority_low
            Priority.NONE -> com.taskmanager.app.R.string.priority_none
        }
    )

    Box(
        modifier = modifier
            .semantics { contentDescription = contentDesc }
            .clip(RoundedCornerShape(12.dp))
            .background(style.accentColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Icon(
                imageVector = priorityIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 2.dp)
            )
            Text(
                text = priorityLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}
