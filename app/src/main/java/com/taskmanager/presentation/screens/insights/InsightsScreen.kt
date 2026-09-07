package com.taskmanager.presentation.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.statistics),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Overview cards
                item {
                    Text(
                        "Обзор задач",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Spacing.sm)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Task,
                            iconColor = AppTheme.colors.primary,
                            value = state.totalTasks.toString(),
                            label = "Всего задач"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.CheckCircle,
                            iconColor = AppTheme.colors.success,
                            value = state.completedTasks.toString(),
                            label = "Выполнено"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Warning,
                            iconColor = AppTheme.colors.warning,
                            value = state.overdueTasks.toString(),
                            label = "Просрочено"
                        )
                    }
                }

                // Completion rate
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(Radius.lg)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
                            Text(
                                "Процент выполнения",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                LinearProgressIndicator(
                                    progress = { state.completionRate },
                                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(Radius.full)),
                                    color = AppTheme.colors.primary,
                                    trackColor = AppTheme.colors.surfaceVariant
                                )
                                Text(
                                    (state.completionRate * 100).toInt().toString() + "%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.primary
                                )
                            }
                        }
                    }
                }

                // Priority breakdown
                item {
                    Text(
                        "Задачи по приоритету",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.sm)
                    )
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(Radius.lg)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            PriorityBar(label = "Высокий", count = state.highPriorityTasks, total = state.pendingTasks, color = AppTheme.colors.error)
                            PriorityBar(label = "Средний", count = state.mediumPriorityTasks, total = state.pendingTasks, color = AppTheme.colors.warning)
                            PriorityBar(label = "Низкий", count = state.lowPriorityTasks, total = state.pendingTasks, color = AppTheme.colors.success)
                        }
                    }
                }

                // Pomodoro focus time
                item {
                    Text(
                        "Фокус-время (Pomodoro)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.sm)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Schedule,
                            iconColor = AppTheme.colors.primary,
                            value = (state.pomodoroStats?.todayMinutes ?: 0).toString() + " мин",
                            label = "Сегодня"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Schedule,
                            iconColor = AppTheme.colors.primary,
                            value = (state.pomodoroStats?.weekMinutes ?: 0).toString() + " мин",
                            label = "Неделя"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Schedule,
                            iconColor = AppTheme.colors.primary,
                            value = (state.pomodoroStats?.monthMinutes ?: 0).toString() + " мин",
                            label = "Месяц"
                        )
                    }
                }

                // Gamification
                item {
                    Text(
                        "Достижения",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.sm)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Star,
                            iconColor = AppTheme.colors.warning,
                            value = (state.userStats?.totalPoints ?: 0).toString(),
                            label = "Очки"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.BarChart,
                            iconColor = AppTheme.colors.primary,
                            value = "Ур. " + (state.userStats?.level ?: 1).toString(),
                            label = "Уровень"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.LocalFireDepartment,
                            iconColor = AppTheme.colors.warning,
                            value = (state.userStats?.streak ?: 0).toString(),
                            label = "Серия дней"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AppTheme.colors.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun PriorityBar(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.onSurface)
            Text(count.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs).clip(RoundedCornerShape(Radius.full)),
            color = color,
            trackColor = AppTheme.colors.surfaceVariant
        )
    }
}