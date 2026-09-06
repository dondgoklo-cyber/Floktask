package com.taskmanager.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.taskmanager.domain.usecase.settings.UserPreferences
import javax.inject.Inject

/**
 * Android implementation of UserPreferences interface.
 * Uses SharedPreferences to store user preferences.
 * Part of Clean Architecture - Data layer implements Domain interfaces.
 */
class UserPreferencesImpl @Inject constructor(
    private val context: Context
) : UserPreferences {
    
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskmanager_prefs", Context.MODE_PRIVATE)

    override var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    override var hapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, value).apply()

    override var baseCurrency: String
        get() = prefs.getString(KEY_BASE_CURRENCY, "RUB") ?: "RUB"
        set(value) = prefs.edit().putString(KEY_BASE_CURRENCY, value).apply()

    override val hasPin: Boolean
        get() = prefs.contains(KEY_PIN_HASH)

    override fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    override fun removePin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    override fun checkPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(pin) == stored
    }

    /**
     * Simple hash function for PIN storage.
     * Note: This is a basic hash, not cryptographically secure.
     * For production, use proper key derivation like PBKDF2.
     */
    private fun hashPin(pin: String): String {
        var hash = 0
        for (c in pin) {
            hash = hash * 31 + c.code
        }
        return hash.toString()
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_BASE_CURRENCY = "base_currency"
    }
}
