package com.taskmanager.domain.logger

/**
 * Logger abstraction to remove direct android.util.Log dependencies from domain layer.
 * This allows domain layer to be pure Kotlin with no Android dependencies.
 */
interface Logger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}
