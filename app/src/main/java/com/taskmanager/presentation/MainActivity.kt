package com.taskmanager.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.taskmanager.presentation.navigation.NavGraph
import com.taskmanager.presentation.screens.onboarding.OnboardingScreen
import com.taskmanager.presentation.theme.TaskManagerTheme
import com.taskmanager.security.PinMode
import com.taskmanager.security.PinScreen
import com.taskmanager.security.UserPrefs
import dagger.hilt.android.AndroidEntryPoint

private const val PREFS_NAME = "taskmanager_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userPrefs = remember { UserPrefs(this@MainActivity) }
            val darkTheme = remember { mutableStateOf(userPrefs.darkTheme) }
            
            TaskManagerTheme(darkTheme = darkTheme.value) {
                val prefs = remember { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
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
