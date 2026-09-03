package com.taskmanager.calendar

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads calendar events from the system [CalendarContract] ContentProvider
 * (issue 30: events -> tasks). Requires READ_CALENDAR permission.
 */
@Singleton
class CalendarRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hasPermission(): Boolean =
        context.checkCallingOrSelfPermission(android.Manifest.permission.READ_CALENDAR) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Events overlapping [start, end], ordered by start ascending.
     * Returns empty if permission is missing.
     */
    fun getEvents(start: Instant, end: Instant): List<CalendarEvent> {
        if (!hasPermission()) return emptyList()

        val events = mutableListOf<CalendarEvent>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.ALL_DAY
        )
        val selection = "(${CalendarContract.Events.DTSTART} <= ? AND " +
            "${CalendarContract.Events.DTEND} >= ?)"
        val args = arrayOf(end.toEpochMilli().toString(), start.toEpochMilli().toString())

        val cursor = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            args,
            "${CalendarContract.Events.DTSTART} ASC"
        ) ?: return emptyList()

        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow(CalendarContract.Events._ID)
            val titleIdx = c.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
            val descIdx = c.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
            val startIdx = c.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
            val endIdx = c.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
            val calIdx = c.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID)
            val locIdx = c.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION)
            val allDayIdx = c.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)

            while (c.moveToNext()) {
                val dtEnd = c.getLong(endIdx)
                events.add(
                    CalendarEvent(
                        id = c.getLong(idIdx),
                        title = c.getString(titleIdx).orEmpty(),
                        description = c.getString(descIdx),
                        start = Instant.ofEpochMilli(c.getLong(startIdx)),
                        end = if (dtEnd > 0) Instant.ofEpochMilli(dtEnd)
                            else Instant.ofEpochMilli(c.getLong(startIdx)),
                        calendarId = c.getLong(calIdx),
                        location = c.getString(locIdx),
                        allDay = c.getInt(allDayIdx) == 1
                    )
                )
            }
        }
        return events
    }

    @Suppress("unused")
    private fun eventUri(id: Long) = ContentUris.withAppendedId(
        CalendarContract.Events.CONTENT_URI, id
    )
}
