package com.taskmanager.presentation.screens.notes

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.taskmanager.R
import com.taskmanager.domain.model.Note
import com.taskmanager.util.HapticAction
import androidx.hilt.navigation.compose.inject
import com.taskmanager.util.HapticManager
import com.taskmanager.util.HapticAction
import com.taskmanager.presentation.components.AppTextField
import com.taskmanager.presentation.components.PrimaryButton
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Modal bottom sheet для импорта заметок из Markdown файлов.
 * Аналогично AddTransactionSheet - предоставляет preview и редактирование перед сохранением.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportNoteSheet(
    initialMarkdown: String,
    onDismiss: () -> Unit,
    onImport: (Note) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hapticManager: HapticManager = inject()
    val haptic = remember(hapticManager) { { type: HapticType -> hapticManager.perform(type) } }
    
    // Парсим начальный markdown
    val parsedNote = remember(initialMarkdown) { 
        val lines = initialMarkdown.trim().lines()
        var title = ""
        var contentStart = 0
        
        if (lines.isNotEmpty() && lines[0].startsWith("# ")) {
            title = lines[0].removePrefix("# ").trim()
            contentStart = 1
            if (lines.size > 1 && lines[1].isBlank()) contentStart = 2
        }
        
        val content = lines.drop(contentStart).joinToString("\n")
        Note(title = title, contentMarkdown = content)
    }
    
    var title by remember { mutableStateOf(parsedNote.title) }
    var content by remember { mutableStateOf(parsedNote.contentMarkdown) }
    var isEditing by remember { mutableStateOf(false) }
    
    val keyboard = LocalSoftwareKeyboardController.current
    
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(Spacing.md)
                    .size(32.dp, 4.dp)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(AppTheme.colors.surfaceVariant)
            )
        },
        containerColor = AppTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    haptic(HapticType.LIGHT)
                    onDismiss() 
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = AppTheme.colors.onSurface
                    )
                }
                
                Text(
                    "Импорт заметки",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onSurface
                )
                
                if (!isEditing) {
                    IconButton(onClick = { 
                        haptic(HapticType.LIGHT)
                        isEditing = true 
                    }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Редактировать",
                            tint = AppTheme.colors.primary
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(Spacing.lg))
            
            // Title field
            if (isEditing) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboard?.hide() }
                    )
                )
                Spacer(Modifier.height(Spacing.md))
            } else {
                Text(
                    title.ifBlank { "Без названия" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )
                Spacer(Modifier.height(Spacing.sm))
            }
            
            // Content preview/edit
            if (isEditing) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Содержимое (Markdown)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboard?.hide() }
                    ),
                    maxLines = 10
                )
            } else {
                // Preview mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                    shape = RoundedCornerShape(Radius.md),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "Предпросмотр",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        
                        Text(
                            content.ifBlank { "Пустое содержимое" },
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurface
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(Spacing.lg))
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Button(
                    onClick = { 
                        haptic(HapticType.LIGHT)
                        onDismiss() 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Radius.lg)
                ) {
                    Text("Отмена", color = AppTheme.colors.onSurface)
                }
                
                PrimaryButton(
                    text = "Импортировать",
                    onClick = { 
                        haptic(HapticType.SUCCESS)
                        onImport(Note(title = title, contentMarkdown = content))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() || content.isNotBlank(),
                    leadingIcon = Icons.Filled.Check
                )
            }
            
            Spacer(Modifier.height(Spacing.md))
        }
    }
}
