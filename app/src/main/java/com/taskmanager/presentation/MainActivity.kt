package com.taskmanager.presentation
import com.taskmanager.domain.logger.Logger

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.taskmanager.notification.AlarmScheduler
import com.taskmanager.presentation.navigation.NavGraph
import com.taskmanager.presentation.screens.onboarding.OnboardingScreen
import com.taskmanager.presentation.theme.TaskManagerTheme
import com.taskmanager.security.PinMode
import com.taskmanager.security.PinScreen
import com.taskmanager.security.UserPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val PREFS_NAME = "taskmanager_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

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
            TaskManagerTheme {
                val prefs = remember { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
                val userPrefs = remember { UserPrefs(this@MainActivity) }
                var onboardingDone by remember {
                    mutableStateOf(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
                }
                var pinUnlocked by remember { mutableStateOf(!userPrefs.hasPin) }

                when {
                    !onboardingDone -> {
                        OnboardingScreen(
                            onFinish = {
                                prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                                onboardingDone = true
                            }
                        )
                    }
                    userPrefs.hasPin && !pinUnlocked -> {
                        PinScreen(
                            mode = PinMode.ENTER,
                            userName = userPrefs.userName,
                            userPrefs = userPrefs,
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
