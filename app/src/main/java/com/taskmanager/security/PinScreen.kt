package com.taskmanager.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.domain.usecase.settings.UserPreferences
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing

/**
 * Screen for PIN code entry. Modes:
 * - CREATE: create new PIN (enter twice)
 * - ENTER: enter PIN to unlock app
 * - CHANGE: change existing PIN (old -> new -> confirm)
 */
enum class PinMode { CREATE, ENTER, CHANGE }

@Composable
fun PinScreen(
    mode: PinMode,
    userName: String,
    userPrefs: UserPreferences,
    onSuccess: () -> Unit,
    onCancel: () -> Unit = {}
) {
    var input by remember { mutableStateOf("") }
    var firstPin by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }
    var stage by remember {
        mutableStateOf(if (mode == PinMode.CHANGE) "old" else "first")
    }
    var hint by remember {
        mutableStateOf(
            when (mode) {
                PinMode.CREATE -> "Enter PIN"
                PinMode.ENTER -> if (userName.isNotBlank()) "Welcome, $userName!" else "Enter PIN"
                PinMode.CHANGE -> "Enter old PIN"
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = hint,
            style = MaterialTheme.typography.titleMedium,
            color = if (error) AppTheme.colors.danger else AppTheme.colors.onSurface,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.padding(Spacing.lg))

        // PIN indicator dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(4) { index ->
                val filled = index < input.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) AppTheme.colors.primary
                            else AppTheme.colors.surfaceVariant
                        )
                )
            }
        }

        if (error) {
            Spacer(Modifier.padding(Spacing.sm))
            Text(
                "Wrong PIN",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.danger
            )
        }

        Spacer(Modifier.padding(Spacing.xl))

        // Numeric keypad
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "del")
        keys.chunked(3).forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowKeys.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(Modifier.size(72.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (key == "del") AppTheme.colors.surfaceVariant
                                    else AppTheme.colors.primaryContainer
                                )
                                .clickable {
                                    error = false
                                    when {
                                        key == "del" && input.isNotEmpty() -> {
                                            input = input.dropLast(1)
                                        }
                                        key != "del" && input.length < 4 -> {
                                            input += key
                                            if (input.length == 4) {
                                                handleComplete(
                                                    input = input,
                                                    mode = mode,
                                                    stage = stage,
                                                    firstPin = firstPin,
                                                    userPrefs = userPrefs,
                                                    onClear = { input = ""; error = false },
                                                    onSetFirst = { firstPin = input; input = ""; stage = "second"; hint = "Confirm PIN" },
                                                    onStageOld = { input = ""; stage = "new"; hint = "Enter new PIN" },
                                                    onSuccess = onSuccess,
                                                    onError = { input = ""; error = true }
                                                )
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "del") {
                                Icon(Icons.Filled.Backspace, contentDescription = "Delete", tint = AppTheme.colors.onSurface)
                            } else {
                                Text(
                                    key,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = AppTheme.colors.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.padding(Spacing.sm))
        }

        if (mode != PinMode.ENTER) {
            Text(
                "Cancel",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = Spacing.lg)
                    .clickable { onCancel() }
            )
        }
    }
}

private fun handleComplete(
    input: String,
    mode: PinMode,
    stage: String,
    firstPin: String?,
    userPrefs: UserPreferences,
    onClear: () -> Unit,
    onSetFirst: () -> Unit,
    onStageOld: () -> Unit,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    when {
        // Create: first entry
        (mode == PinMode.CREATE && stage == "first") || (mode == PinMode.CHANGE && stage == "new" && firstPin == null) -> {
            onSetFirst()
        }
        // Create: confirm first entry
        (mode == PinMode.CREATE && stage == "second") || (mode == PinMode.CHANGE && stage == "new" && firstPin != null) -> {
            if (input == firstPin) {
                userPrefs.setPin(input)
                onSuccess()
            } else {
                onError()
            }
        }
        // Change: check old PIN
        mode == PinMode.CHANGE && stage == "old" -> {
            if (userPrefs.checkPin(input)) {
                onStageOld()
            } else {
                onError()
            }
        }
        // Enter: check PIN
        mode == PinMode.ENTER -> {
            if (userPrefs.checkPin(input)) {
                onSuccess()
            } else {
                onError()
            }
        }
        else -> onClear()
    }
}
