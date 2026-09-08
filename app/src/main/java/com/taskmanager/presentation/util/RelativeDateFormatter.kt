package com.taskmanager.presentation.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Форматирование дат в человекочитаемом виде на русском языке.
 * Паттерн Todoist/TickTick: сегодня/завтра/вчера → день недели → дата.
 */
object RelativeDateFormatter {

    private val ruLocale = Locale("ru")

    /**
     * Форматирует дату относительно сегодня.
     * @param date дата для форматирования
     * @param today сегодняшняя дата (по умолчанию LocalDate.now())
     * @return "Сегодня", "Завтра", "Вчера", "пт", "15 сент.", "15 сент. 2027"
     */
    fun formatDate(date: LocalDate, today: LocalDate = LocalDate.now()): String {
        val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(today, date)

        return when {
            daysDiff == 0L -> "Сегодня"
            daysDiff == 1L -> "Завтра"
            daysDiff == -1L -> "Вчера"
            daysDiff > 1L && daysDiff <= 6L -> {
                // Дни недели в пределах 6 дней
                date.format(DateTimeFormatter.ofPattern("EEEE", ruLocale).withLocale(ruLocale))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(ruLocale) else it.toString() }
                    .substring(0, 2) + "."
            }
            daysDiff < -1L && daysDiff >= -6L -> {
                // Прошедшие дни в пределах 6 дней
                date.format(DateTimeFormatter.ofPattern("EEEE", ruLocale).withLocale(ruLocale))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(ruLocale) else it.toString() }
                    .substring(0, 2) + "."
            }
            date.year == today.year -> {
                // В пределах текущего года: "15 сент."
                date.format(DateTimeFormatter.ofPattern("d MMM", ruLocale))
            }
            else -> {
                // Другой год: "15 сент. 2027"
                date.format(DateTimeFormatter.ofPattern("d MMM yyyy", ruLocale))
            }
        }
    }

    /**
     * Форматирует время в формате "18:00".
     */
    fun formatTime(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("HH:mm", ruLocale))
    }

    /**
     * Проверяет, является ли дата просроченной.
     * @param deadline дедлайн (Instant или LocalDate)
     * @param now текущий момент
     */
    fun isOverdue(deadline: Any, now: java.time.Instant = java.time.Instant.now()): Boolean {
        val nowDate = now.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        
        return when (deadline) {
            is java.time.Instant -> {
                val deadlineDate = deadline.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                deadlineDate < nowDate
            }
            is LocalDate -> deadline < nowDate
            else -> false
        }
    }

    /**
     * Проверка на просрочку для LocalDate с учётом времени.
     */
    fun isOverdue(deadline: LocalDate, now: LocalDate = LocalDate.now()): Boolean {
        return deadline < now
    }
}
