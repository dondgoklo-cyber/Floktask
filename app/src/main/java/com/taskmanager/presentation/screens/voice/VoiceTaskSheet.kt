package com.taskmanager.presentation.screens.voice

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.R
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.util.HapticAction
import androidx.hilt.navigation.compose.inject
import com.taskmanager.util.HapticManager
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.PrimaryButton
import com.taskmanager.presentation.components.SecondaryButton
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import com.taskmanager.voice.RussianVoiceParser
import com.taskmanager.voice.TaskDraft
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceTaskSheet(
    onDismiss: () -> Unit,
    onCreate: (String, LocalDate?, LocalTime?, Priority, RecurrenceRule?) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hapticManager: HapticManager = inject()
    val haptic = remember(hapticManager) { { type: HapticType -> hapticManager.perform(type) } }

    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf<TaskDraft?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf("") }
    var isInitializing by remember { mutableStateOf(true) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    // Инициализация SpeechRecognizer с проверкой доступности
    LaunchedEffect(Unit) {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            } else {
                error = "Распознавание речи недоступно на этом устройстве"
            }
        } catch (e: Exception) {
            error = "Ошибка инициализации распознавания речи"
        }
        isInitializing = false
    }

    // Очистка ресурсов
    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                // Игнорируем ошибки при очистке
            }
        }
    }

    val recognitionIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    // Listener с полной обработкой ошибок
    val listener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                isListening = true
                error = null
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(errorCode: Int) {
                isListening = false
                error = when (errorCode) {
                    SpeechRecognizer.ERROR_AUDIO -> "Ошибка аудио: микрофон занят или отключен"
                    SpeechRecognizer.ERROR_CLIENT -> "Ошибка клиента: проверьте подключение к интернету"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения на использование микрофона"
                    SpeechRecognizer.ERROR_NETWORK -> "Ошибка сети: проверьте подключение"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Таймаут сети"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Не удалось распознать речь. Попробуйте еще раз"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Распознаватель занят. Подождите"
                    SpeechRecognizer.ERROR_SERVER -> "Ошибка сервера"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Время ожидания речи истекло"
                    else -> "Ошибка распознавания: код $errorCode"
                }
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    recognizedText = text
                    try {
                        draft = RussianVoiceParser.parse(text)
                        editedTitle = draft?.title ?: ""
                        haptic(HapticType.SUCCESS)
                    } catch (e: Exception) {
                        error = "Ошибка обработки текста"
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { recognizedText = it }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    // Установка listener
    LaunchedEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(listener)
    }

    fun startListening() {
        if (isListening) return
        
        if (speechRecognizer == null) {
            error = "Распознавание речи недоступно"
            return
        }
        
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            error = "Распознавание речи недоступно на этом устройстве"
            return
        }

        error = null
        recognizedText = ""
        draft = null
        isEditing = false
        isListening = true
        haptic(HapticType.LIGHT)
        
        try {
            speechRecognizer?.startListening(recognitionIntent)
        } catch (e: Exception) {
            isListening = false
            error = "Ошибка запуска распознавания: ${e.message}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isListening = false
        } catch (e: Exception) {
            // Игнорируем
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            speechRecognizer?.destroy()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = AppTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                stringResource(R.string.voice_task),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Mic button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) AppTheme.colors.danger.copy(alpha = 0.15f)
                                else AppTheme.colors.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = if (isListening) "Остановить запись" else "Начать запись",
                            tint = if (isListening) AppTheme.colors.danger else AppTheme.colors.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        if (isListening) "🎤 ${stringResource(R.string.voice_listening)}"
                        else stringResource(R.string.voice_tap_to_speak),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                SecondaryButton(
                    text = if (isListening) "Остановить" else "Начать",
                    onClick = {
                        if (isListening) stopListening() else startListening()
                    },
                    modifier = Modifier.weight(1f)
                )
                
                if (draft != null) {
                    SecondaryButton(
                        text = "Отмена",
                        onClick = {
                            stopListening()
                            recognizedText = ""
                            draft = null
                            error = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Ошибка
            error?.let { msg ->
                Text(
                    msg,
                    color = AppTheme.colors.danger,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = Spacing.xs)
                )
            }

            // Результаты распознавания
            if (recognizedText.isNotBlank() && draft == null && error == null) {
                Text(
                    "Распознано: $recognizedText",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

            // Draft preview
            draft?.let { d ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    if (isEditing) {
                        AppTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text(stringResource(R.string.title)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = d.title.ifBlank { recognizedText },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Date
                    d.date?.let { date ->
                        DraftInfoRow("Дата", date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                    }
                    // Time
                    d.time?.let { time ->
                        val timeLabel = if (d.isAmbiguousTime) "Время (примерно)" else "Время"
                        DraftInfoRow(timeLabel, time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    }
                    // Priority
                    if (d.priority != Priority.NONE) {
                        val priorityLabel = when (d.priority) {
                            Priority.HIGH -> "Высокий"
                            Priority.MEDIUM -> "Средний"
                            Priority.LOW -> "Низкий"
                            Priority.NONE -> ""
                        }
                        DraftInfoRow("Приоритет", priorityLabel)
                    }
                    // Tags
                    if (d.tags.isNotEmpty()) {
                        DraftInfoRow("Теги", d.tags.joinToString(", "))
                    }
                    // Recurrence
                    d.recurrenceRule?.let { rule ->
                        val ruleLabel = when (rule) {
                            RecurrenceRule.DAILY -> "Ежедневно"
                            RecurrenceRule.WEEKLY -> "Еженедельно"
                            RecurrenceRule.MONTHLY -> "Ежемесячно"
                            RecurrenceRule.YEARLY -> "Ежегодно"
                            RecurrenceRule.CUSTOM -> "Другой"
                        }
                        DraftInfoRow("Повтор", ruleLabel)
                    }

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        TextButton(onClick = { isEditing = !isEditing }) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(Spacing.xs))
                            Text(stringResource(R.string.voice_edit))
                        }
                        PrimaryButton(
                            text = stringResource(R.string.create),
                            onClick = {
                                val finalTitle = if (isEditing) editedTitle.trim() else d.title.ifBlank { recognizedText }.trim()
                                if (finalTitle.isNotBlank()) {
                                    onCreate(finalTitle, d.date, d.time, d.priority, d.recurrenceRule)
                                    stopListening()
                                } else {
                                    error = "Введите название задачи"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

@Composable
private fun DraftInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
