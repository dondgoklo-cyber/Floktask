package com.taskmanager.data.repository

import com.taskmanager.domain.model.Note
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экспорт заметок в Markdown формат.
 * Файлосфия: полезность записей - замена переносима.
 *
 * Формат .md файла:
 * # Title
 *
 * Content markdown...
 */
class NoteExportManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())

    /**
     * Экспортирует одну заметку в Markdown.
     * Файл: # Title\n\nContent
     */
    fun exportToMarkdown(note: Note, writer: Writer) {
        val title = note.title.ifBlank { "Без названия" }
        writer.write("# $title\n\n")
        writer.write(note.contentMarkdown)
        writer.flush()
    }

    /**
     * Экспортирует несколько заметок в один Markdown файл.
     * Каждая заметка отделена разделителем.
     */
    fun exportAllToMarkdown(notes: List<Note>, writer: Writer) {
        notes.forEachIndexed { index, note ->
            if (index > 0) writer.write("\n\n---\n\n")
            val title = note.title.ifBlank { "Без названия" }
            writer.write("# $title\n\n")
            writer.write(note.contentMarkdown)
        }
        writer.flush()
    }

    /**
     * Генерирует имя файла для одной заметки.
     */
    fun importFromMarkdown(markdown: String): Note {
        val lines = markdown.trim().lines()
        var title = ""
        var contentStart = 0

        if (lines.isNotEmpty() && lines[0].startsWith("# ")) {
            title = lines[0].removePrefix("# ").trim()
            contentStart = 1
            if (lines.size > 1 && lines[1].isBlank()) contentStart = 2
        }

        val content = lines.drop(contentStart).joinToString("\n")
        return Note(title = title, contentMarkdown = content)
    }

    fun generateNoteFileName(title: String): String {
        val safeTitle = title.ifBlank { "note" }
            .replace(Regex("[^a-zA-Zа-яА-Я0-9\\s-]"), "")
            .replace(" ", "_")
            .take(50)
        return "wolftask_note_${safeTitle}.md"
    }

    /**
     * Генерирует имя файла для всех заметок.
     */
    fun generateAllNotesFileName(): String {
        return "wolftask_notes_${dateFormat.format(Date())}.md"
    }
}
