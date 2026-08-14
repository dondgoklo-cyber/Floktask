package com.taskmanager.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        when (val s = state) {
            CalendarState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is CalendarState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.message) }

            is CalendarState.Success -> {
                val week = selectedDate.let { getWeekDays(it) }
                val today = LocalDate.now()
                val dayTasks = s.days.flatMap { it.tasks }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(vertical = Spacing.sm)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            week.forEach { date ->
                                DayCell(
                                    date = date,
                                    isToday = date == today,
                                    isSelected = date == selectedDate,
                                    hasTasks = dayTasks.any { task ->
                                        task.deadline?.atZone(ZoneId.systemDefault())?.toLocalDate() == date ||
                                            task.startTime?.atZone(ZoneId.systemDefault())?.toLocalDate() == date
                                    },
                                    onClick = { selectedDate = date }
                                )
                            }
                        }
                    }

                    val selectedFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                    item {
                        Text(
                            text = selectedDate.format(selectedFormatter),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        )
                    }

                    val tasksForDay = dayTasks.filter { task ->
                        task.deadline?.atZone(ZoneId.systemDefault())?.toLocalDate() == selectedDate ||
                            task.startTime?.atZone(ZoneId.systemDefault())?.toLocalDate() == selectedDate
                    }

                    if (tasksForDay.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Нет задач на этот день", color = AppTheme.colors.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(tasksForDay, key = { it.id ?: 0 }) { task ->
                            CalendarTaskCard(task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    hasTasks: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isSelected -> AppTheme.colors.primary
        isToday -> AppTheme.colors.primaryContainer
        else -> AppTheme.colors.surface
    }
    val fg = when {
        isSelected -> AppTheme.colors.onPrimary
        else -> AppTheme.colors.onSurface
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = Spacing.sm)
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).replace(".", ""),
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = fg
        )
        if (hasTasks) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.primary)
            )
        }
    }
}

@Composable
private fun CalendarTaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(12.dp).clip(CircleShape).background(priorityColor(task.priority))
            )
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                task.startTime?.let { start ->
                    val time = start.atZone(ZoneId.systemDefault()).toLocalTime()
                    Text(
                        time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
                if (task.isCompleted) {
                    Text("Выполнено", style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.primary)
                }
            }
        }
    }
}

private fun getWeekDays(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.with(DayOfWeek.MONDAY)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}
