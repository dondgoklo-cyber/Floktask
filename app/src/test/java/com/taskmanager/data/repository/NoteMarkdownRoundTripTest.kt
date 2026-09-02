package com.taskmanager.data.repository

import com.taskmanager.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringWriter

class NoteMarkdownRoundTripTest {

    private val manager = NoteExportManager()

    @Test
    fun `round trip preserves title and content`() {
        val note = Note(title = "Заметка", contentMarkdown = "Тело заметки\nвторой абзац")
        val writer = StringWriter()
        manager.exportToMarkdown(note, writer)
        val imported = manager.importFromMarkdown(writer.toString())
        assertEquals("Заметка", imported.title)
        assertEquals("Тело заметки\nвторой абзац", imported.contentMarkdown)
    }

    @Test
    fun `empty note exports with default title`() {
        val note = Note(title = "", contentMarkdown = "")
        val writer = StringWriter()
        manager.exportToMarkdown(note, writer)
        val imported = manager.importFromMarkdown(writer.toString())
        assertEquals("Без названия", imported.title)
    }

    @Test
    fun `multiple paragraphs with blank lines preserved`() {
        val content = "Первый\n\n\nВторой абзац\n\nТретий"
        val note = Note(title = "T", contentMarkdown = content)
        val writer = StringWriter()
        manager.exportToMarkdown(note, writer)
        val imported = manager.importFromMarkdown(writer.toString())
        assertEquals(content, imported.contentMarkdown)
    }

    @Test
    fun `unicode and special characters preserved`() {
        val content = "Ünïcödé — € symbol & <tag> \"quotes\""
        val note = Note(title = "T", contentMarkdown = content)
        val writer = StringWriter()
        manager.exportToMarkdown(note, writer)
        val imported = manager.importFromMarkdown(writer.toString())
        assertEquals(content, imported.contentMarkdown)
    }

    @Test
    fun `title with leading hash inside content is not mistaken for heading`() {
        // Content starting with "# " after a blank line should remain content.
        val content = "Текст\n\n# не заголовок"
        val note = Note(title = "Real title", contentMarkdown = content)
        val writer = StringWriter()
        manager.exportToMarkdown(note, writer)
        val imported = manager.importFromMarkdown(writer.toString())
        assertEquals("Real title", imported.title)
        assertTrue(imported.contentMarkdown.contains("# не заголовок"))
    }

    @Test
    fun `exportAll preserves each note content with separators`() {
        val notes = listOf(
            Note(title = "A", contentMarkdown = "aaa"),
            Note(title = "B", contentMarkdown = "bbb")
        )
        val writer = StringWriter()
        manager.exportAllToMarkdown(notes, writer)
        val out = writer.toString()
        assertTrue(out.contains("# A"))
        assertTrue(out.contains("# B"))
        assertTrue(out.contains("---"))
    }
}
