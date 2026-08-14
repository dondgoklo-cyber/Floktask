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
import dagger.hilt.android.AndroidEntryPoint

private const val PREFS_NAME = "taskmanager_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                val prefs = remember { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
                var onboardingDone by remember {
                    mutableStateOf(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
                }
                if (onboardingDone) {
                    NavGraph()
                } else {
                    OnboardingScreen(
                        onFinish = {
                            prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                            onboardingDone = true
                        }
                    )
                }
            }
        }
    }
}
