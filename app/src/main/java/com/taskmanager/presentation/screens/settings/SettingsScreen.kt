package com.taskmanager.presentation.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.repository.LocalBackup
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
private val DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")

@Composable
fun SettingsScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val state = uiState.backupState

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { viewModel.exportTo(it) } }

    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.importFrom(it) } }

    val busy = state is BackupState.Exporting || state is BackupState.Importing ||
        state is BackupState.SavingLocal

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Backup & Restore", style = MaterialTheme.typography.headlineSmall)
            }

            // Local backup now
            item {
                Button(
                    onClick = viewModel::backupNow,
                    enabled = !busy
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Text("  Backup now")
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val name = "floktask_${Instant.now().atZone(ZoneId.systemDefault()).format(TIMESTAMP_FORMATTER)}.json"
                            createFileLauncher.launch(name)
                        },
                        enabled = !busy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null)
                        Text(" Export")
                    }
                    OutlinedButton(
                        onClick = { openFileLauncher.launch(arrayOf("application/json")) },
                        enabled = !busy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.CloudDownload, contentDescription = null)
                        Text(" Import")
                    }
                }
            }

            when (state) {
                BackupState.Idle -> {}
                BackupState.Exporting -> item { ProgressRow("Exporting…") }
                BackupState.Importing -> item { ProgressRow("Importing…") }
                BackupState.SavingLocal -> item { ProgressRow("Saving backup…") }
                is BackupState.ExportSuccess -> item {
                    ResultMessage("Exported ${state.taskCount} task(s).")
                }
                is BackupState.ImportSuccess -> item {
                    ResultMessage(
                        "Imported ${state.result.tasksRestored} task(s), " +
                            "${state.result.projectsRestored} project(s), " +
                            "${state.result.tagsRestored} tag(s)."
                    )
                }
                is BackupState.LocalBackupSaved -> item {
                    ResultMessage("Backup saved: ${state.fileName}")
                }
                is BackupState.Restored -> item {
                    ResultMessage(
                        "Restored ${state.result.tasksRestored} task(s), " +
                            "${state.result.projectsRestored} project(s), " +
                            "${state.result.tagsRestored} tag(s)."
                    )
                }
                is BackupState.Error -> item {
                    ResultMessage("Error: ${state.message}", isError = true)
                }
            }

            if (uiState.localBackups.isNotEmpty()) {
                item {
                    Text(
                        "Local backups",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item { HorizontalDivider() }
                items(uiState.localBackups, key = { it.fileName }) { backup ->
                    LocalBackupRow(
                        backup = backup,
                        onRestore = { viewModel.restoreLocal(backup.fileName) },
                        onDelete = { viewModel.deleteLocal(backup.fileName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Text(text)
    }
}

@Composable
private fun ResultMessage(text: String, isError: Boolean = false) {
    Text(
        text = text,
        color = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun LocalBackupRow(
    backup: LocalBackup,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val zone = ZoneId.systemDefault()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(backup.fileName, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${Instant.ofEpochMilli(backup.createdAtMillis).atZone(zone).format(DISPLAY_FORMATTER)}  ·  ${formatSize(backup.sizeBytes)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        IconButton(onClick = onRestore) {
            Icon(Icons.Filled.Restore, contentDescription = "Restore")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes >= 1_048_576 -> String.format(Locale.US, "%.1f MB", bytes / 1_048_576.0)
    bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
    else -> "$bytes B"
}
