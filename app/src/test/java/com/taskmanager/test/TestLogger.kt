package com.taskmanager.test

import com.taskmanager.domain.logger.Logger

/**
 * Mock implementation of Logger for unit testing.
 * Captures all log messages for verification in tests.
 * 
 * Part of Phase 1 architecture stabilization - Task #0.1
 */
class TestLogger : Logger {
    val logs = mutableListOf<String>()
    
    override fun debug(tag: String, message: String) {
        logs.add("[DEBUG] $tag: $message")
    }
    
    override fun info(tag: String, message: String) {
        logs.add("[INFO] $tag: $message")
    }
    
    override fun warn(tag: String, message: String) {
        logs.add("[WARN] $tag: $message")
    }
    
    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            logs.add("[ERROR] $tag: $message\n${throwable.stackTraceToString()}")
        } else {
            logs.add("[ERROR] $tag: $message")
        }
    }
    
    /**
     * Clears all captured logs.
     */
    fun clear() = logs.clear()
    
    /**
     * Checks if any log message contains the expected string.
     */
    fun contains(expected: String): Boolean = logs.any { it.contains(expected) }
    
    /**
     * Checks if any log message matches the expected pattern.
     */
    fun containsMatch(predicate: (String) -> Boolean): Boolean = logs.any(predicate)
    
    /**
     * Gets the count of log messages.
     */
    fun logCount(): Int = logs.size
    
    /**
     * Gets the last log message.
     */
    fun lastLog(): String? = logs.lastOrNull()
    
    /**
     * Gets all log messages as a single string joined by newlines.
     */
    fun allLogs(): String = logs.joinToString("\n")
}
