package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Единый premium empty state с gradient-контейнером для иконки.
 * Структура: иконка в gradient-контейнере → заголовок → описание → action-кнопка.
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
            // Премиальный gradient-контейнер для иконки
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(Radius.xl))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AppTheme.colors.primary.copy(alpha = 0.12f),
                                AppTheme.colors.primaryContainer.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(Spacing.xs))

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

            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(Spacing.xs))
                PrimaryButton(
                    text = actionText,
                    onClick = onAction
                )
            }
        }
    }
}
