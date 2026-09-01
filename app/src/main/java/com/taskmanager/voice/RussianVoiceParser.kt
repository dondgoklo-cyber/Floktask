package com.taskmanager.voice

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Детерминированный rule-based парсер для русского языка.
 * НЕ использует AI/LLM — только pattern matching.
 *
 * Понимает:
 * - Даты: сегодня, завтра, послезавтра, понедельник...воскресенье, 20 августа, 20.08
 * - Время: в 15:00, в 3 часа дня, 8 утра, 8 вечера, полдень, полночь
 * - Приоритет: срочно, важная, высокий приоритет
 * - Теги: тег работа
 * - Проект: в проекте Floktask
 * - Повторение: каждый месяц, каждого числа
 */
object RussianVoiceParser {

    /**
     * Парсит распознанный текст в [TaskDraft].
     * Извлекает дату, время, приоритет, теги, проект и очищает title.
     */
    fun parse(rawText: String): TaskDraft {
        val text = rawText.trim()
        if (text.isEmpty()) return TaskDraft(rawText = rawText)

        val dateResult = parseDate(text)
        val timeResult = parseTime(dateResult.cleanedText)
        val priorityResult = parsePriority(timeResult.cleanedText)
        val projectResult = parseProject(priorityResult.cleanedText)
        val tagResult = parseTags(projectResult.cleanedText)
        val recurrenceResult = parseRecurrence(tagResult.cleanedText)

        // Очищаем title от служебных слов
        val title = cleanTitle(recurrenceResult.cleanedText)

        return TaskDraft(
            title = title,
            date = dateResult.date,
            time = timeResult.time,
            projectName = projectResult.project,
            tags = tagResult.tags,
            priority = priorityResult.priority,
            recurrenceRule = recurrenceResult.recurrence,
            rawText = rawText,
            isAmbiguousTime = timeResult.isAmbiguous
        )
    }

    // === DATE PARSING ===

    private data class DateResult(val date: LocalDate?, val cleanedText: String)

