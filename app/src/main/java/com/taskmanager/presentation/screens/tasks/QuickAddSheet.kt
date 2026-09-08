package com.taskmanager.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Быстрое создание задачи с расширенным парсингом (Rich Quick Add).
 * Поддерживает: даты, время, длительность, проект (#), теги (@), приоритет (p1/p2/p3).
 *
 * Пример: "Купить молоко завтра p1 #Личное @покупки"
 * → title="Купить молоко", date=завтра, priority=HIGH, project="Личное", tags=["покупки"]
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    // Live parse for preview chips
    val parsed = remember(input) {
        if (input.isNotBlank()) QuickAddParser.parse(input) else null
    }

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
                    Text("Купить молоко завтра p1 #Личное @покупки...")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val result = QuickAddParser.parse(input)
                            viewModel.createTask(result) { id ->
                                onCreated(id)
                            }
                        },
                        enabled = input.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    }
                }
            )

            // Preview chips for parsed entities
            if (parsed != null) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    parsed.projectName?.let { proj ->
                        AssistChip(
                            onClick = {},
                            label = { Text(proj, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Filled.Folder, contentDescription = null, modifier = Modifier.padding(0.dp)) }
                        )
                    }
                    parsed.priority?.let { pri ->
                        val priText = when (pri) {
                            Priority.HIGH -> "p1 Высокий"
                            Priority.MEDIUM -> "p2 Средний"
                            Priority.LOW -> "p3 Низкий"
                            else -> ""
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text(priText, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.padding(0.dp)) }
                        )
                    }
                    parsed.deadlineDate?.let { d ->
                        AssistChip(
                            onClick = {},
                            label = { Text(formatDate(d), style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.padding(0.dp)) }
                        )
                    }
                    parsed.startTime?.let { t ->
                        AssistChip(
                            onClick = {},
                            label = { Text(t.toString(), style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.padding(0.dp)) }
                        )
                    }
                    parsed.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Filled.Label, contentDescription = null, modifier = Modifier.padding(0.dp)) }
                        )
                    }
                }
            }

            Text(
                "Подсказки: «завтра» «15:00» «на час» #Проект @тег p1(приоритет)",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

private fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> "сегодня"
        date == today.plusDays(1) -> "завтра"
        date == today.plusDays(2) -> "послезавтра"
        else -> date.format(DateTimeFormatter.ofPattern("dd.MM"))
    }
}
