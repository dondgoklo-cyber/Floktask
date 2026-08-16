package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val tasksState by viewModel.tasksState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { paddingValues ->
        when (val state = tasksState) {
            TasksState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is TasksState.Success -> {
                if (state.tasks.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.TaskAlt,
                        title = "No tasks yet",
                        message = "Add your first task to get started. Try the Quick Add bar.",
                        actionLabel = stringResource(R.string.add_task),
                        onAction = onAddTaskClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.tasks, key = { it.id ?: 0 }) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onTaskClick(task.id ?: 0) },
                                onCheckedChange = { viewModel.toggleComplete(task) }
                            )
                        }
                    }
                }
            }

            is TasksState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { Text(state.message) }
        }
    }
}
