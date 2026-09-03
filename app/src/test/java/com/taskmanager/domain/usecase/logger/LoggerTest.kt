package com.taskmanager.domain.usecase.logger

import com.taskmanager.domain.logger.Logger
import com.taskmanager.test.TestLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Logger interface and TestLogger implementation.
 * Part of Phase 1 architecture stabilization - Task #0.1
 */
class LoggerTest {

    private lateinit var logger: TestLogger

    @Before
    fun setup() {
        logger = TestLogger()
    }

    @Test
    fun `TestLogger captures debug messages`() {
        logger.debug("TEST", "debug message")
        assertEquals(1, logger.logCount())
        assertTrue(logger.contains("[DEBUG]"))
        assertTrue(logger.contains("TEST"))
        assertTrue(logger.contains("debug message"))
    }

    @Test
    fun `TestLogger captures info messages`() {
        logger.info("TEST", "info message")
        assertEquals(1, logger.logCount())
        assertTrue(logger.contains("[INFO]"))
        assertTrue(logger.contains("info message"))
    }

    @Test
    fun `TestLogger captures warn messages`() {
        logger.warn("TEST", "warn message")
        assertEquals(1, logger.logCount())
        assertTrue(logger.contains("[WARN]"))
        assertTrue(logger.contains("warn message"))
    }

    @Test
    fun `TestLogger captures error messages without throwable`() {
        logger.error("TEST", "error message")
        assertEquals(1, logger.logCount())
        assertTrue(logger.contains("[ERROR]"))
        assertTrue(logger.contains("error message"))
    }

    @Test
    fun `TestLogger captures error messages with throwable`() {
        val exception = Exception("Test exception")
        logger.error("TEST", "error message", exception)
        assertEquals(1, logger.logCount())
        assertTrue(logger.contains("[ERROR]"))
        assertTrue(logger.contains("error message"))
        assertTrue(logger.contains("Test exception"))
    }

    @Test
    fun `TestLogger captures all log levels`() {
        logger.debug("TEST", "debug message")
        logger.info("TEST", "info message")
        logger.warn("TEST", "warn message")
        logger.error("TEST", "error message")

        assertEquals(4, logger.logCount())
        assertTrue(logger.contains("[DEBUG]"))
        assertTrue(logger.contains("[INFO]"))
        assertTrue(logger.contains("[WARN]"))
        assertTrue(logger.contains("[ERROR]"))
    }

    @Test
    fun `TestLogger clear removes all logs`() {
        logger.debug("TEST", "message 1")
        logger.info("TEST", "message 2")
        assertEquals(2, logger.logCount())

        logger.clear()
        assertEquals(0, logger.logCount())
    }

    @Test
    fun `TestLogger lastLog returns last message`() {
        logger.debug("TEST", "first")
        logger.info("TEST", "last")

        val last = logger.lastLog()
        assertTrue(last?.contains("last") == true)
        assertTrue(last?.contains("[INFO]") == true)
    }

    @Test
    fun `TestLogger allLogs returns concatenated messages`() {
        logger.debug("TEST", "message 1")
        logger.info("TEST", "message 2")

        val all = logger.allLogs()
        assertTrue(all.contains("message 1"))
        assertTrue(all.contains("message 2"))
        assertTrue(all.contains("\n"))
    }

    @Test
    fun `TestLogger containsMatch works with predicate`() {
        logger.debug("TEST", "debug message")
        logger.info("TEST", "info message")

        val hasDebug = logger.containsMatch { it.contains("[DEBUG]") }
        assertTrue(hasDebug)

        val hasError = logger.containsMatch { it.contains("[ERROR]") }
        assertTrue(!hasError)
    }

    @Test
    fun `Logger interface has all required methods`() {
        val logger: Logger = TestLogger()
        
        // This test verifies that the Logger interface has all the methods we need
        // by ensuring we can call them without compilation errors
        logger.debug("tag", "message")
        logger.info("tag", "message")
        logger.warn("tag", "message")
        logger.error("tag", "message")
        logger.error("tag", "message", Exception("test"))
    }
}
