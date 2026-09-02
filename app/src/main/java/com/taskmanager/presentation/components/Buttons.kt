package com.taskmanager.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.util.HapticManager
import com.taskmanager.presentation.theme.Spacing

/**
 * Primary button: заметный, умеренное скругление, комфортная высота, чёткая типографика.
 * Без чрезмерных теней (Elevation.none).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "primaryButtonScale"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        modifier = tapModifier,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(Radius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.onPrimary,
            disabledContainerColor = AppTheme.colors.surfaceVariant,
            disabledContentColor = AppTheme.colors.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            pressedElevation = Elevation.none,
            focusedElevation = Elevation.none,
            hoveredElevation = Elevation.none,
            defaultElevation = Elevation.none
        ),
        contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md)
    ) {
        leadingIcon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(20.dp)) }
        if (leadingIcon != null) {
            androidx.compose.foundation.layout.Spacer(Modifier.size(Spacing.xs))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Secondary button: визуально слабее primary, аккуратный border.
 * Одинаковая высота с primary.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(Radius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.surfaceVariant,
            contentColor = AppTheme.colors.onSurface,
            disabledContainerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.5f),
            disabledContentColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            pressedElevation = Elevation.none,
            defaultElevation = Elevation.none
        ),
        contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Minimalist text button: без лишнего визуального веса.
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(Radius.sm),
        colors = ButtonDefaults.textButtonColors(
            contentColor = AppTheme.colors.primary,
            disabledContentColor = AppTheme.colors.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Icon button с достаточной touch target area (48dp) и понятным pressed state.
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Int = 24
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(Spacing.touchTarget),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = AppTheme.colors.onSurfaceVariant
        )
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

/**
 * FAB with long-press context menu support.
 * Primary color, with shadow.
 */
@Composable
fun AppFloatingActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hapticManager: HapticManager? = null,
    onLongClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "fabScale"
    )
    
    val tapModifier = if (onLongClick != null) {
        modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick()
                        hapticManager?.lightVibrate()
                    },
                    onLongPress = {
                        onLongClick()
                        hapticManager?.mediumVibrate()
                    }
                )
            }
            .graphicsLayer { scaleX = scale; scaleY = scale }
    } else {
        modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
    }
    
    FloatingActionButton(
        onClick = {
            onClick()
            hapticManager?.lightVibrate()
        },
        modifier = tapModifier,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(Radius.lg),
        containerColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Elevation.sm,
            pressedElevation = Elevation.sm
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(24.dp))
    }
}
