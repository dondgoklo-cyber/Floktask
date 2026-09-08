package com.taskmanager.presentation.screens.tasks

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class ParsedQuickTask(
    val title: String,
    val deadlineDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val durationMinutes: Long? = null,
    val projectName: String? = null,
    val tags: List<String> = emptyList(),
    val priority: Priority? = null
)

/**
 * Расширенный парсер Quick Add (Rich Quick Add).
 * Распознаёт:
 * - Даты: «завтра», «сегодня», «послезавтра», дни недели
 * - Время: ЧЧ:ММ, «в N»
 * - Длительность: «на час», «на N (часов/мин)»
 * - Проект: #НазваниеПроекта (без пробелов)
 * - Теги: @тег1 @тег2
 * - Приоритет: p1 (высокий), p2 (средний), p3 (низкий)
 */
object QuickAddParser {

    fun parse(input: String): ParsedQuickTask {
        var text = input.trim()
        val today = LocalDate.now()

        var date: LocalDate? = null
        var time: LocalTime? = null
        var duration: Long? = null
        var projectName: String? = null
        val tags = mutableListOf<String>()
        var priority: Priority? = null

        // Проект: #Название (одно слово, без пробелов)
        val projectRegex = Regex("#(\\S+)")
        projectRegex.findAll(text).toList().forEach { match ->
            projectName = match.groupValues[1]
            text = text.replace(match.value, "")
        }

        // Теги: @тег (одно слово, без пробелов)
        val tagRegex = Regex("@(\\S+)")
        tagRegex.findAll(text).toList().forEach { match ->
            tags.add(match.groupValues[1])
            text = text.replace(match.value, "")
        }

        // Приоритет: p1/p2/p3 как отдельное слово
        val priorityRegex = Regex("\\bp([1-3])\\b", RegexOption.IGNORE_CASE)
        priorityRegex.find(text)?.let { match ->
            priority = when (match.groupValues[1]) {
                "1" -> Priority.HIGH
                "2" -> Priority.MEDIUM
                "3" -> Priority.LOW
                else -> null
            }
            text = text.replace(match.value, "")
        }

        // Дата
        if (text.contains("сегодня")) {
            date = today
            text = text.replace("сегодня", "").trim()
        }
        if (text.contains("послезавтра")) {
            date = today.plusDays(2)
            text = text.replace("послезавтра", "").trim()
        }
        if (text.contains("завтра")) {
            date = today.plusDays(1)
            text = text.replace("завтра", "").trim()
        }

        // Дни недели
        val weekdays = mapOf(
            "понедельник" to 1, "вторник" to 2, "среда" to 3, "четверг" to 4,
            "пятница" to 5, "суббота" to 6, "воскресенье" to 7
        )
        for ((word, dayOfWeek) in weekdays) {
            if (text.contains(word)) {
                val todayDow = today.dayOfWeek.value
                var diff = dayOfWeek - todayDow
                if (diff <= 0) diff += 7
                date = today.plusDays(diff.toLong())
                text = text.replace(word, "").trim()
                break
            }
        }

        // Время ЧЧ:ММ
        val timeRegex = Regex("\\b(\\d{1,2}):(\\d{2})\\b")
        timeRegex.find(text)?.let { match ->
            val h = match.groupValues[1].toIntOrNull()
            val m = match.groupValues[2].toIntOrNull()
            if (h != null && m != null && h in 0..23 && m in 0..59) {
                time = LocalTime.of(h, m)
                text = text.replace(match.value, "").trim()
            }
        }

        // Время в формате "в 15" или "в 15:00"
        val hourRegex = Regex("\\bв\\s+(\\d{1,2})\\b")
        if (time == null) {
            hourRegex.find(text)?.let { match ->
                val h = match.groupValues[1].toIntOrNull()
                if (h != null && h in 0..23) {
                    time = LocalTime.of(h, 0)
                    text = text.replace(match.value, "").trim()
                }
            }
        }

        // Длительность: "на час", "на 2 часа", "на 30 мин", "на полчаса"
        if (text.contains("на час")) {
            duration = 60
            text = text.replace("на час", "").trim()
        }
        if (text.contains("на полчаса")) {
            duration = 30
            text = text.replace("на полчаса", "").trim()
        }
        val durationRegex = Regex("на\\s+(\\d+)\\s*(час|часа|часов|мин|минут)")
        durationRegex.find(text)?.let { match ->
            val value = match.groupValues[1].toLongOrNull()
            val unit = match.groupValues[2]
            if (value != null) {
                duration = when {
                    unit.startsWith("час") -> value * 60
                    unit.startsWith("мин") -> value
                    else -> null
                }
                text = text.replace(match.value, "").trim()
            }
        }

        // Очистка: лишние пробелы, "в", предлоги
        text = text
            .replace(Regex("\\s+"), " ")
            .replace(Regex("^в\\s+", RegexOption.IGNORE_CASE), "")
            .trim()
            .removeSuffix("в")
            .trim()

        return ParsedQuickTask(
            title = text.ifBlank { input.trim() },
            deadlineDate = date,
            startTime = time,
            durationMinutes = duration,
            projectName = projectName,
            tags = tags,
            priority = priority
        )
    }
}
