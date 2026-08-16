package com.taskmanager.integration

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * Integration test modelling a sync round-trip (issue 48: offline create →
 * online sync → conflict → verify data consistency).
 *
 * On main there's no cloud sync yet; this validates the *consistency*
 * contract that any sync must preserve: after a restore the data set
 * equals what was exported, and a last-write-wins conflict keeps the newer
 * revision.
 */
class BackupRestoreConsistencyTest {

    private class FakeTaskRepo {
        private val state = MutableStateFlow<Map<Long, Task>>(emptyMap())
        val all: Flow<Map<Long, Task>> = state.asStateFlow()

        suspend fun snapshot(): Map<Long, Task> = state.value

        suspend fun replaceAll(data: Map<Long, Task>) {
            state.value = data
        }

        suspend fun upsert(task: Task) {
            val id = task.id ?: ((state.value.keys.maxOrNull() ?: 0) + 1)
            state.value = state.value + (id to task.copy(id = id))
        }

        suspend fun get(id: Long): Task? = state.value[id]
    }

    private fun task(id: Long, title: String, updatedAt: Instant, completed: Boolean = false) =
        Task(id = id, title = title, isCompleted = completed, createdAt = updatedAt, updatedAt = updatedAt)

    @Test
    fun `round-trip preserves data consistency`() = runBlocking {
        val local = FakeTaskRepo()
        local.upsert(task(1, "Buy milk", Instant.parse("2026-01-15T10:00:00Z")))
        local.upsert(task(2, "Email", Instant.parse("2026-01-15T11:00:00Z"), completed = true))

        // "export" (serialize snapshot)
        val exported = local.snapshot()

        // "restore" into a fresh remote
        val remote = FakeTaskRepo()
        remote.replaceAll(exported)

        assertEquals(local.snapshot(), remote.snapshot())
        assertEquals(2, remote.snapshot().size)
    }

    @Test
    fun `last-write-wins conflict resolution keeps newer revision`() = runBlocking {
        val t1 = Instant.parse("2026-01-15T10:00:00Z")
        val t2 = Instant.parse("2026-01-15T12:00:00Z")

        val local = FakeTaskRepo()
        local.upsert(task(1, "old title", t1))

        val remote = FakeTaskRepo()
        remote.upsert(task(1, "newer title", t2)) // remote edit happened later

        // merge: for each id, keep the max(updatedAt)
        val merged = (local.snapshot().values + remote.snapshot().values)
            .groupBy { it.id!! }
            .mapValues { (_, versions) -> versions.maxBy { it.updatedAt } }
            .values
            .associateBy { it.id!! }

        assertEquals("newer title", merged[1L]?.title)
        assertNotEquals("old title", merged[1L]?.title)
    }

    @Test
    fun `offline creates survive a restore`() = runBlocking {
        val local = FakeTaskRepo()
        local.upsert(task(1, "offline task", Instant.parse("2026-01-15T09:00:00Z")))

        val exported = local.snapshot()
        val restored = FakeTaskRepo()
        restored.replaceAll(exported)

        assertTrue(restored.snapshot().isNotEmpty())
        assertEquals("offline task", restored.get(1)?.title)
    }
}
