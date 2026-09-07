package com.taskmanager.presentation.screens.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val phaseColor = when (state.type) {
        PomodoroType.WORK -> Color(0xFFE85D04)
        PomodoroType.SHORT_BREAK -> Color(0xFF2A9D8F)
        PomodoroType.LONG_BREAK -> Color(0xFF264653)
    }
    val animatedColor by animateColorAsState(targetValue = phaseColor, label = "phaseColor")

    val phaseLabel = when (state.type) {
        PomodoroType.WORK -> "Работа"
        PomodoroType.SHORT_BREAK -> "Короткий перерыв"
        PomodoroType.LONG_BREAK -> "Длинный перерыв"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Header row: title + settings button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Фокус", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                IconButton(onClick = viewModel::showSettingsDialog) {
                    Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                }
            }
        }

        // Cycle indicator dots
        item {
            CycleIndicator(
                current = state.cyclePosition,
                total = state.pomodorosBeforeLongBreak,
                color = animatedColor
            )
        }

        // Type selector
        item {
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
        }

        // Timer circle
        item {
            val progress = if (state.totalSeconds > 0) {
                state.remainingSeconds.toFloat() / state.totalSeconds.toFloat()
            } else 0f

            Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 10.dp,
                    color = animatedColor
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val mins = state.remainingSeconds / 60
                    val secs = state.remainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = animatedColor
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = phaseLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (state.taskTitle != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.taskTitle!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Controls
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { viewModel.reset() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Сброс", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Сброс")
                }
                Button(
                    onClick = { if (state.isRunning) viewModel.pause() else viewModel.start() },
                    colors = ButtonDefaults.buttonColors(containerColor = animatedColor),
                ) {
                    Icon(
                        if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (state.isRunning) "Пауза" else "Старт")
                }
                OutlinedButton(onClick = { viewModel.skip() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Пропустить", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Пропустить")
                }
            }
        }

        // Task selection
        item {
            OutlinedButton(
                onClick = { viewModel.showTaskPicker() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.TaskAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(state.taskTitle ?: "Выбрать задачу для фокуса")
            }
        }

        // Stats card
        item {
            val stats = state.stats
            if (stats != null) {
                StatsCard(stats = stats, dailyGoal = state.dailyGoal)
            }
        }
    }

    // Settings dialog
    if (state.showSettings) {
        PomodoroSettingsDialog(state = state, viewModel = viewModel)
    }

    // Task picker dialog
    if (state.showTaskPicker) {
        TaskPickerDialog(state = state, viewModel = viewModel)
    }
}

@Composable
private fun CycleIndicator(current: Int, total: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            val isActive = i < current
            Box(
                modifier = Modifier
                    .size(if (isActive) 16.dp else 12.dp)
                    .clip(CircleShape)
                    .background(if (isActive) color else color.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun StatsCard(stats: com.taskmanager.domain.usecase.pomodoro.PomodoroStats, dailyGoal: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Статистика", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${stats.todayCount}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("сегодня", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
                Column {
                    Text("${stats.todayMinutes} мин", style = MaterialTheme.typography.titleMedium)
                    Text("время", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
                Column {
                    Text("${stats.weekCount}", style = MaterialTheme.typography.titleMedium)
                    Text("за неделю", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            // Daily goal progress
            val goalProgress = if (dailyGoal > 0) (stats.todayCount.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
            Spacer(Modifier.height(4.dp))
            Text("Цель: $dailyGoal помодоро в день", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            LinearProgressIndicator(
                progress = { goalProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFFE85D04),
            )
        }
    }
}

@Composable
private fun PomodoroSettingsDialog(state: FocusUiState, viewModel: FocusViewModel) {
    var workDuration by remember { mutableStateOf(state.workDuration.toFloat()) }
    var shortBreak by remember { mutableStateOf(state.shortBreakDuration.toFloat()) }
    var longBreak by remember { mutableStateOf(state.longBreakDuration.toFloat()) }
    var beforeLong by remember { mutableStateOf(state.pomodorosBeforeLongBreak.toFloat()) }
    var autoStartBreaks by remember { mutableStateOf(state.autoStartBreaks) }
    var autoStartPomodoros by remember { mutableStateOf(state.autoStartPomodoros) }
    var sound by remember { mutableStateOf(state.soundEnabled) }
    var vibration by remember { mutableStateOf(state.vibrationEnabled) }
    var dailyGoal by remember { mutableStateOf(state.dailyGoal.toFloat()) }

    AlertDialog(
        onDismissRequest = viewModel::hideSettingsDialog,
        title = { Text("Настройки Pomodoro") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { SettingSlider("Работа (мин)", workDuration, 1f..90f, workDuration.toFloat()) { workDuration = it } }
                item { SettingSlider("Короткий перерыв (мин)", shortBreak, 1f..30f, shortBreak.toFloat()) { shortBreak = it } }
                item { SettingSlider("Длинный перерыв (мин)", longBreak, 1f..60f, longBreak.toFloat()) { longBreak = it } }
                item { SettingSlider("Помодоро до длинного перерыва", beforeLong, 2f..10f, beforeLong.toFloat()) { beforeLong = it } }
                item { SettingSlider("Дневная цель (помодоро)", dailyGoal, 1f..30f, dailyGoal.toFloat()) { dailyGoal = it } }
                item { SettingSwitch("Авто-старт перерывов", autoStartBreaks) { autoStartBreaks = it } }
                item { SettingSwitch("Авто-старт работы", autoStartPomodoros) { autoStartPomodoros = it } }
                item { SettingSwitch("Звук уведомления", sound) { sound = it } }
                item { SettingSwitch("Вибрация", vibration) { vibration = it } }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.updateSettings(
                    workDuration = workDuration.toInt(),
                    shortBreakDuration = shortBreak.toInt(),
                    longBreakDuration = longBreak.toInt(),
                    pomodorosBeforeLongBreak = beforeLong.toInt(),
                    autoStartBreaks = autoStartBreaks,
                    autoStartPomodoros = autoStartPomodoros,
                    soundEnabled = sound,
                    vibrationEnabled = vibration,
                    dailyGoal = dailyGoal.toInt()
                )
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = viewModel::hideSettingsDialog) { Text("Отмена") } }
    )
}

@Composable
private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, displayValue: Float, onChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${displayValue.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun TaskPickerDialog(state: FocusUiState, viewModel: FocusViewModel) {
    var search by remember { mutableStateOf("") }
    val filtered = if (search.isBlank()) state.tasks else state.tasks.filter { it.title.contains(search, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = viewModel::hideTaskPicker,
        title = { Text("Выбрать задачу") },
        text = {
            Column {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Поиск...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    item {
                        TextButton(onClick = { viewModel.selectTask(null) }) {
                            Text("Без задачи")
                        }
                    }
                    items(filtered) { task ->
                        TextButton(
                            onClick = { viewModel.selectTask(task.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(task.title, modifier = Modifier.fillMaxWidth(), maxLines = 1)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = viewModel::hideTaskPicker) { Text("Закрыть") } }
    )��