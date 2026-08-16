package com.taskmanager.presentation.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.priorityColor
import com.taskmanager.security.UserPrefs
import com.taskmanager.presentation.screens.tasks.TaskDetailSheet
import com.taskmanager.presentation.screens.tasks.QuickAddSheet
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.domain.model.TransactionType
import com.taskmanager.presentation.screens.finance.formatMoney
import com.taskmanager.presentation.screens.finance.formatSignedMoney
import com.taskmanager.presentation.screens.finance.AddTransactionSheet
import com.taskmanager.presentation.screens.finance.FinanceViewModel
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.CreateMenuSheet
import com.taskmanager.presentation.screens.voice.VoiceTaskSheet
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.Account
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel(),
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    onStartFocus: (Long) -> Unit = {},
    onAllFinance: () -> Unit = {},
    onAddHabit: () -> Unit = {},
    onAddProject: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAllNotes: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var detailTaskId by remember { mutableStateOf<Long?>(null) }
    var showQuickAdd by remember { mutableStateOf(false) }
    var showCreateMenu by remember { mutableStateOf(false) }
    var showVoice by remember { mutableStateOf(false) }
    var addTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    val financeViewModel: FinanceViewModel = hiltViewModel()
    val quickAddViewModel: com.taskmanager.presentation.screens.tasks.QuickAddViewModel = hiltViewModel()
    val financeState by financeViewModel.state.collectAsState()

    if (detailTaskId != null) {
        TaskDetailSheet(
            taskId = detailTaskId!!,
            onDismiss = { detailTaskId = null },
            onEdit = { id ->
                detailTaskId = null
                onTaskClick(id)
            },
            onStartFocus = onStartFocus,
            onNoteClick = { noteId ->
                detailTaskId = null
                onAllNotes()
            }
        )
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onCreated = { _ ->
                showQuickAdd = false
            }
        )
    }

    if (showCreateMenu) {
        CreateMenuSheet(
            onDismiss = { showCreateMenu = false },
            onTask = { showQuickAdd = true },
            onHabit = onAddHabit,
            onIncome = { addTransactionType = TransactionType.INCOME },
            onExpense = { addTransactionType = TransactionType.EXPENSE },
            onProject = onAddProject,
            onNote = onAddNote,
            onVoice = { showVoice = true }
        )
    }

    addTransactionType?.let { txType ->
        AddTransactionSheet(
            categories = financeState.categories,
            accounts = financeState.accounts,
            onDismiss = { addTransactionType = null },
            onCreate = { amount, type, currency, categoryId, accountId, date, note ->
                financeViewModel.createTransaction(amount, type, currency, categoryId, accountId, date, note)
                addTransactionType = null
            },
            initialType = txType
        )
    }

    if (showVoice) {
        VoiceTaskSheet(
            onDismiss = { showVoice = false },
            onCreate = { title, date, time, priority, recurrence ->
                showVoice = false
                // Создаём задачу через QuickAddViewModel
                quickAddViewModel.createTaskFromVoice(title, date, time, priority, recurrence)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.create),
                onClick = { showCreateMenu = true }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item { GreetingHeader() }
                item { ProgressCard(state) }
                item { SummaryRow(state) }
                if (state.inboxTasks.isNotEmpty()) {
                    item { InboxPreviewCard(state, onTaskClick) }
                }
                if (state.recentTransactions.isNotEmpty() || state.financeBalance != 0.0) {
                    item { FinanceSummaryCard(state, onAllFinance) }
                }
                if (state.recentNotes.isNotEmpty()) {
                    item { NotesPreviewCard(state, onAllNotes) }
                }
                if (state.nextTasks.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.next_tasks)) }
                    items(state.nextTasks, key = { it.id ?: 0 }) { task ->
                        NextTaskRow(task, onClick = { detailTaskId = task.id ?: 0 })
                    }
                }
                if (state.focusMinutesToday > 0) {
                    item { FocusCard(state) }
                }
                if (state.habitsTotal > 0) {
                    item { HabitsCard(state) }
                }
            }
        }
    }
}

@Composable
private fun GreetingHeader() {
    val context = LocalContext.current
    val userPrefs = remember { UserPrefs(context) }
    val hour = LocalTime.now().hour
    val name = userPrefs.userName
    val greeting = when (hour) {
        in 5..11 -> stringResource(R.string.good_morning)
        in 12..17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_evening)
    }
    val text = if (name.isNotBlank()) "$greeting, $name!" else greeting
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colors.onBackground
    )
}

