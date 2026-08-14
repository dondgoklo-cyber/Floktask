package com.taskmanager.presentation.screens.habits

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val showCreate by viewModel.showCreateDialog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habits)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openCreateDialog) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_habit))
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) { TaskListSkeleton() }
        } else if (state.habits.isEmpty()) {
            EmptyHabitsState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(state.habits, key = { it.habit.id ?: 0 }) { habitWithCompletion ->
                    HabitCard(
                        habitWithCompletion = habitWithCompletion,
                        onToggle = { viewModel.toggleCompletion(habitWithCompletion.habit.id ?: 0) }
                    )
                }
            }
        }
    }

    if (showCreate) {
        CreateHabitDialog(
            onDismiss = viewModel::closeCreateDialog,
            onCreate = { name, color, freq -> viewModel.createHabit(name, color, freq) }
        )
    }
}

@Composable
private fun EmptyHabitsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Spa,
                contentDescription = null,
                tint = AppTheme.colors.outline
            )
            Text(
                stringResource(R.string.no_habits),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.md)
            )
        }
    }
}

@Composable
private fun HabitCard(
    habitWithCompletion: HabitWithCompletion,
    onToggle: () -> Unit
) {
    val habit = habitWithCompletion.habit
    val accentColor = habit.color?.let { parseColor(it) } ?: Color(0xFFFF6D00)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Иконка-кружок с цветом привычки
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    habit.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (habitWithCompletion.currentStreak > 0) AppTheme.colors.warning
                        else AppTheme.colors.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "${habitWithCompletion.currentStreak} дн.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                    Text(
                        "· Лучшая: ${habitWithCompletion.bestStreak}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }

            // Кнопка отметки выполнения
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (habitWithCompletion.completedToday) accentColor
                            else AppTheme.colors.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (habitWithCompletion.completedToday) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateHabitDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("FF6D00") }
    var selectedFreq by remember { mutableStateOf("DAILY") }

    val colors = listOf(
        "FF6D00" to "Оранжевый",
        "2E7D32" to "Зелёный",
        "0277BD" to "Синий",
        "C62828" to "Красный",
        "ED6C02" to "Жёлтый"
    )
    val frequencies = listOf("DAILY" to "Ежедневно", "WEEKLY" to "Еженедельно")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_habit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.frequency), style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    frequencies.forEach { (value, label) ->
                        FilterChip(
                            selected = selectedFreq == value,
                            onClick = { selectedFreq = value },
                            label = { Text(label) }
                        )
                    }
                }
                Text("Цвет", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { (hex, _) ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseColor(hex))
                                .then(
                                    if (selectedColor == hex) Modifier.padding(2.dp)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == hex) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, "#$selectedColor", selectedFreq)
                    }
                }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

private fun parseColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        Color(android.graphics.Color.parseColor("#$clean"))
    } catch (_: Throwable) {
        Color(0xFFFF6D00)
    }
}
