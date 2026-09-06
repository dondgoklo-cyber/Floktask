package com.taskmanager.domain

import java.time.ZoneId

/**
 * Interface for providing time zone information.
 * Allows for dependency injection and testing of time zone behavior.
 * Part of Clean Architecture - Domain layer depends on abstractions, not concrete implementations.
 */
interface TimeZoneProvider {
    /**
     * Get the default time zone for the application.
     * This should be used instead of hardcoded ZoneId.of("UTC") or ZoneId.systemDefault().
     */
    val defaultZone: ZoneId
}

/**
 * Default implementation that uses UTC as the default time zone.
 * This ensures consistent behavior across all devices regardless of system settings.
 */
class DefaultTimeZoneProvider : TimeZoneProvider {
    override val defaultZone: ZoneId = ZoneId.of("UTC")
}
