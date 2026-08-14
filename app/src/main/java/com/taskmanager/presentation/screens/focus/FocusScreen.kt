package com.taskmanager.presentation.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.focus)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Переключатель типа
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                PomodoroType.entries.forEach { type ->
                    val label = when (type) {
                        PomodoroType.WORK -> stringResource(R.string.work_session)
                        PomodoroType.SHORT_BREAK -> stringResource(R.string.short_break)
                        PomodoroType.LONG_BREAK -> stringResource(R.string.long_break)
                    }
                    FilterChip(
                        selected = state.type == type,
                        onClick = { viewModel.selectType(type) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // Круглый таймер
            Box(
                modifier = Modifier.padding(top = Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicatorTimer(
                    progress = if (state.totalSeconds > 0) {
                        state.remainingSeconds.toFloat() / state.totalSeconds.toFloat()
                    } else 0f,
                    color = when (state.type) {
                        PomodoroType.WORK -> AppTheme.colors.primary
                        PomodoroType.SHORT_BREAK -> AppTheme.colors.success
                        PomodoroType.LONG_BREAK -> AppTheme.colors.info
                    },
                    trackColor = AppTheme.colors.surfaceVariant
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val mins = state.remainingSeconds / 60
                    val secs = state.remainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.onSurface
                    )
                    Text(
                        text = "${state.completedPomodoros} 🍅 сегодня",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }

            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterHorizontally)
            ) {
                FilledIconButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.reset))
                }
                FilledIconButton(
                    onClick = {
                        if (state.isRunning) viewModel.pause() else viewModel.start()
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isRunning) stringResource(R.string.pause)
                        else stringResource(R.string.start_pomodoro),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Статистика
            state.stats?.let { stats ->
                StatsCard(stats)
            }
        }
    }
}

@Composable
private fun CircularProgressIndicatorTimer(
    progress: Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val stroke = 12.dp.toPx()
            // Фон
            drawCircle(
                color = trackColor,
                radius = (size.minDimension - stroke) / 2,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Прогресс
            val sweep = 360f * progress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun StatsCard(stats: PomodoroStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
            Text(
                stringResource(R.string.pomodoros_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn("Сегодня", "${stats.todayCount} 🍅", "${stats.todayMinutes} мин")
                StatColumn("Неделя", "${stats.weekCount} 🍅", "${stats.weekMinutes} мин")
                StatColumn("Месяц", "${stats.monthCount} 🍅", "${stats.monthMinutes} мин")
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, count: String, minutes: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.onSurfaceVariant)
        Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(minutes, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
    }
}

