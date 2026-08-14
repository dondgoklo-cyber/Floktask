package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val form by viewModel.formState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (viewModel.isEditing) R.string.edit_task else R.string.add_task))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text(stringResource(R.string.title)) },
                isError = form.titleError,
                supportingText = {
                    if (form.titleError) Text("Введите название задачи")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(stringResource(R.string.priority), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { priority ->
                    val label = when (priority) {
                        Priority.HIGH -> "Высокий"
                        Priority.MEDIUM -> "Средний"
                        Priority.LOW -> "Низкий"
                        Priority.NONE -> "Нет"
                    }
                    FilterChip(
                        selected = form.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) },
                        label = { Text(label) }
                    )
                }
            }

            Text(stringResource(R.string.recurrence), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = form.recurrenceRule == null,
                    onClick = { viewModel.onRecurrenceChange(null) },
                    label = { Text(stringResource(R.string.none)) }
                )
                RecurrenceRule.entries.forEach { rule ->
                    val label = when (rule) {
                        RecurrenceRule.DAILY -> "Ежедневно"
                        RecurrenceRule.WEEKLY -> "Еженедельно"
                        RecurrenceRule.MONTHLY -> "Ежемесячно"
                        RecurrenceRule.YEARLY -> "Ежегодно"
                        RecurrenceRule.CUSTOM -> "Другое"
                    }
                    FilterChip(
                        selected = form.recurrenceRule == rule,
                        onClick = { viewModel.onRecurrenceChange(rule) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(onSaved = onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
