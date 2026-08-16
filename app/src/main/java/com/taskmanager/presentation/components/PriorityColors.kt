package com.taskmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.theme.AppTheme

/** Утилита для получения цвета приоритета — единый источник истины для всех экранов. */
@Composable
fun priorityColor(priority: Priority): Color = when (priority) {
    Priority.HIGH -> AppTheme.colors.danger
    Priority.MEDIUM -> AppTheme.colors.warning
    Priority.LOW -> AppTheme.colors.success
    Priority.NONE -> AppTheme.colors.outline
}