@Composable
private fun ProgressCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AppTheme.colors.primary.copy(alpha = 0.08f),
                            AppTheme.colors.primaryContainer.copy(alpha = 0.04f)
                        )
                    )
                )
        ) {
        Column(Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.todays_progress),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.primary
                )
            }
            Spacer(Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = state.progress,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(Radius.full)),
                color = AppTheme.colors.primary,
                trackColor = AppTheme.colors.surfaceVariant
            )
        }
        }
    }
}

@Composable
private fun SummaryRow(state: TodayUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.totalToday.toString(),
            label = stringResource(R.string.tasks_total),
            color = AppTheme.colors.info
        )
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.completedToday.toString(),
            label = stringResource(R.string.tasks_completed),
            color = AppTheme.colors.success
        )
        SummaryStat(
            modifier = Modifier.weight(1f),
            value = state.overdueCount.toString(),
            label = stringResource(R.string.tasks_overdue),
            color = AppTheme.colors.danger
        )
    }
}

@Composable
private fun SummaryStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(color)
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurface
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xs)
    )
}

@Composable
private fun NextTaskRow(task: Task, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            task.startTime?.let { start ->
                val time = start.atZone(ZoneId.systemDefault()).toLocalTime()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.primary
                    )
                    task.durationMinutes?.let { mins ->
                        Text(
                            "${mins}м",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            } ?: Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = AppTheme.colors.outline
            )
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape).background(priorityColor(task.priority))
            )
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = AppTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun FocusCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = AppTheme.colors.primary)
            }
            Column {
                Text(
                    stringResource(R.string.focus_time_today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
                Text(
                    "${state.focusMinutesToday / 60}ч ${state.focusMinutesToday % 60}м",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun HabitsCard(state: TodayUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.habits_today),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${state.habitsCompleted}/${state.habitsTotal}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.primary
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                repeat(state.habitsTotal) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < state.habitsCompleted) AppTheme.colors.success
                                else AppTheme.colors.surfaceVariant
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun FinanceSummaryCard(state: TodayUiState, onAllFinance: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onAllFinance,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = com.taskmanager.presentation.theme.Elevation.none),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(com.taskmanager.presentation.theme.Radius.lg),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AppTheme.colors.success.copy(alpha = 0.06f),
                            AppTheme.colors.primary.copy(alpha = 0.04f)
                        )
                    )
                )
        ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg)
        ) {
            androidx.compose.material3.Text(
                "Финансы",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(Spacing.sm))
            androidx.compose.material3.Text(
                formatMoney(state.financeBalance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = AppTheme.colors.primary
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(Spacing.sm))
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.Text(
                        "Доходы",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                    androidx.compose.material3.Text(
                        formatSignedMoney(state.financeIncome),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = AppTheme.colors.success
                    )
                }
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.Text(
                        "Расходы",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                    androidx.compose.material3.Text(
                        formatSignedMoney(-state.financeExpense),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = AppTheme.colors.danger
                    )
                }
            }
            // Последние операции
            if (state.recentTransactions.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(Modifier.height(Spacing.md))
                state.recentTransactions.forEach { tx ->
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        androidx.compose.material3.Text(
                            tx.note ?: (if (tx.type == TransactionType.INCOME) "Доход" else "Расход"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        androidx.compose.material3.Text(
                            formatSignedMoney(if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            color = if (tx.type == TransactionType.INCOME) AppTheme.colors.success else AppTheme.colors.danger
                        )
                    }
                }
                androidx.compose.material3.TextButton(onClick = onAllFinance) {
                    androidx.compose.material3.Text("Все финансы →")
                }
            }
        }
        }
    }
}

@Composable
private fun NotesPreviewCard(state: TodayUiState, onAllNotes: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onAllNotes,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = com.taskmanager.presentation.theme.Elevation.none),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(com.taskmanager.presentation.theme.Radius.lg),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    "Заметки",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                androidx.compose.material3.TextButton(onClick = onAllNotes) {
                    androidx.compose.material3.Text("Все →")
                }
            }
            state.recentNotes.forEach { note ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "●",
                        color = AppTheme.colors.info,
                        style = MaterialTheme.typography.labelSmall
                    )
                    androidx.compose.material3.Text(
                        note.title.ifBlank { "Без названия" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxPreviewCard(state: TodayUiState, onTaskClick: (Long) -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onTaskClick,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = com.taskmanager.presentation.theme.Elevation.none),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(com.taskmanager.presentation.theme.Radius.lg),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    "Входящие",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                androidx.compose.material3.Text(
                    "${state.inboxTasks.size}",
                    style = MaterialTheme.typography.titleSmall,
                    color = AppTheme.colors.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            state.inboxTasks.forEach { task ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.outline
                    )
                    androidx.compose.material3.Text(
                        task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
