package com.taskmanager.presentation.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.security.PinMode
import com.taskmanager.security.UserPrefs
import com.taskmanager.security.PinScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val userPrefs = remember { UserPrefs(context) }
    var userName by remember { mutableStateOf(userPrefs.userName) }
    var hasPin by remember { mutableStateOf(userPrefs.hasPin) }
    var nameInput by remember { mutableStateOf(userPrefs.userName) }
    var showPinScreen by remember { mutableStateOf<PinMode?>(null) }
    var showRemovePinDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(state.userName) }

    if (showPinScreen != null) {
        PinScreen(
            mode = showPinScreen!!,
            userName = userName,
            userPrefs = userPrefs,
            onSuccess = {
                if (showPinScreen == PinMode.CREATE || showPinScreen == PinMode.CHANGE) {
                    hasPin = true
                }
                showPinScreen = null
            },
            onCancel = { showPinScreen = null }
        )
        return
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.exportToUri(it, onSuccess = {}, onError = {})
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importFromUri(it, onSuccess = {}, onError = {})
        }
    }

    if (showRemovePinDialog) {
        AlertDialog(
            onDismissRequest = { showRemovePinDialog = false },
            title = { Text(stringResource(R.string.pin_remove)) },
            text = { Text(stringResource(R.string.pin_remove_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    userPrefs.removePin()
                    hasPin = false
                    showRemovePinDialog = false
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showRemovePinDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Имя пользователя
            Text(
                stringResource(R.string.profile),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Spacing.md)
            )
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text(stringResource(R.string.user_name)) },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            userPrefs.userName = nameInput.trim()
                            userName = nameInput.trim()
                        },
                        enabled = nameInput.trim().isNotEmpty() && nameInput.trim() != userName
                    ) { Text(stringResource(R.string.save)) }
                }
            )

            // Безопасность
            Text(
                stringResource(R.string.security),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Spacing.sm)
            )

            if (hasPin) {
                SettingsCard(
                    title = stringResource(R.string.pin_change),
                    subtitle = stringResource(R.string.pin_change_subtitle),
                    icon = Icons.Filled.Lock,
                    onClick = { showPinScreen = PinMode.CHANGE }
                )
                SettingsCard(
                    title = stringResource(R.string.pin_remove),
                    subtitle = stringResource(R.string.pin_remove_subtitle),
                    icon = Icons.Filled.LockOpen,
                    onClick = { showRemovePinDialog = true }
                )
            } else {
                SettingsCard(
                    title = stringResource(R.string.pin_create),
                    subtitle = stringResource(R.string.pin_create_subtitle),
                    icon = Icons.Filled.Lock,
                    onClick = { showPinScreen = PinMode.CREATE }
                )
            }

            // Данные
            Text(
                stringResource(R.string.settings_general),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Spacing.sm)
            )
            SettingsCard(
                title = stringResource(R.string.export_json),
                subtitle = stringResource(R.string.export_data),
                icon = Icons.Filled.Upload,
                enabled = !state.isExporting,
                onClick = { exportLauncher.launch("taskmanager_backup_${System.currentTimeMillis()}.json") }
            )
            SettingsCard(
                title = stringResource(R.string.import_json),
                subtitle = stringResource(R.string.import_data),
                icon = Icons.Filled.Download,
                enabled = !state.isImporting,
                onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) }
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(Radius.md)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Icon(icon, contentDescription = null, tint = AppTheme.colors.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = AppTheme.colors.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
            }
        }
    }
}
