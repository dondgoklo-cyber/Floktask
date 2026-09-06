package com.taskmanager.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.taskmanager.domain.usecase.settings.UserPreferences
import javax.inject.Inject

/**
 * Implementation of UserPreferences interface for Android.
 * Uses SharedPreferences to store user preferences.
 */
class UserPreferencesImpl @Inject constructor(
    private val context: Context
) : UserPreferences {
    
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskmanager_prefs", Context.MODE_PRIVATE)

    override val baseCurrency: String
        get() = prefs.getString(KEY_BASE_CURRENCY, "RUB") ?: "RUB"

    companion object {
        private const val KEY_BASE_CURRENCY = "base_currency"
    }
}
