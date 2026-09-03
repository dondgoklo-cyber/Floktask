package com.taskmanager.calendar

import java.time.Instant

/**
 * A calendar event read from the system CalendarProvider (issue 30:
 * two-way sync with Google/Apple Calendar). This is the read side.
 */
data class CalendarEvent(
    val id: Long,
    val title: String,
    val description: String?,
    val start: Instant,
    val end: Instant,
    val calendarId: Long,
    val location: String? = null,
    val allDay: Boolean = false
)
