package com.taskmanager.presentation.screens.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.domain.model.LocationReminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationRemindersScreen(
    viewModel: LocationRemindersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val showCreate by viewModel.showCreateDialog.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Location reminders") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openCreateDialog) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) { padding ->
        when (val s = state) {
            LocationRemindersState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is LocationRemindersState.Success -> {
                if (s.reminders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) { Text("No location reminders yet") }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.reminders, key = { it.id ?: 0 }) { reminder ->
                            ReminderRow(
                                reminder = reminder,
                                onToggle = { viewModel.toggle(reminder.id ?: 0, it) },
                                onDelete = { viewModel.delete(reminder.id ?: 0) }
                            )
                        }
                    }
                }
            }

            is LocationRemindersState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.message) }
        }
    }

    if (showCreate) {
        CreateReminderDialog(
            onDismiss = viewModel::closeCreateDialog,
            onCreate = { taskId, label, lat, lng, radius ->
                viewModel.createReminder(taskId, label, lat, lng, radius)
            }
        )
    }
}

@Composable
private fun ReminderRow(
    reminder: LocationReminder,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(reminder.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    "%.5f, %.5f (%.0fm)".format(reminder.latitude, reminder.longitude, reminder.radiusMeters),
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Task #${reminder.taskId}", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = reminder.isActive, onCheckedChange = onToggle)
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
private fun CreateReminderDialog(
    onDismiss: () -> Unit,
    onCreate: (Long, String, Double, Double, Float) -> Unit
) {
    var taskId by remember { mutableStateOf("1") }
    var label by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("150") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New location reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = taskId, onValueChange = { taskId = it }, label = { Text("Task ID") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Label") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = radius, onValueChange = { radius = it }, label = { Text("Radius (m)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val tid = taskId.toLongOrNull() ?: 1L
                val lat = latitude.toDoubleOrNull() ?: 0.0
                val lng = longitude.toDoubleOrNull() ?: 0.0
                val rad = radius.toFloatOrNull() ?: 150f
                if (label.isNotBlank()) onCreate(tid, label.trim(), lat, lng, rad)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
