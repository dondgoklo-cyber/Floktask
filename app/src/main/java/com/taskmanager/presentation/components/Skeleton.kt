package com.taskmanager.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Skeleton-заполнитель с пульсирующей анимацией для loading-состояний.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(Radius.xs)
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(AppTheme.colors.surfaceVariant.copy(alpha = alpha))
    )
}

@Composable
fun TaskCardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xs)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.md)
        ) {
            SkeletonBox(
                modifier = Modifier.size(24.dp),
                height = 24.dp,
                shape = CircleShape
            )
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    height = 16.dp
                )
                Spacer(Modifier.height(Spacing.xs))
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    height = 12.dp
                )
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.xs)) {
            SkeletonBox(modifier = Modifier.width(60.dp), height = 20.dp)
            SkeletonBox(modifier = Modifier.width(80.dp), height = 20.dp)
        }
    }
}

@Composable
fun TaskListSkeleton(count: Int = 5) {
    Column {
        repeat(count) {
            TaskCardSkeleton()
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}
