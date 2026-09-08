package com.taskmanager.presentation.screens.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TaskCard
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.screens.tasks.QuickAddSheet
import com.taskmanager.presentation.screens.tasks.TaskDetailSheet
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onEditTask: (Long) -> Unit,
    viewModel: InboxViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var detailTaskId by remember { mutableStateOf<Long?>(null) }
    var showQuickAdd by remember { mutableStateOf(false) }

    if (detailTaskId != null) {
        TaskDetailSheet(
            taskId = detailTaskId!!,
            onDismiss = { detailTaskId = null },
            onEdit = { id ->
                detailTaskId = null
                onEditTask(id)
            },
            onStartFocus = { id ->
                detailTaskId = null
                onEditTask(id)
            }
        )
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onCreated = { _ -> showQuickAdd = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.inbox)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            com.taskmanager.presentation.components.AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_task),
                onClick = { showQuickAdd = true }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    TaskListSkeleton()
                }
            }
            state.tasks.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.Inbox,
                    title = stringResource(R.string.inbox_empty_title),
                    message = stringResource(R.string.inbox_empty_subtitle),
                    modifier = Modifier.padding(padding),
                    actionLabel = stringResource(R.string.add_task),
                    onAction = { showQuickAdd = true }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(state.tasks, key = { it.id ?: 0 }) { task ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.completeTask(task.id ?: 0)
                                    true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> AppTheme.colors.success.copy(alpha = 0.2f)
                                        SwipeToDismissBoxValue.EndToStart -> AppTheme.colors.secondaryContainer.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }
                                )
                                val icon by animateIntAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> R.drawable.ic_check
                                        SwipeToDismissBoxValue.EndToStart -> R.drawable.ic_schedule
                                        else -> null
                                    } ?: Icons.Default.Check
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = if (direction == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Check else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (direction == SwipeToDismissBoxValue.StartToEnd) AppTheme.colors.success else AppTheme.colors.onSecondaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            },
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.completeTask(task.id ?: 0)
                                    true
                                } else false
                            }
                        ) {
                            TaskRow(
                                task = task,
                                projectName = null,
                                projectColor = null,
                                isSelected = false,
                                onCheckedChange = { checked -> if (checked) viewModel.completeTask(task.id ?: 0) },
                                onClick = { detailTaskId = task.id ?: 0 },
                                onLongClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
