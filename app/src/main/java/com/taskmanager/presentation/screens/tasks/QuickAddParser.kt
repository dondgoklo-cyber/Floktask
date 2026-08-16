package com.taskmanager.presentation.screens.tasks

import com.taskmanager.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class ParsedQuickTask(
    val title: String,
    val deadlineDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val durationMinutes: Long? = null
)

/**
 * Ручной парсер Quick Add (без NLP).
 * Распознаёт ключевые слова: «завтра», «сегодня», «послезавтра»,
 * время в формате ЧЧ:ММ, длительность: «на час», «на 2 часа», «на 30 мин».
 */
object QuickAddParser {

    fun parse(input: String): ParsedQuickTask {
        var text = input.trim()
        val today = LocalDate.now()

        var date: LocalDate? = null
        var time: LocalTime? = null
        var duration: Long? = null

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

        // Длительность: "на час", "на 2 часа", "на 30 мин", "на полтора часа"
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
            durationMinutes = duration
        )
    }
}
