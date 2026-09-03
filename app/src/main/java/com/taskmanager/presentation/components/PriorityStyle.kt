package com.taskmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.taskmanager.domain.model.Priority

/**
 * Visual styling for a task card by priority, implementing the hierarchy:
 * - P1 (HIGH)   : red border + elevated shadow
 * - P2 (MEDIUM) : orange border, subtle elevation
 * - P3 (LOW)    : grey border, flat
 * - NONE        : neutral, flat
 */
data class PriorityStyle(
    val borderColor: Color,
    val borderWidth: Dp,
    val elevation: Dp,
    val accentColor: Color
)

object PriorityStyles {

    @Composable
    fun forPriority(priority: Priority): PriorityStyle = when (priority) {
        Priority.HIGH -> PriorityStyle(
            borderColor = Color(0xFFEF5350),
            borderWidth = 2.dp,
            elevation = 6.dp,
            accentColor = Color(0xFFEF5350)
        )
        Priority.MEDIUM -> PriorityStyle(
            borderColor = Color(0xFFFFA726),
            borderWidth = 1.5.dp,
            elevation = 3.dp,
            accentColor = Color(0xFFFFA726)
        )
        Priority.LOW -> PriorityStyle(
            borderColor = MaterialTheme.colorScheme.outline,
            borderWidth = 1.dp,
            elevation = 0.dp,
            accentColor = Color(0xFFBDBDBD)
        )
        Priority.NONE -> PriorityStyle(
            borderColor = Color.Transparent,
            borderWidth = 0.dp,
            elevation = 0.dp,
            accentColor = MaterialTheme.colorScheme.outline
        )
    }

    val priorityLabel: (Priority) -> String = { priority ->
        when (priority) {
            Priority.HIGH -> "P1"
            Priority.MEDIUM -> "P2"
            Priority.LOW -> "P3"
            Priority.NONE -> "—"
        }
    }
}
