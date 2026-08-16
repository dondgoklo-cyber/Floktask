package com.taskmanager.domain.model

/**
 * Предустановленные шаблоны задач для быстрого создания.
 */
data class TaskTemplate(
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.NONE,
    val durationMinutes: Long? = null,
    val pomodoroEstimate: Int? = null,
    val icon: String? = null
)

/**
 * Каталог предустановленных шаблонов.
 * Пользователь может выбрать шаблон → задача создаётся с предзаполненными полями.
 */
object TaskTemplates {
    
    val all = listOf(
        TaskTemplate(
            title = "Звонок",
            description = null,
            priority = Priority.MEDIUM,
            durationMinutes = 15,
            pomodoroEstimate = 1,
            icon = "phone"
        ),
        TaskTemplate(
            title = "Встреча",
            description = null,
            priority = Priority.HIGH,
            durationMinutes = 60,
            pomodoroEstimate = 2,
            icon = "people"
        ),
        TaskTemplate(
            title = "Подготовить отчёт",
            description = null,
            priority = Priority.HIGH,
            durationMinutes = 120,
            pomodoroEstimate = 4,
            icon = "description"
        ),
        TaskTemplate(
            title = "Презентация",
            description = null,
            priority = Priority.HIGH,
            durationMinutes = 90,
            pomodoroEstimate = 3,
            icon = "slideshow"
        ),
        TaskTemplate(
            title = "Проверить почту",
            description = null,
            priority = Priority.LOW,
            durationMinutes = 30,
            pomodoroEstimate = 1,
            icon = "mail"
        ),
        TaskTemplate(
            title = "Тренировка",
            description = null,
            priority = Priority.MEDIUM,
            durationMinutes = 60,
            pomodoroEstimate = 2,
            icon = "fitness"
        ),
        TaskTemplate(
            title = "Прочитать книгу",
            description = null,
            priority = Priority.LOW,
            durationMinutes = 30,
            pomodoroEstimate = 1,
            icon = "book"
        ),
        TaskTemplate(
            title = "Планирование дня",
            description = null,
            priority = Priority.MEDIUM,
            durationMinutes = 15,
            pomodoroEstimate = 1,
            icon = "event_note"
        ),
        TaskTemplate(
            title = "Идея",
            description = "Записать идею",
            priority = Priority.LOW,
            durationMinutes = 10,
            pomodoroEstimate = 1,
            icon = "lightbulb"
        ),
        TaskTemplate(
            title = "Купить",
            description = null,
            priority = Priority.MEDIUM,
            durationMinutes = 30,
            pomodoroEstimate = 1,
            icon = "shopping_cart"
        )
    )
}
