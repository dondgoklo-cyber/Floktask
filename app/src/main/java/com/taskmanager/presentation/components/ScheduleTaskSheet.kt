package com.taskmanager.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing
import java.time.LocalDate

@Composable
fun ScheduleTaskSheet(
    onDismiss: () -> Unit,
    onSchedule: (LocalDate?) -> Unit,
    currentDeadline: LocalDate? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.schedule_task)) },
        text = {
            Column {
                ScheduleOption(
                    label = stringResource(R.string.today),
                    icon = Icons.Filled.Today,
                    onClick = {
                        onSchedule(LocalDate.now())
                        onDismiss()
                    }
                )
                ScheduleOption(
                    label = stringResource(R.string.tomorrow),
                    icon = Icons.Filled.DateRange,
                    onClick = {
                        onSchedule(LocalDate.now().plusDays(1))
                        onDismiss()
                    }
                )
                ScheduleOption(
                    label = stringResource(R.string.this_weekend),
                    icon = Icons.Filled.Event,
                    onClick = {
                        val today = LocalDate.now()
                        val daysUntilSaturday = (6 - today.dayOfWeek.value) % 7
                        val saturday = today.plusDays(daysUntilSaturday.toLong())
                        onSchedule(saturday)
                        onDismiss()
                    }
                )
                ScheduleOption(
                    label = stringResource(R.string.next_week),
                    icon = Icons.Filled.CalendarMonth,
                    onClick = {
                        val today = LocalDate.now()
                        val daysUntilMonday = (8 - today.dayOfWeek.value) % 7
                        val monday = if (daysUntilMonday == 0) today.plusDays(7) else today.plusDays(daysUntilMonday.toLong())
                        onSchedule(monday)
                        onDismiss()
                    }
                )
                ScheduleOption(
                    label = stringResource(R.string.choose_date),
                    icon = Icons.Filled.EditCalendar,
                    onClick = { showDatePicker = true }
                )
                if (currentDeadline != null) {
                    ScheduleOption(
                        label = stringResource(R.string.clear_date),
                        icon = Icons.Filled.Clear,
                        onClick = {
                            onSchedule(null)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showDatePicker) {
        // DatePickerDialog would be implemented here using existing patterns from TaskEditScreen
        // For now, just dismiss and use a placeholder
        showDatePicker = false
        onDismiss()
    }
}

@Composable
private fun ScheduleOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.md, horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppTheme.colors.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = AppTheme.colors.onSurface
        )
    }
}
