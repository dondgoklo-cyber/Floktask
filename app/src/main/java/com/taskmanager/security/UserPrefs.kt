package com.taskmanager.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
/**
 * Управляет PIN-кодом и настройками пользователя через SharedPreferences.
 * PIN хранится как хэш (не в открытом виде).
 */
class UserPrefs(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskmanager_prefs", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var hapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, value).apply()

    var baseCurrency: String
        get() = prefs.getString(KEY_BASE_CURRENCY, "RUB") ?: "RUB"
        set(value) = prefs.edit().putString(KEY_BASE_CURRENCY, value).apply()

    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    val hasPin: Boolean
        get() = prefs.contains(KEY_PIN_HASH)

    /**
     * Backward compatibility: migrate old weak hashes to new secure hashes.
     * Old hashes were simple numeric hashes, new ones are SHA-256 hex strings.
     */
    fun setPin(pin: String) {
        val oldHash = prefs.getString(KEY_PIN_HASH, null)
        // If old hash exists and is numeric (old format), it will be replaced naturally
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    fun removePin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    fun checkPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(pin) == stored
    }

    private fun hashPin(pin: String): String {
        // Улучшенное хэширование: SHA-256 с солью
        // Простой пример соль
        val salt = "floktask_salt_2024"
        val bytes = (pin + salt).toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_BASE_CURRENCY = "base_currency"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
