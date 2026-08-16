package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Tasks : Screen("tasks", R.string.tasks)
    data object Projects : Screen("projects", R.string.projects)
    data object Timeline : Screen("timeline", R.string.timeline)

    companion object {
        val bottomNavItems = listOf(Tasks, Projects, Timeline)
    }
}
