package com.taskmanager.domain.usecase.template

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskTemplate
import javax.inject.Inject

/**
 * Instantiates a concrete [Task] from a [TaskTemplate] (issue 29).
 * The resulting task has template-provided fields but no deadline/id yet.
 */
class ApplyTaskTemplateUseCase @Inject constructor() {

    operator fun invoke(template: TaskTemplate): Task = Task(
        title = template.title,
        description = template.description,
        priority = template.priority
        // deadline, projectId, tags are resolved by the caller (NLP/scheduling)
    )

    companion object {
        /** Built-in starter templates. */
        val builtIn = listOf(
            TaskTemplate(
                name = "Weekly review",
                title = "Weekly review",
                description = "Reflect on the past week, plan the next one.",
                priority = Priority.MEDIUM
            ),
            TaskTemplate(
                name = "Morning routine",
                title = "Morning routine",
                description = "Plan the day, top 3 priorities.",
                priority = Priority.LOW
            ),
            TaskTemplate(
                name = "Client onboarding",
                title = "Onboard new client",
                description = "Send welcome email, schedule kickoff call, create project.",
                priority = Priority.HIGH
            )
        )
    }
}
