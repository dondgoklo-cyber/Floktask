package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.components.TaskCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val tasksState by viewModel.tasksState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val priorityFilter by viewModel.priorityFilter.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tasks)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("Поиск задач...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Фильтр статуса
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip("Все", filter == TaskFilter.ALL) { viewModel.setFilter(TaskFilter.ALL) }
                FilterChip("Активные", filter == TaskFilter.ACTIVE) { viewModel.setFilter(TaskFilter.ACTIVE) }
                FilterChip("Готово", filter == TaskFilter.COMPLETED) { viewModel.setFilter(TaskFilter.COMPLETED) }
            }

            // Фильтр приоритета
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip("Все", priorityFilter == null) { viewModel.setPriorityFilter(null) }
                Priority.entries.forEach { priority ->
                    PriorityChip(
                        text = when (priority) {
                            Priority.HIGH -> "Высокий"
                            Priority.MEDIUM -> "Средний"
                            Priority.LOW -> "Низкий"
                            Priority.NONE -> "Нет"
                        },
                        selected = priorityFilter == priority
                    ) { viewModel.setPriorityFilter(priority) }
                }
            }

            when (val state = tasksState) {
                TasksState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is TasksState.Success -> {
                    if (state.tasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text(stringResource(R.string.no_tasks)) }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.tasks, key = { it.id ?: 0 }) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onTaskClick = { onTaskClick(task.id ?: 0) },
                                    onCheckedChange = { viewModel.toggleComplete(task) },
                                    onDelete = {
                                        viewModel.deleteTask(task.id ?: 0)
                                    }
                                )
                            }
                        }
                    }
                }

                is TasksState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(state.message) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else if (value == SwipeToDismissBoxValue.StartToEnd) {
                onCheckedChange(true)
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isDelete = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val isComplete = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalArrangement = if (isDelete) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                } else if (isComplete) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Выполнить", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) {
        TaskCard(
            task = task,
            onClick = onTaskClick,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityChip(text: String, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelSmall) }
    )
}
