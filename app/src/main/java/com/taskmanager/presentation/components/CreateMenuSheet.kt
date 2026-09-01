package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Bottom sheet menu for creating different types of content.
 * Accessed via long-press on FAB or context menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuSheet(
    onDismiss: () -> Unit,
    onCreateTask: () -> Unit,
    onCreateNote: () -> Unit,
    onCreateVoiceTask: () -> Unit,
    onCreateEvent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.lg))
                .background(AppTheme.colors.surface)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = "Создать",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            // Task
            CreateMenuItem(
                icon = Icons.Filled.TaskAlt,
                label = "Задача",
                onClick = {
                    onCreateTask()
                    onDismiss()
                }
            )

            // Voice Task
            CreateMenuItem(
                icon = Icons.Filled.Mic,
                label = "Голосовая задача",
                onClick = {
                    onCreateVoiceTask()
                    onDismiss()
                }
            )

            // Note
            CreateMenuItem(
                icon = Icons.Filled.Note,
                label = "Заметка",
                onClick = {
                    onCreateNote()
                    onDismiss()
                }
            )

            // Calendar Event
            CreateMenuItem(
                icon = Icons.Filled.CalendarMonth,
                label = "Событие",
                onClick = {
                    onCreateEvent()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun CreateMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurface
        )
    }
}
