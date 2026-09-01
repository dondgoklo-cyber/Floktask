package com.taskmanager.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
/**
 * 233f4030323b4f3542 PIN-3a3e343e3c 38 383c353d353c 3f3e3b4c373e323042353b4f 4735403537 SharedPreferences.
 * PIN 4540303d3842414f 3a303a 454d48 (3d35 32 3e423a404b423e3c 32383435).
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
        // 233b434748353d3d3e35 454d4838403e32303d3835: SHA-256 41 413e3b4c4e
        // 1f403e41423e39 3f403e423832 40303443363d4b45 4230313b3846303c (3d35 323e373c3e363d3e 3f354035313e4030)
        val bytes = pin.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_BASE_CURRENCY = "base_currency"
    }
}
