package com.taskmanager.domain.nlp

import com.taskmanager.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class QuickAddParserTest {

    private val zone = ZoneId.systemDefault()
    private val parser = QuickAddParser(zone)
    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

    @Test
    fun `plain text becomes title only`() {
        val r = parser.parse("Купить молоко")
        assertEquals("Купить молоко", r.title)
        assertNull(r.deadline)
        assertTrue(r.tags.isEmpty())
        assertNull(r.projectName)
        assertNull(r.priority)
    }

    @Test
    fun `parses tomorrow with time`() {
        val r = parser.parse("Звонок клиенту завтра в 15:00")
        assertEquals("Звонок клиенту", r.title)
        val expected = LocalDate.now(zone).plusDays(1)
        assertEquals(dateFmt.format(expected), dateFmt.format(r.deadline!!.atZone(zone).toLocalDate()))
        assertEquals(15, r.deadline!!.atZone(zone).hour)
        assertEquals(0, r.deadline!!.atZone(zone).minute)
    }

    @Test
    fun `parses in N days`() {
        val r = parser.parse("Дедлайн через 3 дня")
        assertEquals("Дедлайн", r.title)
        val expected = LocalDate.now(zone).plusDays(3)
        assertEquals(dateFmt.format(expected), dateFmt.format(r.deadline!!.atZone(zone).toLocalDate()))
    }

    @Test
    fun `parses english tomorrow`() {
        val r = parser.parse("Call bob tomorrow at 9:30")
        assertEquals("Call bob", r.title)
        val expected = LocalDate.now(zone).plusDays(1)
        assertEquals(dateFmt.format(expected), dateFmt.format(r.deadline!!.atZone(zone).toLocalDate()))
        assertEquals(9, r.deadline!!.atZone(zone).hour)
        assertEquals(30, r.deadline!!.atZone(zone).minute)
    }

    @Test
    fun `parses tags and project`() {
        val r = parser.parse("Ревью кода #work @backend")
        assertEquals("Ревью кода", r.title)
        assertEquals(listOf("work"), r.tags)
        assertEquals("backend", r.projectName)
    }

    @Test
    fun `parses multiple tags`() {
        val r = parser.parse("Тесты #qa #important")
        assertEquals(listOf("qa", "important"), r.tags)
    }

    @Test
    fun `parses numeric priority`() {
        val r = parser.parse("Срочно починить баг !1")
        assertEquals("Срочно починить баг", r.title)
        assertEquals(Priority.HIGH, r.priority)
    }

    @Test
    fun `parses textual priority`() {
        val r = parser.parse("Небольшая задача !low")
        assertEquals("Небольшая задача", r.title)
        assertEquals(Priority.LOW, r.priority)
    }

    @Test
    fun `parses weekday`() {
        val r = parser.parse("Встреча в пятницу")
        assertEquals("Встреча", r.title)
        // deadline should be the next Friday
        val today = LocalDate.now(zone)
        val expected = today.plusDays(((5 - today.dayOfWeek.value + 7) % 7).toLong().let { if (it == 0L) 7 else it })
        assertEquals(dateFmt.format(expected), dateFmt.format(r.deadline!!.atZone(zone).toLocalDate()))
    }

    @Test
    fun `blank input returns empty title`() {
        val r = parser.parse("   ")
        assertEquals("", r.title)
    }

    @Test
    fun `time only uses today`() {
        val r = parser.parse("Подъём в 7:00")
        assertEquals("Подъём", r.title)
        val expected = LocalDate.now(zone)
        assertEquals(dateFmt.format(expected), dateFmt.format(r.deadline!!.atZone(zone).toLocalDate()))
        assertEquals(7, r.deadline!!.atZone(zone).hour)
    }
}
