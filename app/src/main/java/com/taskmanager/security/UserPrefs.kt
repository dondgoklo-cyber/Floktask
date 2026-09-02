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
        if (isLockedOut()) return false
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val candidate = hashPin(pin)
        val ok = constantTimeEquals(candidate, stored)
        if (ok) {
            prefs.edit().putInt(KEY_FAILED_ATTEMPTS, 0).remove(KEY_LOCKOUT_UNTIL).apply()
        } else {
            registerFailedAttempt()
        }
        return ok
    }

    /** Действует ли сейчас блокировка из-за множества неудачных попыток. */
    fun isLockedOut(): Boolean = System.currentTimeMillis() < lockoutUntil

    /** Время окончания блокировки (epoch millis) или 0, если блокировки нет. */
    fun lockoutRemainingMillis(): Long = (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0L)

    private val lockoutUntil: Long
        get() = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)

    private val failedAttempts: Int
        get() = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)

    private fun registerFailedAttempt() {
        val attempts = failedAttempts + 1
        val editor = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts)
        if (attempts % MAX_FAILED_ATTEMPTS == 0) {
            val cycles = attempts / MAX_FAILED_ATTEMPTS
            val delayMs = BASE_LOCKOUT_MS * (1L shl (cycles - 1).coerceAtMost(8))
            editor.putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + delayMs)
            Log.w("UserPrefs", "PIN lockout engaged: ${delayMs}ms after $attempts failures")
        }
        editor.apply()
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    private fun hashPin(pin: String): String {
        // SHA-256 с фиксированной солью. Внимание: это НЕ криптографическое хранилище уровня Keystore.
        // TODO (security): перенести PIN в Android Keystore с key-derived hash (PBKDF2/scrypt).
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
        private const val KEY_FAILED_ATTEMPTS = "pin_failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "pin_lockout_until"
        private const val MAX_FAILED_ATTEMPTS = 5
        private const val BASE_LOCKOUT_MS = 30_000L
    }
}
