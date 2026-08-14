package com.taskmanager.security

import android.content.Context
import android.content.SharedPreferences
/**
 * Управляет PIN-кодом и именем пользователя через SharedPreferences.
 * PIN хранится как хэш (не в открытом виде).
 */
class UserPrefs(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskmanager_prefs", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    val hasPin: Boolean
        get() = prefs.contains(KEY_PIN_HASH)

    fun setPin(pin: String) {
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
        // Простой хэш — достаточен для локального PIN (не криптография)
        var hash = 0
        for (c in pin) {
            hash = hash * 31 + c.code
        }
        return hash.toString()
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_USER_NAME = "user_name"
    }
}
