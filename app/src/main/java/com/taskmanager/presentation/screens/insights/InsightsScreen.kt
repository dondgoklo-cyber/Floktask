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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
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
                        "Аналитика",
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
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = Spacing.lg,
                    vertical = Spacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    StatCard(
                        icon = Icons.Filled.CheckCircle,
                        iconTint = AppTheme.colors.primary,
                        title = "Задачи",
                        mainValue = state.tasksCompletedToday.toString(),
                        mainLabel = "выполнено сегодня",
                        secondaryValues = listOf(
                            "${state.tasksCompletedWeek} за неделю",
                            "${state.tasksTotal} всего выполнено"
                        )
                    )
                }

                item {
                    StatCard(
                        icon = Icons.Filled.Schedule,
                        iconTint = AppTheme.colors.primary,
             
           title = "Фокус (Pomodoro)",
                        mainValue = state.pomodoroStats.todayCount.toString(),
                        mainLabel = "сессий сегодня",
                        secondaryValues = listOf(
                            "${state.pomodoroStats.todayMinutes} мин сегодня",
                            "${state.pomodoroStats.weekCount} сессий за неделю",
                            "${state.pomodoroStats.weekMinutes} мин за неделю"
                        )
                    )
                }

                item {
                    StatCard(
                        icon = Icons.Filled.AccountBalanceWallet,
                        iconTint = AppTheme.colors.secondary,
                        title = "Финансы",
                        mainValue = String.format("%.0f", state.balance),
                        mainLabel = "баланс",
                        secondaryValues = listOf(
                            "Доход: ${String.format("%.0f", state.totalIncome)}",
                            "Расход: ${String.format("%.0f", state.totalExpense)}"
                        )
                    )
                }

                item {
                    StatCard(
                        icon = Icons.Filled.LocalFireDepartment,
                        iconTint = AppTheme.colors.warning,
                        title = "Месячная статистика фокуса",
                        mainValue = state.pomodoroStats.monthCount.toString(),
                        mainLabel = "сессий за месяц",
                        secondaryValues = listOf(
                            "${state.pomodoroStats.monthMinutes} минут за месяц"
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    mainValue: String,
    mainLabel: String,
    secondaryValues: List<String>
) {
    Card(
        modifier = Mod
ifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    mainValue,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = iconTint
                )
                Text(
                    mainLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            secondaryValues.forEach { value ->
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
