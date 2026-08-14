package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Tasks : Screen("tasks", R.string.tasks)
    data object Projects : Screen("projects", R.string.projects)
    data object Calendar : Screen("calendar", R.string.calendar)
    data object Profile : Screen("profile", R.string.profile)
    data object Location : Screen("location", R.string.location)
    data object AiAssistant : Screen("ai", R.string.ai_assistant)

    data object TaskEditNew : Screen("task/new", R.string.add_task)

    data object TaskEdit : Screen("task/{taskId}", R.string.add_task) {
        const val ARG_TASK_ID = "taskId"
        fun buildRoute(taskId: Long): String = "task/$taskId"
    }

    companion object {
        val bottomNavItems = listOf(Tasks, Projects, Calendar, Location, AiAssistant, Profile)
    }
}
