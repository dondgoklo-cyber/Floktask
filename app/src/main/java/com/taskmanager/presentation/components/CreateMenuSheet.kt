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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

private data class CreateMenuItem(
    val icon: ImageVector,
    val labelRes: Int,
    val color: Color,
    val onClick: () -> Unit
)

/**
 * Универсальное меню создания: Задача / Привычка / Доход / Расход / Проект.
 * Компактный ModalBottomSheet с цветными иконками.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuSheet(
    onDismiss: () -> Unit,
    onTask: () -> Unit,
    onHabit: () -> Unit,
    onIncome: () -> Unit,
    onExpense: () -> Unit,
    onProject: () -> Unit,
    onNote: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val items = listOf(
        CreateMenuItem(Icons.Filled.AddTask, R.string.add_task, AppTheme.colors.primary, onTask),
        CreateMenuItem(Icons.Filled.Spa, R.string.habits, AppTheme.colors.secondary, onHabit),
        CreateMenuItem(Icons.Filled.ArrowUpward, R.string.add_income, AppTheme.colors.success, onIncome),
        CreateMenuItem(Icons.Filled.ArrowDownward, R.string.add_expense, AppTheme.colors.danger, onExpense),
        CreateMenuItem(Icons.Filled.Description, R.string.add_note, AppTheme.colors.info, onNote),
        CreateMenuItem(Icons.Filled.Folder, R.string.new_project, AppTheme.colors.info, onProject)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                stringResource(R.string.create),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
            items.forEach { item ->
                CreateMenuRow(
                    icon = item.icon,
                    label = stringResource(item.labelRes),
                    color = item.color,
                    onClick = {
                        onDismiss()
                        item.onClick()
                    }
                )
            }
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

@Composable
private fun CreateMenuRow(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.colors.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
