package com.taskmanager.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius

/**
 * Стилизованный OutlinedTextField с единым визуальным стилем:
 * - скругление Radius.md (12dp) — современное, не слишком круглое
 * - спокойные цвета границ (border / outlineVariant)
 * - focused state: primary цвет границы
 * - error state: danger цвет
 * - placeholder/label: onSurfaceVariant
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(Radius.md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppTheme.colors.onSurface,
            unfocusedTextColor = AppTheme.colors.onSurface,
            disabledTextColor = AppTheme.colors.onSurfaceVariant,
            errorTextColor = AppTheme.colors.danger,
            focusedContainerColor = AppTheme.colors.surface,
            unfocusedContainerColor = AppTheme.colors.surface,
            disabledContainerColor = AppTheme.colors.surfaceVariant,
            cursorColor = AppTheme.colors.primary,
            errorCursorColor = AppTheme.colors.danger,
            focusedBorderColor = AppTheme.colors.primary,
            unfocusedBorderColor = AppTheme.colors.border,
            disabledBorderColor = AppTheme.colors.outlineVariant,
            errorBorderColor = AppTheme.colors.danger,
            focusedLabelColor = AppTheme.colors.primary,
            unfocusedLabelColor = AppTheme.colors.onSurfaceVariant,
            disabledLabelColor = AppTheme.colors.onSurfaceVariant,
            errorLabelColor = AppTheme.colors.danger,
            focusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
            unfocusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
            disabledPlaceholderColor = AppTheme.colors.onSurfaceVariant,
            focusedLeadingIconColor = AppTheme.colors.primary,
            unfocusedLeadingIconColor = AppTheme.colors.onSurfaceVariant,
            focusedTrailingIconColor = AppTheme.colors.primary,
            unfocusedTrailingIconColor = AppTheme.colors.onSurfaceVariant
        )
    )
}
