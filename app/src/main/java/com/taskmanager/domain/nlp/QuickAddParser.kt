package com.taskmanager.domain.nlp

import com.taskmanager.domain.model.Priority
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Result of natural-language parsing of a quick-add task string.
 * Only the fields actually detected by the parser are non-null.
 */
data class ParsedTask(
    val title: String,
    val deadline: Instant? = null,
    val reminderDate: Instant? = null,
    val tags: List<String> = emptyList(),
    val projectName: String? = null,
    val priority: Priority? = null
) {
    companion object
}

/**
 * Parses a free-form quick-add string into a structured [ParsedTask].
 *
 * Supported syntax (Russian + English):
 *  - Dates: "сегодня/today", "завтра/tomorrow", "послезавтра/day after tomorrow",
 *           "через N дн(ей/я)/in N days", "в понедельник/on monday"
 *  - Time: "в 15:00/at 15:00", "19:30", "в 9/at 9"
 *  - Tags: "#work", "#home"
 *  - Project: "@project"
 *  - Priority: "!1"/"!high", "!2"/"!medium", "!3"/"!low"
 *
 * Anything not matched is left in [ParsedTask.title].
 */
class QuickAddParser(
    private val zone: ZoneId = ZoneId.systemDefault()
) {

    fun parse(input: String): ParsedTask {
        if (input.isBlank()) return ParsedTask(title = "")

        var remaining = input.trim()
        val tags = mutableListOf<String>()
        var projectName: String? = null
        var priority: Priority? = null
        var date: LocalDate? = null
        var time: LocalTime? = null

        // Extract tags #tag
        TAG_REGEX.findAll(remaining).forEach { m ->
            tags.add(m.groupValues[1])
        }
        remaining = remaining.replace(TAG_REGEX, " ").trim()

        // Extract project @project
        PROJECT_REGEX.find(remaining)?.let { m ->
            projectName = m.groupValues[1]
            remaining = remaining.replace(m.value, " ").trim()
        }

        // Extract priority !1 / !high
        PRIORITY_REGEX.find(remaining)?.let { m ->
            priority = parsePriority(m.groupValues[1])
            if (priority != null) {
                remaining = remaining.replace(m.value, " ").trim()
            }
        }

        // Extract relative date "через N дн" / "in N days"
        RELATIVE_DAYS_REGEX.find(remaining)?.let { m ->
            val n = m.groupValues[1].toIntOrNull()
            if (n != null) {
                date = LocalDate.now(zone).plusDays(n.toLong())
                remaining = remaining.replace(m.value, " ")
            }
        }
        remaining = remaining.trim()

        // Extract named day "завтра" / "tomorrow" etc.
        if (date == null) {
            NAMED_DAY_REGEX.find(remaining)?.let { m ->
                date = namedDayToDate(m.value.lowercase())
                if (date != null) {
                    remaining = remaining.replace(m.value, " ")
                }
            }
            remaining = remaining.trim()
        }

        // Extract weekday "в понедельник" / "on monday"
        if (date == null) {
            WEEKDAY_REGEX.find(remaining)?.let { m ->
                date = weekdayToDate(m.groupValues[1].lowercase())
                if (date != null) {
                    remaining = remaining.replace(m.value, " ")
                }
            }
            remaining = remaining.trim()
        }

        // Extract time "в 15:00" / "at 15:00" / "15:00" / "в 9"
        TIME_REGEX.find(remaining)?.let { m ->
            val hour = m.groupValues[1].toIntOrNull()
            val minute = m.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            if (hour != null && hour in 0..23 && minute in 0..59) {
                time = LocalTime.of(hour, minute)
                remaining = remaining.replace(m.value, " ")
            }
        }
        remaining = remaining
            .replace(LEAD_TRAIL_TIME_WORD, " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val deadline = combineDateTime(date, time)

        return ParsedTask(
            title = remaining.trim().trim(',', '.').trim(),
            deadline = deadline,
            tags = tags.distinct(),
            projectName = projectName,
            priority = priority
        )
    }

    private fun combineDateTime(date: LocalDate?, time: LocalTime?): Instant? {
        if (date == null && time == null) return null
        val resolvedDate = date ?: LocalDate.now(zone)
        val resolvedTime = time ?: LocalTime.MIDNIGHT
        return LocalDateTime.of(resolvedDate, resolvedTime).atZone(zone).toInstant()
    }

    private fun parsePriority(token: String): Priority? = when (token.lowercase()) {
        "1", "high", "высокий", "срочно" -> Priority.HIGH
        "2", "medium", "средний" -> Priority.MEDIUM
        "3", "low", "низкий" -> Priority.LOW
        else -> null
    }

    private fun namedDayToDate(token: String): LocalDate? {
        val today = LocalDate.now(zone)
        return when (token) {
            "сегодня", "today" -> today
            "завтра", "tomorrow" -> today.plusDays(1)
            "послезавтра", "day after tomorrow", "послезавтра" -> today.plusDays(2)
            "вчера", "yesterday" -> today.minusDays(1)
            else -> null
        }
    }

    private fun weekdayToDate(token: String): LocalDate? {
        val target = WEEKDAYS[token] ?: return null
        val today = LocalDate.now(zone)
        val diff = (target.value - today.dayOfWeek.value + 7) % 7
        val offset = if (diff == 0L) 7 else diff // next occurrence, not today
        return today.plusDays(offset)
    }

    companion object {
        // #word (letters, digits, dash, underscore)
        private val TAG_REGEX = "#([\\p{L}\\p{N}_-]+)".toRegex(RegexOption.IGNORE_CASE)
        // @word
        private val PROJECT_REGEX = "@([\\p{L}\\p{N}_-]+)".toRegex(RegexOption.IGNORE_CASE)
        // !1 / !high / !2 / !medium
        private val PRIORITY_REGEX = "!(\\p{L}+|\\d+)".toRegex(RegexOption.IGNORE_CASE)
        // "через N дней" / "in N days"
        private val RELATIVE_DAYS_REGEX =
            "(?i)(?:через|in)\\s+(\\d+)\\s+(?:дн(?:ей|я|о)|day(?:s)?)".toRegex()
        // named day
        private val NAMED_DAY_REGEX =
            "(?i)\\b(сегодня|завтра|послезавтра|вчера|today|tomorrow|day after tomorrow|yesterday)\\b"
                .toRegex()
        // weekday "в понедельник" / "on monday"
        private val WEEKDAY_REGEX =
            "(?i)(?:в|on)\\s+(понедельник|вторник|сред[ау]|четверг|пятниц[ау]|суббот[ау]|воскресенье|monday|tuesday|wednesday|thursday|friday|saturday|sunday)"
                .toRegex()
        // time "в 15:00" / "at 15:00" / "15:00" / "в 9" / "at 9"
        private val TIME_REGEX =
            "(?i)(?:(?:в|at)\\s+)?(\\d{1,2})(?::(\\d{2}))?(?=[\\s,]|$)".toRegex()
        private val LEAD_TRAIL_TIME_WORD = "(?i)\\b(?:в|at)\\b".toRegex()

        private val WEEKDAYS = mapOf(
            "понедельник" to java.time.DayOfWeek.MONDAY,
            "monday" to java.time.DayOfWeek.MONDAY,
            "вторник" to java.time.DayOfWeek.TUESDAY,
            "tuesday" to java.time.DayOfWeek.TUESDAY,
            "среда" to java.time.DayOfWeek.WEDNESDAY,
            "среду" to java.time.DayOfWeek.WEDNESDAY,
            "wednesday" to java.time.DayOfWeek.WEDNESDAY,
            "четверг" to java.time.DayOfWeek.THURSDAY,
            "thursday" to java.time.DayOfWeek.THURSDAY,
            "пятница" to java.time.DayOfWeek.FRIDAY,
            "пятницу" to java.time.DayOfWeek.FRIDAY,
            "friday" to java.time.DayOfWeek.FRIDAY,
            "суббота" to java.time.DayOfWeek.SATURDAY,
            "субботу" to java.time.DayOfWeek.SATURDAY,
            "saturday" to java.time.DayOfWeek.SATURDAY,
            "воскресенье" to java.time.DayOfWeek.SUNDAY,
            "sunday" to java.time.DayOfWeek.SUNDAY
        )
    }
}
