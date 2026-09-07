package com.taskmanager.presentation.screens.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.haptic.HapticType
import com.taskmanager.haptic.rememberHaptic
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = rememberHaptic()

    LaunchedEffect(state.isRunning, state.remainingSeconds) {
        if (state.remainingSeconds == 0 && !state.isRunning) {
            haptic(HapticType.SUCCESS)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Фокус",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSurface
        )

        Spacer(Modifier.height(Spacing.xl))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            FilterChip(
                selected = state.type == PomodoroType.WORK,
                onClick = { viewModel.selectType(PomodoroType.WORK); haptic(HapticType.LIGHT) },
                label = { Text("Работа") }
            )
            FilterChip(
                selected = state.type == PomodoroType.SHORT_BREAK,
                onClick = { viewModel.selectType(PomodoroType.SHORT_BREAK); haptic(HapticType.LIGHT) },
                label = { Text("Перерыв") }
            )
            FilterChip(
                selected = state.type == PomodoroType.LONG_BREAK,
                onClick = { viewModel.selectType(PomodoroType.LONG_BREAK); haptic(HapticType.LIGHT) },
                label = { Text("Длинный") }
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        val progress = if (state.totalSeconds > 0) {
            state.remainingSeconds.toFloat() / state.totalSeconds.toFloat()
        } else 0f

        val timerColor = when (state.type) {
            PomodoroType.WORK -> AppTheme.colors.primary
            PomodoroType.SHORT_BREAK -> AppTheme.colors.success
            PomodoroType.LONG_BREAK -> AppTheme.colors.info
        }

        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = timerColor,
                trackColor = AppTheme.colors.surfaceVariant,
                strokeWidth = 8.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val mins = state.remainingSeconds / 60
                val secs = state.remainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onSurface
                )
                if (state.taskTitle != null) {
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = state.taskTitle!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            OutlinedButton(
                onClick = { viewModel.reset(); haptic(HapticType.LIGHT) },
                shape = RoundedCornerShape(Radius.lg)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Сброс")
                Spacer(Modifier.size(Spacing.sm))
                Text("Сброс")
            }
            Button(
                onClick = {
                    if (state.isRunning) {
                        viewModel.pause()
                        haptic(HapticType.LIGHT)
                    } else {
                        viewModel.start()
                        haptic(HapticType.SELECTION)
                    }
                },
                shape = RoundedCornerShape(Radius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = timerColor,
                    contentColor = AppTheme.colors.onPrimary
                )
            ) {
                Icon(
                    if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.size(Spacing.sm))
                Text(if (state.isRunning) "Пауза" else "Старт")
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        if (state.completedPomodoros > 0) {
            Text(
                text = "Помодоро сегодня: " + state.completedPomodoros,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
