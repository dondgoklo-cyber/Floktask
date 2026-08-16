package com.taskmanager.presentation.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.IosShare
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.taskmanager.data.repository.NoteExportManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.haptic.HapticType
import com.taskmanager.haptic.rememberHaptic
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import androidx.compose.foundation.shape.RoundedCornerShape

private enum class NoteEditMode { EDIT, PREVIEW }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mode by remember { mutableStateOf(NoteEditMode.EDIT) }
    val haptic = rememberHaptic()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isNew) "Новая заметка" else "Заметка")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic(HapticType.LIGHT)
                        viewModel.saveNow()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    val context = LocalContext.current
                    val exportManager = remember { NoteExportManager() }
                    val mdLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.CreateDocument("text/markdown")
                    ) { uri ->
                        uri?.let {
                            context.contentResolver.openOutputStream(it)?.use { stream ->
                                val writer = stream.bufferedWriter()
                                exportManager.exportToMarkdown(
                                    com.taskmanager.domain.model.Note(
                                        title = state.title,
                                        contentMarkdown = state.content
                                    ),
                                    writer
                                )
                                Toast.makeText(context, "Markdown экспортирован", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    IconButton(onClick = {
                        viewModel.saveNow()
                        mdLauncher.launch(exportManager.generateNoteFileName(state.title))
                    }) {
                        Icon(Icons.Filled.IosShare, contentDescription = "Экспорт Markdown")
                    }
                    IconButton(onClick = {
                        haptic(HapticType.SELECTION)
                        mode = if (mode == NoteEditMode.EDIT) NoteEditMode.PREVIEW else NoteEditMode.EDIT
                    }) {
                        Icon(
                            if (mode == NoteEditMode.EDIT) Icons.Filled.Visibility else Icons.Filled.Edit,
                            contentDescription = if (mode == NoteEditMode.EDIT) "Просмотр" else "Редактировать"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.lg)
            ) {
                // Edit / Preview toggle
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    FilterChip(
                        selected = mode == NoteEditMode.EDIT,
                        onClick = {
                            haptic(HapticType.SELECTION)
                            mode = NoteEditMode.EDIT
                        },
                        label = { Text("Текст") }
                    )
                    FilterChip(
                        selected = mode == NoteEditMode.PREVIEW,
                        onClick = {
                            haptic(HapticType.SELECTION)
                            mode = NoteEditMode.PREVIEW
                        },
                        label = { Text("Просмотр") }
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.isSaved) {
                        Text(
                            "✓ Сохранено",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.success
                        )
                    }
                }

                when (mode) {
                    NoteEditMode.EDIT -> {
                        NoteEditor(
                            title = state.title,
                            content = state.content,
                            onTitleChange = viewModel::onTitleChange,
                            onContentChange = viewModel::onContentChange
                        )
                    }
                    NoteEditMode.PREVIEW -> {
                        MarkdownPreview(
                            title = state.title,
                            content = state.content
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteEditor(
    title: String,
    content: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit
) {
    val haptic = rememberHaptic()

    Column(modifier = Modifier.fillMaxSize()) {
        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Заголовок") },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(Radius.md),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Spacing.sm))

        // Markdown toolbar
        MarkdownToolbar(
            onInsert = { prefix, suffix ->
                haptic(HapticType.LIGHT)
                onContentChange(content + prefix + (suffix ?: ""))
            }
        )

        Spacer(Modifier.height(Spacing.xs))

        // Content field
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            placeholder = { Text("Начните писать... (Markdown)") },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(Radius.md),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}

@Composable
private fun MarkdownToolbar(onInsert: (String, String?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarButton(Icons.Filled.Title, "Заголовок") { onInsert("# ", null) }
        ToolbarButton(Icons.Filled.FormatBold, "Жирный") { onInsert("**", "**") }
        ToolbarButton(Icons.Filled.FormatItalic, "Курсив") { onInsert("*", "*") }
        ToolbarButton(Icons.Filled.FormatListBulleted, "Список") { onInsert("- ", null) }
        ToolbarButton(Icons.Filled.FormatListNumbered, "Нумерация") { onInsert("1. ", null) }
        ToolbarButton(Icons.Filled.Check, "Чекбокс") { onInsert("- [ ] ", null) }
        ToolbarButton(Icons.Filled.Code, "Код") { onInsert("```\n", "\n```") }
    }
}

@Composable
private fun ToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(icon, contentDescription = contentDescription, tint = AppTheme.colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun MarkdownPreview(title: String, content: String) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = Spacing.md)
    ) {
        if (title.isNotBlank()) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurface
            )
            Spacer(Modifier.height(Spacing.md))
        }
        // Simple markdown rendering — parse line by line
        content.lines().forEach { line ->
            MarkdownLine(line)
        }
    }
}

@Composable
private fun MarkdownLine(line: String) {
    when {
        line.startsWith("### ") -> Text(
            line.removePrefix("### "),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = Spacing.sm)
        )
        line.startsWith("## ") -> Text(
            line.removePrefix("## "),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Spacing.md)
        )
        line.startsWith("# ") -> Text(
            line.removePrefix("# "),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Spacing.md)
        )
        line.startsWith("- [ ] ") -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(start = Spacing.sm)
        ) {
            Text("☐", color = AppTheme.colors.outline)
            Text(line.removePrefix("- [ ] "), style = MaterialTheme.typography.bodyMedium)
        }
        line.startsWith("- [x] ") -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(start = Spacing.sm)
        ) {
            Text("☑", color = AppTheme.colors.success)
            Text(
                line.removePrefix("- [x] "),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
        line.startsWith("- ") || line.startsWith("* ") -> Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(start = Spacing.sm)
        ) {
            Text("•", color = AppTheme.colors.primary)
            Text(line.removePrefix("- ").removePrefix("* "), style = MaterialTheme.typography.bodyMedium)
        }
        line.matches(Regex("^\\d+\\. .+")) -> {
            val num = line.substringBefore(". ")
            val text = line.substringAfter(". ")
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.padding(start = Spacing.sm)
            ) {
                Text("$num.", color = AppTheme.colors.primary, style = MaterialTheme.typography.bodyMedium)
                Text(text, style = MaterialTheme.typography.bodyMedium)
            }
        }
        line.startsWith("```") -> {
            // Code block — render as monospace
            Text(
                line.removePrefix("```"),
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .padding(vertical = Spacing.xs)
                    .fillMaxWidth()
            )
        }
        line.isBlank() -> Spacer(Modifier.height(Spacing.xs))
        else -> Text(
            renderInlineMarkdown(line),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

/** Упрощённый inline Markdown: **bold**, *italic*, `code`. */
private fun renderInlineMarkdown(text: String): String {
    return text
        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1") // bold — без форматирования в Text
        .replace(Regex("\\*(.+?)\\*"), "$1") // italic
        .replace(Regex("`(.+?)`"), "$1") // inline code
}
