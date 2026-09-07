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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.PomodoroType

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Фокус",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.type == PomodoroType.WORK,
                onClick = { viewModel.selectType(PomodoroType.WORK) },
                label = { Text("Работа") }
            )
            FilterChip(
                selected = state.type == PomodoroType.SHORT_BREAK,
                onClick = { viewModel.selectType(PomodoroType.SHORT_BREAK) },
                label = { Text("Перерыв") }
            )
            FilterChip(
                selected = state.type == PomodoroType.LONG_BREAK,
                onClick = { viewModel.selectType(PomodoroType.LONG_BREAK) },
                label = { Text("Длинный") }
            )
        }

        Spacer(Modifier.height(40.dp))

        val progress = if (state.totalSeconds > 0) {
            state.remainingSeconds.toFloat() / state.totalSeconds.toFloat()
        } else 0f

        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val mins = state.remainingSeconds / 60
                val secs = state.remainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                )
                if (state.taskTitle != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.taskTitle!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.reset() }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Сброс")
                Spacer(Modifier.size(8.dp))
                Text("Сброс")
            }
            Button(onClick = {
                if (state.isRunning) viewModel.pause() else viewModel.start()
            }) {
                Icon(
                    if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.size(8.dp))
                Text(if (state.isRunning) "Пауза" else "Старт")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (state.completedPomodoros > 0) {
            Text(
                text = "Pomodoro сегодня: " + state.completedPomodoros,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}