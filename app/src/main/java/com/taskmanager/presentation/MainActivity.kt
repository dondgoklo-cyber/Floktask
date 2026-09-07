package com.taskmanager.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.usecase.settings.UserPreferences
import com.taskmanager.haptic.HapticManager
import com.taskmanager.haptic.LocalHapticManager
import com.taskmanager.notification.AlarmScheduler
import com.taskmanager.presentation.navigation.NavGraph
import com.taskmanager.presentation.screens.onboarding.OnboardingScreen
import com.taskmanager.presentation.theme.LocalThemeController
import com.taskmanager.presentation.theme.TaskManagerTheme
import com.taskmanager.presentation.theme.ThemeController
import com.taskmanager.security.PinMode
import com.taskmanager.security.PinScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val PREFS_NAME = "taskmanager_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var hapticManager: HapticManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Reschedule all reminders on app startup to handle device reboots
        try {
            alarmScheduler.rescheduleAllReminders()
            logger.debug("MainActivity", "Reminders rescheduled successfully")
        } catch (e: Exception) {
            logger.error("MainActivity", "Error rescheduling reminders", e)
        }
        
        setContent {
            // Read theme mode from preferences and track it as state
            var themeMode by remember { mutableStateOf(userPreferences.themeMode) }

            // Determine if dark theme should be used
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            // Provide HapticManager and ThemeController to all composables
            CompositionLocalProvider(
                LocalHapticManager provides hapticManager,
                LocalThemeController provides ThemeController(themeMode) { newMode ->
                    userPreferences.themeMode = newMode
                    themeMode = newMode
                }
            ) {
                TaskManagerTheme(darkTheme = isDark) {
                    val prefs = remember { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
                    var onboardingDone by remember {
                        mutableStateOf(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
                    }
                    var pinUnlocked by remember { mutableStateOf(!userPreferences.hasPin) }

                    when {
                        !onboardingDone -> {
                            OnboardingScreen(
                                onFinish = {
                                    prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                                    onboardingDone = true
                                }
                            )
                        }
                        userPreferences.hasPin && !pinUnlocked -> {
                            PinScreen(
                                mode = PinMode.ENTER,
                                userName = userPreferences.userName,
                                userPrefs = userPreferences,
                                onSuccess = { pinUnlocked = true }
                            )
                        }
                        else -> {
                            NavGraph()
                        }
                    }
                }
            }
        }
    }
}