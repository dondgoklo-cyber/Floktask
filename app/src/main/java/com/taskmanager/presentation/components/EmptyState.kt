package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Единое переиспользуемое пустое состояние.
 * Структура: иконка в контейнере → заголовок → описание → опциональная action-кнопка.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier.padding(horizontal = Spacing.xxl)
        ) {
            // Иконка в лёгком скруглённом контейнере
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(AppTheme.colors.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppTheme.colors.outline,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )

            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Опциональная action-кнопка (использует существующую систему кнопок)
            if (actionText != null && onAction != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.size(Spacing.xs))
                PrimaryButton(
                    text = actionText,
                    onClick = onAction
                )
            }
        }
    }
}
