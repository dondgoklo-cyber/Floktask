package com.taskmanager.domain.usecase.search

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.SearchResultType
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class GlobalSearchUseCaseTest {

    private val useCase = GlobalSearchUseCase()

    private fun task(id: Long, title: String, desc: String? = null) =
        Task(id = id, title = title, description = desc, createdAt = Instant.now(), updatedAt = Instant.now())

    private fun project(id: Long, title: String) = Project(id = id, title = title)

    private fun tag(id: Long, name: String) = Tag(id = id, name = name)

    @Test
    fun `empty query returns no results`() {
        assertTrue(useCase("", listOf(task(1, "x")), emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun `matches across all entity types`() {
        val results = useCase(
            "buy",
            tasks = listOf(task(1, "Buy milk"), task(2, "Email")),
            projects = listOf(project(3, "Buying guide")),
            tags = listOf(tag(4, "bought"))
        )
        assertEquals(3, results.size)
        assertEquals(SearchResultType.TASK, results[0].type)
        assertEquals(SearchResultType.PROJECT, results[1].type)
        assertEquals(SearchResultType.TAG, results[2].type)
    }

    @Test
    fun `case-insensitive matching`() {
        val results = useCase(
            "BUY",
            tasks = listOf(task(1, "buy milk")),
            projects = emptyList(),
            tags = emptyList()
        )
        assertEquals(1, results.size)
    }

    @Test
    fun `no matches returns empty`() {
        assertTrue(
            useCase("zzz", listOf(task(1, "x")), listOf(project(2, "y")), listOf(tag(3, "w"))).isEmpty()
        )
    }

    @Test
    fun `subtitle carries description for tasks`() {
        val results = useCase("t", listOf(task(1, "task", "my desc")), emptyList(), emptyList())
        assertEquals("my desc", results[0].subtitle)
    }
}
