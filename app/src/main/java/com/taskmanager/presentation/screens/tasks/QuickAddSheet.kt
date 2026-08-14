package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Быстрое создание задачи. Поддерживает парсинг ключевых слов:
 * "завтра", "сегодня", время в формате ЧЧ:ММ, "на N (часов/мин)".
 *
 * Пример: "Позвонить клиенту завтра в 15:00 на час"
 * → title="Позвонить клиенту", date=завтра, time=15:00, duration=60
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onCreated: (Long) -> Unit,
    viewModel: QuickAddViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = Radius.xl,
            topEnd = Radius.xl
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                stringResource(R.string.add_task),
                style = MaterialTheme.typography.titleMedium
            )
            AppTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = {
                    Text("Позвонить клиенту завтра в 15:00 на час...")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val parsed = QuickAddParser.parse(input)
                            viewModel.createTask(parsed) { id ->
                                onCreated(id)
                            }
                        },
                        enabled = input.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    }
                }
            )
            Text(
                "Подсказки: «завтра», «сегодня», «15:00», «на час», «на 30 мин»",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
