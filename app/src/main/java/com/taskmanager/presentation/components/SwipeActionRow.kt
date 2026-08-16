package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Wraps [content] with directional swipe gestures (issue 34: swipe was
 * complete-only). Swipe right → delete, swipe left → snooze.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeActionRow(
    onSnooze: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> { onDelete(); true }
                SwipeToDismissBoxValue.EndToStart -> { onSnooze(); true }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )
    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            val isDelete = state.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            val color = if (isDelete) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.secondaryContainer
            val icon = if (isDelete) Icons.Filled.Delete else Icons.Filled.Snooze
            val align = if (isDelete) Alignment.CenterStart else Alignment.CenterEnd
            Box(
                modifier = Modifier.fillMaxSize().background(color).padding(horizontal = 24.dp),
                contentAlignment = align
            ) {
                Icon(icon, contentDescription = if (isDelete) "Delete" else "Snooze")
            }
        },
        modifier = modifier
    ) {
        content()
    }
}

