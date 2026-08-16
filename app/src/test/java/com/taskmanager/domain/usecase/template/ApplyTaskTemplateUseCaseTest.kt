package com.taskmanager.domain.usecase.template

import com.taskmanager.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ApplyTaskTemplateUseCaseTest {

    private val useCase = ApplyTaskTemplateUseCase()

    @Test
    fun `instantiates task with template fields`() {
        val template = com.taskmanager.domain.model.TaskTemplate(
            name = "Review",
            title = "Weekly review",
            description = "Reflect",
            priority = Priority.HIGH
        )
        val task = useCase(template)
        assertEquals("Weekly review", task.title)
        assertEquals("Reflect", task.description)
        assertEquals(Priority.HIGH, task.priority)
    }

    @Test
    fun `instantiated task has no id and is not completed`() {
        val template = com.taskmanager.domain.model.TaskTemplate(
            name = "x", title = "t", priority = Priority.NONE
        )
        val task = useCase(template)
        assertEquals(null, task.id)
        assertEquals(false, task.isCompleted)
    }

    @Test
    fun `built-in templates are non-empty and named`() {
        val templates = ApplyTaskTemplateUseCase.builtIn
        assert(templates.isNotEmpty())
        templates.forEach { assertNotNull(it.name); assertNotNull(it.title) }
    }
}
