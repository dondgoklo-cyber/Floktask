package com.taskmanager.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Note
import com.taskmanager.haptic.HapticType
import androidx.hilt.navigation.compose.inject
import com.taskmanager.haptic.HapticManager
import com.taskmanager.haptic.HapticType
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.data.repository.NoteExportManager
import com.taskmanager.presentation.theme.Spacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (Long) -> Unit,
    onFolderClick: (Long) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val showCreateFolder by viewModel.showCreateFolder.collectAsState()
    var pendingDeleteNote by remember { mutableStateOf<Note?>(null) }
    val hapticManager: HapticManager = inject()
    val haptic = remember(hapticManager) { { type: HapticType -> hapticManager.perform(type) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.notes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    val context = LocalContext.current
                    val exportManager = remember { NoteExportManager() }
                    val importLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.OpenDocument()
                    ) { uri ->
                        uri?.let {
                            context.contentResolver.openInputStream(it)?.use { stream ->
                                val md = stream.bufferedReader().readText()
                                val note = exportManager.importFromMarkdown(md)
                                viewModel.createNoteWithContent(note.title, note.contentMarkdown) { id -> onNoteClick(id) }
                            }
                        }
                    }
                    IconButton(onClick = {
                        importLauncher.launch(arrayOf("text/markdown", "text/plain", "*/*"))
                    }) {
                        Icon(Icons.Filled.FileUpload, contentDescription = "Импорт Markdown")
                    }
                    IconButton(onClick = {
                        haptic(HapticType.LIGHT)
                        viewModel.openCreateFolderDialog()
                    }) {
                        Icon(Icons.Filled.Folder, contentDescription = "Создать папку")
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
                contentDescription = stringResource(R.string.add_note),
                onClick = {
                    haptic(HapticType.LIGHT)
                    viewModel.createNote { id -> onNoteClick(id) }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        } else if (state.pinnedNotes.isEmpty() && state.recentNotes.isEmpty() && state.folders.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Description,
                title = stringResource(R.string.no_notes),
                subtitle = "Создайте заметку — идеи, конспекты, инструкции",
                modifier = Modifier.padding(padding),
                actionText = stringResource(R.string.add_note),
                onAction = {
                    haptic(HapticType.LIGHT)
                    viewModel.createNote { id -> onNoteClick(id) }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Folders
                if (state.folders.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.folders),
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xs)
                        )
                    }
                    items(state.folders, key = { it.id ?: 0 }) { folder ->
                        FolderRow(
                            name = folder.name,
                            onClick = { folder.id?.let(onFolderClick) }
                        )
                    }
                }

                // Pinned notes
                if (state.pinnedNotes.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.xs)
                        ) {
                            Icon(Icons.Filled.PushPin, contentDescription = null, tint = AppTheme.colors.primary, modifier = Modifier.size(16.dp))
                            Text(
                                stringResource(R.string.pinned),
                                style = MaterialTheme.typography.labelLarge,
                                color = AppTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                    items(state.pinnedNotes, key = { it.id ?: 0 }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { note.id?.let(onNoteClick) },
                            onPin = {
                                haptic(HapticType.SELECTION)
                                viewModel.togglePin(note)
                            },
                            onDelete = { pendingDeleteNote = note }
                        )
                    }
                }

                // Recent notes
                if (state.recentNotes.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.recent),
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.xs)
                        )
                    }
                    items(state.recentNotes, key = { it.id ?: 0 }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { note.id?.let(onNoteClick) },
                            onPin = {
                                haptic(HapticType.SELECTION)
                                viewModel.togglePin(note)
                            },
                            onDelete = { pendingDeleteNote = note }
                        )
                    }
                }
            }
        }
    }

    if (showCreateFolder) {
        var folderName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = viewModel::closeCreateFolderDialog,
            title = { Text("Новая папка") },
            text = {
                AppTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (folderName.isNotBlank()) viewModel.createFolder(folderName)
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeCreateFolderDialog) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    pendingDeleteNote?.let { note ->
        AlertDialog(
            onDismissRequest = { pendingDeleteNote = null },
            title = { Text("Удалить заметку?") },
            text = { Text("«" + note.title.ifBlank { "Без названия" } + "»") },
            confirmButton = {
                TextButton(onClick = {
                    note.id?.let { viewModel.deleteNote(it) }
                    pendingDeleteNote = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteNote = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit
) {
    val hapticManager: HapticManager = inject()
    val haptic = remember(hapticManager) { { type: HapticType -> hapticManager.perform(type) } }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            haptic(HapticType.LIGHT)
            onClick()
        },
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(AppTheme.colors.primaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = AppTheme.colors.primary, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        note.title.ifBlank { "Без названия" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    note.contentMarkdown.takeIf { it.isNotBlank() }?.let { content ->
                        val preview = content.lines().filter { it.isNotBlank() }.firstOrNull() ?: ""
                        if (preview.isNotBlank()) {
                            Text(
                                preview,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Text(
                    formatDate(note.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.outlineVariant
                )
                IconButton(onClick = onPin) {
                    Icon(
                        Icons.Filled.PushPin,
                        contentDescription = "Закрепить",
                        tint = if (note.pinned) AppTheme.colors.primary else AppTheme.colors.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FolderRow(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Icon(Icons.Filled.Folder, contentDescription = null, tint = AppTheme.colors.primary)
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppTheme.colors.outlineVariant, modifier = Modifier.size(20.dp))
        }
    }
}

private fun formatDate(instant: Instant): String {
    val zone = ZoneId.systemDefault()
    return instant.atZone(zone).toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
}