    private fun parseDate(text: String): DateResult {
        val today = LocalDate.now()
        val lower = text.lowercase()

        // Относительные дни
        val relativeDates = mapOf(
            "сегодня" to today,
            "завтра" to today.plusDays(1),
            "послезавтра" to today.plusDays(2),
            "вчера" to today.minusDays(1)
        )
        for ((word, date) in relativeDates) {
            if (lower.contains(word)) {
                return DateResult(date, text.replace(word, "", ignoreCase = true).trim())
            }
        }

        // Дни недели
        val dayNames = mapOf(
            "понедельник" to DayOfWeek.MONDAY,
            "вторник" to DayOfWeek.TUESDAY,
            "среда" to DayOfWeek.WEDNESDAY,
            "среду" to DayOfWeek.WEDNESDAY,
            "четверг" to DayOfWeek.THURSDAY,
            "пятница" to DayOfWeek.FRIDAY,
            "пятницу" to DayOfWeek.FRIDAY,
            "суббота" to DayOfWeek.SATURDAY,
            "субботу" to DayOfWeek.SATURDAY,
            "воскресенье" to DayOfWeek.SUNDAY
        )
        for ((word, dayOfWeek) in dayNames) {
            if (lower.contains("следующ$word") || lower.contains("следующий $word") ||
                lower.contains("следующая $word") || lower.contains("следующую $word")) {
                val date = nextDayOfWeek(dayOfWeek, today, skipCurrent = true)
                return DateResult(date, removeWord(text, "следующ", word))
            }
            if (lower.contains(word)) {
                val date = nextDayOfWeek(dayOfWeek, today, skipCurrent = false)
                return DateResult(date, removeWord(text, word))
            }
        }

        // "через N дней/недель"
        val throughDaysRegex = Regex("""через\s+(\d+)\s*(дн[еяй]|дня|день)""")
        throughDaysRegex.find(lower)?.let { match ->
            val days = match.groupValues[1].toIntOrNull() ?: 1
            return DateResult(today.plusDays(days.toLong()), text.replace(match.value, "", ignoreCase = true).trim())
        }

        // "через час" / "через 2 часа"
        if (lower.contains("через час") || lower.contains("через 2 часа") || lower.contains("через два часа")) {
            return DateResult(today, text.replace("через час", "", ignoreCase = true).replace("через 2 часа", "", ignoreCase = true).replace("через два часа", "", ignoreCase = true).trim())
        }

        // "через N минут"
        val throughMinutesRegex = Regex("""через\s+(\d+)\s*(минут|минуты|минуту|мин)""")
        throughMinutesRegex.find(lower)?.let { match ->
            return DateResult(today, text.replace(match.value, "", ignoreCase = true).trim())
        }

        // "на следующей неделе"
        if (lower.contains("на следующей неделе") || lower.contains("следующая неделя")) {
            return DateResult(today.plusWeeks(1), text.replace("на следующей неделе", "", ignoreCase = true).replace("следующая неделя", "", ignoreCase = true).trim())
        }

        // "в выходные" — ближайшая суббота
        if (lower.contains("в выходные") || lower.contains("выходные")) {
            return DateResult(nextDayOfWeek(DayOfWeek.SATURDAY, today, false), removeWord(text, "в выходные", "выходные"))
        }
        if (lower.contains("через неделю")) {
            return DateResult(today.plusWeeks(1), text.replace("через неделю", "", ignoreCase = true).trim())
        }

        // "N августа" или "N августа" (число + месяц)
        val dateMonthRegex = Regex("""(\d{1,2})\s+(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)""")
        dateMonthRegex.find(lower)?.let { match ->
            val day = match.groupValues[1].toIntOrNull() ?: return@let
            val month = monthByName(match.groupValues[2]) ?: return@let
            val year = today.year
            var date = LocalDate.of(year, month, day)
            if (date.isBefore(today)) date = date.plusYears(1)
            return DateResult(date, text.replace(match.value, "", ignoreCase = true).trim())
        }

        // "DD.MM" или "DD.MM.YY"
        val shortDateRegex = Regex("""\b(\d{1,2})\.(\d{1,2})(?:\.(\d{2,4}))?\b""")
        shortDateRegex.find(lower)?.let { match ->
            val day = match.groupValues[1].toIntOrNull() ?: return@let
            val month = match.groupValues[2].toIntOrNull() ?: return@let
            val yearStr = match.groupValues[3]
            val year = if (yearStr.isNotEmpty()) {
                val y = yearStr.toInt()
                if (y < 100) 2000 + y else y
            } else today.year
            var date = LocalDate.of(year, month, day)
            if (yearStr.isEmpty() && date.isBefore(today)) date = date.plusYears(1)
            return DateResult(date, text.replace(match.value, "", ignoreCase = true).trim())
        }

        return DateResult(null, text)
    }

    private fun nextDayOfWeek(target: DayOfWeek, from: LocalDate, skipCurrent: Boolean): LocalDate {
        var date = from
        val offset = if (skipCurrent) 7 else 0
        while (date.dayOfWeek != target || (skipCurrent && date == from)) {
            date = date.plusDays(1)
        }
        if (skipCurrent && date.dayOfWeek == target && date == from) {
            date = date.plusWeeks(1)
        }
        return date
    }

    private fun monthByName(name: String): Int? = when (name) {
        "января" -> 1; "февраля" -> 2; "марта" -> 3; "апреля" -> 4
        "мая" -> 5; "июня" -> 6; "июля" -> 7; "августа" -> 8
        "сентября" -> 9; "октября" -> 10; "ноября" -> 11; "декабря" -> 12
        else -> null
    }

    // === TIME PARSING ===

    private data class TimeResult(val time: LocalTime?, val cleanedText: String, val isAmbiguous: Boolean)

    private fun parseTime(text: String): TimeResult {
        val lower = text.lowercase()

        // Полдень / полночь
        if (lower.contains("полдень")) {
            return TimeResult(LocalTime.NOON, removeWord(text, "полдень"), false)
        }
        if (lower.contains("полночь")) {
            return TimeResult(LocalTime.MIDNIGHT, removeWord(text, "полночь"), false)
        }

        // "в N:MM" или "в N"
        val timeRegex = Regex("""(?:в\s+)?(\d{1,2}):(\d{2})""")
        timeRegex.find(lower)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let
            val minute = match.groupValues[2].toIntOrNull() ?: return@let
            if (hour in 0..23 && minute in 0..59) {
                return TimeResult(LocalTime.of(hour, minute), text.replace(match.value, "", ignoreCase = true).trim(), false)
            }
        }

