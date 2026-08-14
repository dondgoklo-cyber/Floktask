package com.taskmanager.presentation.screens.tasks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class QuickAddParserTest {

    @Test
    fun parse_today_keyword_extracts_date() {
        val result = QuickAddParser.parse("Позвонить сегодня")
        assertEquals("Позвонить", result.title)
        assertEquals(LocalDate.now(), result.deadlineDate)
    }

    @Test
    fun parse_tomorrow_keyword_extracts_date() {
        val result = QuickAddParser.parse("Позвонить завтра")
        assertEquals("Позвонить", result.title)
        assertEquals(LocalDate.now().plusDays(1), result.deadlineDate)
    }

    @Test
    fun parse_time_hh_mm_extracts_time() {
        val result = QuickAddParser.parse("Встреча в 15:30")
        assertEquals("Встреча", result.title)
        assertNotNull(result.startTime)
        assertEquals(15, result.startTime!!.hour)
        assertEquals(30, result.startTime!!.minute)
    }

    @Test
    fun parse_duration_hour_extracts_60_minutes() {
        val result = QuickAddParser.parse("Работа на час")
        assertEquals("Работа", result.title)
        assertEquals(60L, result.durationMinutes)
    }

    @Test
    fun parse_duration_minutes_extracts_value() {
        val result = QuickAddParser.parse("Задача на 30 мин")
        assertEquals("Задача", result.title)
        assertEquals(30L, result.durationMinutes)
    }

    @Test
    fun parse_full_example_extracts_all_fields() {
        val result = QuickAddParser.parse("Позвонить клиенту завтра в 15:00 на час")
        assertEquals("Позвонить клиенту", result.title)
        assertEquals(LocalDate.now().plusDays(1), result.deadlineDate)
        assertNotNull(result.startTime)
        assertEquals(15, result.startTime!!.hour)
        assertEquals(0, result.startTime!!.minute)
        assertEquals(60L, result.durationMinutes)
    }

    @Test
    fun parse_no_keywords_returns_title_only() {
        val result = QuickAddParser.parse("Просто задача")
        assertEquals("Просто задача", result.title)
        assertNull(result.deadlineDate)
        assertNull(result.startTime)
        assertNull(result.durationMinutes)
    }

    @Test
    fun parse_empty_string_returns_original_input_as_title() {
        val result = QuickAddParser.parse("завтра")
        // «завтра» — это всё, что есть; заголовок пуст → возвращаем исходный ввод
        assertEquals("завтра", result.title)
    }
}
