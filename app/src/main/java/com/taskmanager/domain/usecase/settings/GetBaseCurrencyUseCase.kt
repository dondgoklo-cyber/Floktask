package com.taskmanager.domain.usecase.settings

import com.taskmanager.domain.logger.Logger
import javax.inject.Inject

/**
 * Interface for user preferences.
 * Part of Clean Architecture - Presentation layer depends only on abstractions, not concrete implementations.
 * 
 * This interface abstracts all user preference operations including PIN management,
 * user name, haptic feedback settings, and base currency.
 */
interface UserPreferences {
    /** User's display name */
    var userName: String
    
    /** Whether haptic feedback is enabled */
    var hapticEnabled: Boolean
    
    /** Base currency for finance operations (e.g., "RUB", "USD", "EUR") */
    var baseCurrency: String

    /** Theme mode: "SYSTEM" (follow system), "LIGHT", or "DARK" */
    var themeMode: String
    
    /** Whether a PIN has been set */
    val hasPin: Boolean
    
    /**
     * Set a new PIN for app access.
     * @param pin The PIN to set (4 digits)
     */
    fun setPin(pin: String)
    
    /**
     * Remove the currently set PIN.
     */
    fun removePin()
    
    /**
     * Check if the provided PIN matches the stored PIN.
     * @param pin The PIN to check
     * @return true if the PIN matches, false otherwise
     */
    fun checkPin(pin: String): Boolean
}

/**
 * Use case for retrieving base currency from user preferences.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not direct Android dependencies.
 */
class GetBaseCurrencyUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val logger: Logger
) {
    operator fun invoke(): String = userPreferences.baseCurrency
}