        // "в N утра" / "в N вечера" / "в N дня"
        val hourWithPeriodRegex = Regex("""(?:в\s+)?(\d{1,2})\s*(утра|вечера|дня|ночью|ночь)""")
        hourWithPeriodRegex.find(lower)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let
            val period = match.groupValues[2]
            val adjustedHour = when (period) {
                "вечера", "дня" -> if (hour < 12) hour + 12 else hour
                "утра" -> if (hour == 0) 0 else if (hour == 12) 0 else hour
                "ночью", "ночь" -> if (hour in 1..6) hour else if (hour == 12) 0 else hour + 12
                else -> hour
            }
            val safeHour = adjustedHour.coerceIn(0, 23)
            return TimeResult(LocalTime.of(safeHour, 0), text.replace(match.value, "", ignoreCase = true).trim(), false)
        }

        // "в N часов" / "в N часа"
        val hourWordRegex = Regex("""(?:в\s+)?(\d{1,2})\s*(час(?:ов|а|)?)""")
        hourWordRegex.find(lower)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let
            if (hour in 0..23) {
                return TimeResult(LocalTime.of(hour, 0), text.replace(match.value, "", ignoreCase = true).trim(), false)
            }
        }

        // Текстовые числа: "в три часа", "в три"
        val wordNumbers = mapOf(
            "один" to 1, "два" to 2, "три" to 3, "четыре" to 4, "пять" to 5,
            "шесть" to 6, "семь" to 7, "восемь" to 8, "девять" to 9, "десять" to 10,
            "одиннадцать" to 11, "двенадцать" to 12, "тринадцать" to 13,
            "четырнадцать" to 14, "пятнадцать" to 15, "шестнадцать" to 16,
            "семнадцать" to 17, "восемнадцать" to 18, "девятнадцать" to 19,
            "двадцать" to 20,
            "тридцать" to 30, "сорок" to 40, "пятьдесят" to 50,
            "шестьдесят" to 60, "сто" to 100
        )

        // "в три часа дня" / "в три часа вечера"
        for ((word, num) in wordNumbers) {
            val pattern = Regex("""(?:в\s+)?$word\s+час(?:ов|а|)?\s*(утра|вечера|дня|ночью|ночь)?""")
            pattern.find(lower)?.let { match ->
                val period = match.groupValues.getOrNull(1) ?: ""
                val adjustedHour = when (period) {
                    "вечера", "дня" -> if (num < 12) num + 12 else num
                    "утра" -> if (num == 12) 0 else num
                    "ночью", "ночь" -> if (num in 1..6) num else num + 12
                    else -> num
                }.coerceIn(0, 23)
                return TimeResult(LocalTime.of(adjustedHour, 0), text.replace(match.value, "", ignoreCase = true).trim(), period.isEmpty())
            }
        }

        // "утром" / "днём" / "вечером" / "ночью" — ambiguous
        val periods = listOf("утром" to LocalTime.of(9, 0), "днём" to LocalTime.of(13, 0),
            "вечером" to LocalTime.of(19, 0), "ночью" to LocalTime.of(23, 0))
        for ((word, defaultTime) in periods) {
            if (lower.contains(word)) {
                return TimeResult(defaultTime, removeWord(text, word), true)
            }
        }

        return TimeResult(null, text, false)
    }

    // === PRIORITY PARSING ===

    private data class PriorityResult(val priority: Priority, val cleanedText: String)

    private fun parsePriority(text: String): PriorityResult {
        val lower = text.lowercase()
        val priorityKeywords = mapOf(
            "срочно" to Priority.HIGH,
            "срочная" to Priority.HIGH,
            "срочный" to Priority.HIGH,
            "высокий приоритет" to Priority.HIGH,
            "важная" to Priority.HIGH,
            "важный" to Priority.HIGH,
            "важно" to Priority.HIGH,
            "низкий приоритет" to Priority.LOW,
            "неважно" to Priority.LOW,
            "не срочно" to Priority.LOW
        )
        for ((keyword, priority) in priorityKeywords) {
            if (lower.contains(keyword)) {
                return PriorityResult(priority, removeWord(text, keyword))
            }
        }
        return PriorityResult(Priority.NONE, text)
    }

    // === PROJECT PARSING ===

    private data class ProjectResult(val project: String?, val cleanedText: String)

    private fun parseProject(text: String): ProjectResult {
        val lower = text.lowercase()
        // "в проекте X" or "в проект X"
        val projectRegex = Regex("""(?:в\s+проекте|в\s+проект)\s+(\S+)""", RegexOption.IGNORE_CASE)
        projectRegex.find(text)?.let { match ->
            val projectName = match.groupValues[1].trim()
            return ProjectResult(projectName, text.replace(match.value, "", ignoreCase = true).trim())
        }
        return ProjectResult(null, text)
    }

    // === TAG PARSING ===

    private data class TagResult(val tags: List<String>, val cleanedText: String)

    private fun parseTags(text: String): TagResult {
        val tags = mutableListOf<String>()
        var cleaned = text

        // "тег работа" or "теги работа, дом"
        val tagRegex = Regex("""тег(?:и)?\s+(\S+(?:\s*,\s*\S+)*)""", RegexOption.IGNORE_CASE)
        tagRegex.find(text)?.let { match ->
            val tagList = match.groupValues[1].split(",").map { it.trim() }.filter { it.isNotEmpty() }
            tags.addAll(tagList)
            cleaned = cleaned.replace(match.value, "", ignoreCase = true).trim()
        }

        // Hashtag style: #работа
        val hashtagRegex = Regex("""#(\S+)""")
        hashtagRegex.findAll(text).forEach { match ->
            tags.add(match.groupValues[1].trim())
            cleaned = cleaned.replace(match.value, "", ignoreCase = true).trim()
        }

        return TagResult(tags.distinct(), cleaned)
    }

    // === RECURRENCE PARSING ===

    private data class RecurrenceResult(val recurrence: RecurrenceRule?, val cleanedText: String)

    private fun parseRecurrence(text: String): RecurrenceResult {
        val lower = text.lowercase()
        val recurrenceKeywords = mapOf(
            "каждый день" to RecurrenceRule.DAILY,
            "ежедневно" to RecurrenceRule.DAILY,
            "каждую неделю" to RecurrenceRule.WEEKLY,
            "еженедельно" to RecurrenceRule.WEEKLY,
            "каждый месяц" to RecurrenceRule.MONTHLY,
            "ежемесячно" to RecurrenceRule.MONTHLY,
            "каждый год" to RecurrenceRule.YEARLY,
            "ежегодно" to RecurrenceRule.YEARLY
        )
        for ((keyword, rule) in recurrenceKeywords) {
            if (lower.contains(keyword)) {
                return RecurrenceResult(rule, removeWord(text, keyword))
            }
        }
        return RecurrenceResult(null, text)
    }

    // === HELPERS ===

    private fun removeWord(text: String, vararg words: String): String {
        var result = text
        for (word in words) {
            result = result.replace(word, "", ignoreCase = true)
        }
        return result.replace(Regex("\\s+"), " ").trim()
    }

    private fun cleanTitle(text: String): String {
        var title = text.trim()
        // Убираем лишние пробелы
        title = title.replace(Regex("\\s+"), " ").trim()
        // Убираем начальные предлоги если title начинается с них и дальше есть текст
        val leadingPrepositions = listOf("в ", "на ", "что ", "надо ", "нужно ", "сделай ", "создай ", "добавь ", "поставь ")
        for (prep in leadingPrepositions) {
            if (title.lowercase().startsWith(prep) && title.length > prep.length + 3) {
                title = title.substring(prep.length).replaceFirstChar { it.uppercase() }
                break
            }
        }
        // Делаем первую букву заглавной
        if (title.isNotEmpty()) {
            title = title.replaceFirstChar { it.uppercase() }
        }
        return title
    }
}
