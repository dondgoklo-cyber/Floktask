package com.taskmanager.presentation.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Project
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.util.HapticManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onProjectClick: (Long) -> Unit = {},
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val state by viewModel.projectsState.collectAsState()
    val showCreate by viewModel.showCreateDialog.collectAsState()
    val hapticManager = viewModel.hapticManager

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.projects)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            com.taskmanager.presentation.components.AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.new_project),
                onClick = viewModel::openCreateDialog,
                onLongClick = viewModel::onFabLongClick,
                hapticManager = hapticManager
            )
        }
    ) { padding ->
        when (val s = state) {
            ProjectsState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) { TaskListSkeleton() }

            is ProjectsState.Success -> {
                if (s.projects.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Folder,
                        title = stringResource(R.string.no_projects),
                        subtitle = "Создайте проект, чтобы группировать задачи",
                        modifier = Modifier.padding(padding),
                        actionText = stringResource(R.string.new_project),
                        onAction = viewModel::openCreateDialog
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(s.projects, key = { it.first.id ?: 0 }) { (project, stats) ->
                            ProjectCard(project, stats, onClick = { project.id?.let(onProjectClick) })
                        }
                    }
                }
            }

            is ProjectsState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.message) }
        }
    }

    if (showCreate) {
        CreateProjectDialog(
            onDismiss = viewModel::closeCreateDialog,
            onCreate = { title, description -> viewModel.createProject(title, description) }
        )
    }
}

@Composable
private fun ProjectCard(project: Project, stats: ProjectStats, onClick: () -> Unit = {}) {
    val accentColor = project.color?.let { parseColor(it) } ?: Color(0xFFFF6D00)
    val progress = if (stats.total > 0) stats.completed.toFloat() / stats.total else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        shape = RoundedCornerShape(Radius.lg)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Folder, contentDescription = null, tint = accentColor)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                project.description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
        if (stats.total > 0) {
            androidx.compose.foundation.layout.Spacer(Modifier.size(Spacing.sm))
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(Radius.full)),
                color = accentColor,
                trackColor = AppTheme.colors.surfaceVariant
            )
        }
        }
    }
}

@Composable
private fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_project)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                AppTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onCreate(title, description.ifBlank { null }) }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

private fun parseColor(hex: String): androidx.compose.ui.graphics.Color {
    return try {
        val clean = hex.removePrefix("#")
        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#$clean"))
    } catch (_: Throwable) {
        Color(0xFFFF6D00)
    }
}
