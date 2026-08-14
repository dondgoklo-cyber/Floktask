package com.taskmanager.presentation.screens.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Tag
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.TagColorPalette
import com.taskmanager.presentation.components.TaskListSkeleton
import com.taskmanager.presentation.components.parseTagColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    onBack: () -> Unit,
    viewModel: TagsViewModel = hiltViewModel()
) {
    val state by viewModel.tagsState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    var pendingDeleteTag by remember { mutableStateOf<Tag?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tags)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.new_tag),
                onClick = viewModel::openCreateDialog
            )
        }
    ) { padding ->
        when (val s = state) {
            TagsState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) { TaskListSkeleton() }

            is TagsState.Success -> {
                if (s.tags.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Label,
                        title = stringResource(R.string.no_tags),
                        subtitle = "Создайте теги, чтобы помечать задачи цветом",
                        modifier = Modifier.padding(padding),
                        actionText = stringResource(R.string.new_tag),
                        onAction = viewModel::openCreateDialog
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.xl
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(s.tags, key = { it.id ?: 0 }) { tag ->
                            TagRow(
                                tag = tag,
                                onEdit = { viewModel.openEditDialog(tag) },
                                onDelete = { pendingDeleteTag = tag }
                            )
                        }
                    }
                }
            }

            is TagsState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.message) }
        }
    }

    if (dialogState.isOpen) {
        TagDialog(
            state = dialogState,
            onNameChange = viewModel::onNameChange,
            onColorChange = viewModel::onColorChange,
            onDismiss = viewModel::closeDialog,
            onSave = viewModel::saveTag
        )
    }

    pendingDeleteTag?.let { tag ->
        AlertDialog(
            onDismissRequest = { pendingDeleteTag = null },
            title = { Text(stringResource(R.string.delete_tag_title)) },
            text = { Text("«${tag.name}» — ${stringResource(R.string.delete_tag_message)}") },
            confirmButton = {
                TextButton(onClick = {
                    tag.id?.let { viewModel.deleteTag(it) }
                    pendingDeleteTag = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTag = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun TagRow(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val color = parseTagColor(tag.color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Text(
                text = tag.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_tag),
                    tint = AppTheme.colors.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = AppTheme.colors.danger
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagDialog(
    state: TagDialogState,
    onNameChange: (String) -> Unit,
    onColorChange: (androidx.compose.ui.graphics.Color) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val selectedColor = parseTagColor(state.colorHex)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (state.editingTag != null) R.string.edit_tag else R.string.new_tag))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                AppTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.tag_name)) },
                    isError = state.nameError,
                    supportingText = {
                        if (state.nameError) Text("Введите название тега")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    stringResource(R.string.select_color),
                    style = MaterialTheme.typography.labelLarge,
                    color = AppTheme.colors.onSurfaceVariant
                )
                TagColorPalette(
                    selectedColor = selectedColor,
                    onColorSelected = onColorChange
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
